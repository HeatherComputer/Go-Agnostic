package computer.heather.goagnostic.internal.commandgen.forge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import computer.heather.goagnostic.api.annotations.Mod;
import computer.heather.goagnostic.api.annotations.Command.Argument;
import computer.heather.goagnostic.api.annotations.Command.Executes;
import computer.heather.goagnostic.api.annotations.Command.Require;
import computer.heather.goagnostic.api.annotations.Mod.Target;
import computer.heather.goagnostic.internal.commandgen.CommandGen;
import computer.heather.goagnostic.internal.util.ProcessorUtils;
import computer.heather.goagnostic.internal.util.StringUtils;

import org.objectweb.asm.Opcodes;
import computer.heather.goagnostic.internal.annotationprocessors.CommandProcessor;
import computer.heather.goagnostic.internal.annotationprocessors.ProcessorBase;

public class ForgeCommandGens {
    
    public static class FORGE_1_12 extends CommandGen {

        public static final List<String> generatedClasses = new ArrayList<String>();
        private final String implName;
        private final String qualifiedName;
        private FORGE_1_12 subCommand;
        private String subCommandName;
        private int nest = 0;

        public FORGE_1_12(String implName, String qualifiedName) throws IOException {
            super(
                ProcessorUtils.readAllBytes(FORGE_1_12.class.getResourceAsStream("/templates/GoAgnosticTemplate-" + implName + "/goagnosticmodgroup/goagnosticmodid/goagnosticcommandclassname.class.bin"))
            );
            this.implName = implName;
            this.qualifiedName = qualifiedName;
        }

        @Override
        public void executes(ProcessingEnvironment processingEnv, Element element, Executes executesAnnotation,
                Target target, CommandGen commandGen, TypeElement type, String className) throws IOException {
                
            if (nest >= 1) {
                //If we're currently processing a subcommand we'll pass it onto that
                subCommand.executes(processingEnv, element, executesAnnotation, target, commandGen, type, className);
                return;
            }

            //Create the method
            //We use a custom execute method that gets called by the super class
            createMethod(Opcodes.ACC_PUBLIC, "gaexecute", "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/command/ICommandSender;[Ljava/lang/String;)V");

            //Then inject into it
            String toInject = new String(ProcessorUtils.readAllBytes(CommandProcessor.class.getResourceAsStream("/commandgen/" + target.getImplName() + "/executes")))
                .replace("EXECUTEOWNER", className)
                .replace("EXECUTEFUNC", element.getSimpleName());
                
            injectIntoMethod(0, "gaexecute", toInject);
        }

        @Override
        public void requires(ProcessingEnvironment processingEnv, Element element, Require requiresAnnotation,
                Target target, CommandGen commandGen, TypeElement type, String className) throws IOException {

            if (nest >= 1) {
                //If we're currently processing a subcommand we'll pass it onto that
                subCommand.requires(processingEnv, element, requiresAnnotation, target, commandGen, type, className);
                return;
            }

            //Create the method
            createMethod(Opcodes.ACC_PUBLIC, "func_184882_a", "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/command/ICommandSender;)Z");

            //Then inject into it
            String toInject = new String(ProcessorUtils.readAllBytes(CommandProcessor.class.getResourceAsStream("/commandgen/" + target.getImplName() + "/requires")))
                .replace("REQUIREOWNER", className)
                .replace("REQUIREFUNC", element.getSimpleName());
                
            injectIntoMethod(0, "func_184882_a", toInject);
        }

        @Override
        public void subcommandStart(String name, Target target, CommandGen commandGen, String className)
                throws IOException {

            //Increase our nest level
            nest++;

            if (nest <= 1) {
                //Not currently processing a subcommand
                subCommandName = qualifiedName + "$" + name + "command";
                subCommand = new FORGE_1_12(implName, subCommandName);
            }
            //Or, we are processing a subcommand and IT gets this subcommand
            else {
                subCommand.subcommandStart(name, target, commandGen, className);
            }

        }

