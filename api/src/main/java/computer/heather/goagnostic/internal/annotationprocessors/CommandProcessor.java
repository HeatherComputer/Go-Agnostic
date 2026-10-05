package computer.heather.goagnostic.internal.annotationprocessors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import computer.heather.goagnostic.api.annotations.Command;
import computer.heather.goagnostic.api.annotations.Mod;
import computer.heather.goagnostic.api.annotations.Command.Executes;
import computer.heather.goagnostic.api.annotations.Mod.Target;
import computer.heather.goagnostic.internal.commandgen.CommandGen;
import computer.heather.goagnostic.internal.util.ProcessorUtils;
import computer.heather.goagnostic.internal.util.StringUtils;


public class CommandProcessor {




    public void processTarget(Target target, RoundEnvironment roundEnv, ProcessingEnvironment processingEnv, Mod mod, Element commandElement) throws IOException {

        if (!(commandElement instanceof TypeElement)) return;

        //temp
        if (target != Target.NEO_1_21) return;

        //Checks to perform!
        TypeElement type = (TypeElement) commandElement;

        Command command = type.getAnnotation(Command.class);

        //First, check if the command type has an outer class
        TypeElement outer = ProcessorUtils.getOuterOrNull(type);
        if (outer != null) {
            //If so, check if the parent is a command;
            if (ProcessorUtils.hasAnnotation(outer, Command.class)) {
                //If so, our initial type is a subcommand. We only care for roots at this point in time
                return;
            }

        }
        processingEnv.getMessager().printMessage(Kind.NOTE, "Processing command: " + type.getQualifiedName(), type);

        //Now, we process the command. We prepare the start, and then work inwards. Subcommands, executes etc
        //but for this quick test we just need start and end really
        //logic to be reproduced in other methods for subcommands and executes etc

        //This is going to be so, so, so cursed.
        CommandGen commandGen = CommandGen.getCommandGen(target);
        String className = type.getSimpleName().toString();

        type.getEnclosedElements().forEach((element) -> {

            Executes executesAnnotation = element.getAnnotation(Executes.class);
            if (executesAnnotation != null) {
                try {
                    parseExecutes(processingEnv, element, executesAnnotation, target, commandGen, type.getQualifiedName().toString().replace(".", "/"));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        });


        //And now we write the class
        
        File buildDir = ProcessorUtils.getBuildDir(processingEnv);
        File outputDir = new File(buildDir, "goagnostic/" + target.getImplName());

        HashMap<String, String> map = new HashMap<>();
        map.put("goagnosticcommandname", command.name());
        
        byte[] remapped = ProcessorUtils.remapFile(commandGen.getRemappedBytes(), mod, ((TypeElement) commandElement).getQualifiedName().toString(), map);

        File outputFile = new File(outputDir, StringUtils.lowerWithSlashes(mod
                        .group() + "." + mod.modid() + ".") + className + ".class");
                        
                    if (!outputFile.getParentFile().exists()) outputFile.getParentFile().mkdirs();
                        
                    FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                    fileOutputStream.write(remapped);
                    fileOutputStream.flush();
                    fileOutputStream.close();
        
    }

    private void parseExecutes(ProcessingEnvironment processingEnv, Element element, Executes executesAnnotation, Target target, CommandGen commandGen, String className) throws IOException {
        
        String toInject = new String(ProcessorUtils.readAllBytes(CommandProcessor.class.getResourceAsStream("/commandgen/" + target.getImplName() + "/executes")))
            .replace("EXECUTEOWNER", className)
            .replace("EXECUTEFUNC", element.getSimpleName());


        processingEnv.getMessager().printMessage(Kind.WARNING, toInject);

        commandGen.injectIntoMethod(4, "register", toInject);
    }

}
