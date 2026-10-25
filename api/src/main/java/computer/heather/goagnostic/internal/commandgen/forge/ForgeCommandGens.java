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

        public FORGE_1_12(String implName) throws IOException {
            super(
                ProcessorUtils.readAllBytes(FORGE_1_12.class.getResourceAsStream("/templates/GoAgnosticTemplate-" + implName + "/goagnosticmodgroup/goagnosticmodid/goagnosticcommandnamecommand.class.bin"))
            );
            this.implName = implName;
            //TODO Auto-generated constructor stub
        }

        @Override
        public void executes(ProcessingEnvironment processingEnv, Element element, Executes executesAnnotation,
                Target target, CommandGen commandGen, TypeElement type, String className) throws IOException {

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

            //Create the method
            createMethod(Opcodes.ACC_PUBLIC, "func_184882_a", "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/command/ICommandSender;)Z");

            //Then inject into it
            String toInject = new String(ProcessorUtils.readAllBytes(CommandProcessor.class.getResourceAsStream("/commandgen/" + target.getImplName() + "/requires")))
                .replace("REQUIREOWNER", className)
                .replace("REQUIREFUNC", element.getSimpleName());
                
            injectIntoMethod(0, "func_184882_a", toInject);
        }

        //1.12 inherently doesn't really support these. We'll still support them when parsing but we can't integrate them with actual commands, instead we have to parse arguments at command execution time
        //So all of these are just.. nothing
        @Override
        public void subcommandStart(String name, Target target, CommandGen commandGen, String className)
                throws IOException {
        }

        @Override
        public void subcommandEnd(Target target, CommandGen commandGen, String className) throws IOException {
        }

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