        @Override
        public void subcommandEnd(String name, Target target, CommandGen commandGen, String className, Mod mod, ProcessingEnvironment processingEnv) throws IOException {
            //Nest level is only 1 if our current subcommand has zero subcommands
            if (nest == 1) {
                String toInject = new String(ProcessorUtils.readAllBytes(CommandProcessor.class.getResourceAsStream("/commandgen/" + target.getImplName() + "/subcommand")))
                    .replace("SUBCOMMANDNAME", subCommandName);
                injectIntoMethod(2, "<init>", toInject);



                File buildDir = ProcessorUtils.getBuildDir(processingEnv);
                File outputDir = new File(buildDir, "goagnostic/" + target.getImplName());

                HashMap<String, String> map = new HashMap<>();
                map.put("goagnosticcommandname", name);
                
                String commandClassName = subCommandName.replace(StringUtils.lowerWithSlashes(mod
                        .group() + "." + mod.modid() + "."), "");
                map.put("goagnosticcommandclassname", commandClassName);
                
                byte[] remapped = ProcessorUtils.remapFile(subCommand.getRemappedBytes(), mod, "", map);

                File outputFile = new File(outputDir, qualifiedName + "$" + name + "command.class");
                        
                if (!outputFile.getParentFile().exists()) outputFile.getParentFile().mkdirs();
                    
                FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                fileOutputStream.write(remapped);
                fileOutputStream.flush();
                fileOutputStream.close();

            }
            else {
                subCommand.subcommandEnd(name, target, commandGen, className, mod, processingEnv);
            }
            nest--;
        }

        //1.12 inherently doesn't really support these. We'll still support them when parsing but we can't integrate them with actual commands, instead we have to parse arguments at command execution time
        //So all of these are just.. nothing

        @Override
        public void argumentstart(Target target, CommandGen commandGen, Argument argument) throws IOException {
        }

        @Override
        public void argumentEnd(Target target, CommandGen commandGen, Argument argument, boolean lastArg)
                throws IOException {
        }
        
        @Override 
        public void finalise(Target target, String className) {
            generatedClasses.add(className);

            ProcessorBase.addFinaliseTask(target.getImplName() + "cmdfinalise", this::finaliseTask);

        }

        private void finaliseTask(File buildDir, Mod mod) {
            try {
                CommandGen dummyGen = new CommandGen.DUMMY(ProcessorUtils.readAllBytes(FORGE_1_12.class.getResourceAsStream
                    ("/templates/GoAgnosticTemplate-" + implName + "/goagnosticmodgroup/goagnosticmodid/util/CommandHandler$Registrar.class.bin")));
            
                //First up we pop the 0 array size marker, and replace it with the correct size
                int count = 1;
                dummyGen.injectIntoMethod(count++, "getCommands", "pop");
                dummyGen.injectIntoMethod(count++, "getCommands", "iconst " + generatedClasses.size());
                count++;

                //Next we populate the array
                int i = 0;
                for (String clasz : generatedClasses) {
                    dummyGen.injectIntoMethod(count++, "getCommands", "dup");
                    dummyGen.injectIntoMethod(count++, "getCommands", "iconst " + i);
                    i++;
                    dummyGen.injectIntoMethod(count++, "getCommands", "class L" + clasz.substring(0, clasz.length() - 6) + ";");
                    dummyGen.injectIntoMethod(count++, "getCommands", "aastore");
                }

                //Now write the class file
                
                File outputDir = new File(buildDir, "goagnostic/" + implName);

                byte[] remapped = ProcessorUtils.remapFile(dummyGen.getRemappedBytes(), mod, "", new HashMap<>());

                File outputFile = new File(outputDir, StringUtils.lowerWithSlashes(mod
                        .group() + "." + mod.modid() + ".") + "util/CommandHandler$Registrar.class");
                        
                    if (!outputFile.getParentFile().exists()) outputFile.getParentFile().mkdirs();
                        
                    FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                    fileOutputStream.write(remapped);
                    fileOutputStream.flush();
                    fileOutputStream.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
                
        }
        
    }

}
