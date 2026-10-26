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
import computer.heather.goagnostic.api.annotations.Command.Require;
import computer.heather.goagnostic.api.annotations.Mod.Target;
import computer.heather.goagnostic.internal.commandgen.CommandGen;
import computer.heather.goagnostic.internal.util.ProcessorUtils;
import computer.heather.goagnostic.internal.util.StringUtils;


public class CommandProcessor {




    public void processTarget(Target target, RoundEnvironment roundEnv, ProcessingEnvironment processingEnv, Mod mod, Element commandElement) throws IOException {

        if (!(commandElement instanceof TypeElement)) return;

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
        processingEnv.getMessager().printMessage(Kind.NOTE, "Processing command: " + type.getSimpleName() + " on target: " + target, type);

        //Now, we process the command. We prepare the start, and then work inwards. Subcommands, executes etc
        //but for this quick test we just need start and end really
        //logic to be reproduced in other methods for subcommands and executes etc

        //This is going to be so, so, so cursed.
        CommandGen commandGen = CommandGen.getCommandGen(target, StringUtils.lowerWithSlashes(mod.group() + "." + mod.modid() + ".") + command.name() + "command");
        String className = type.getQualifiedName().toString().replace(".", "/");

        //One for the outer, and everything else is inner
        //Handle requirements before execution, then subcommands after
        type.getEnclosedElements().forEach((element) -> {

            Require requireAnnotation = element.getAnnotation(Require.class);
            if (requireAnnotation != null) {
                try {
                    commandGen.requires(processingEnv, element, requireAnnotation, target, commandGen, type, className);
                } catch (IOException e) {
                    processingEnv.getMessager().printMessage(Kind.ERROR, "GoAgnostic encountered an error: " + e.getMessage(), element);
                }
            }

        });
        type.getEnclosedElements().forEach((element) -> {

            Executes executesAnnotation = element.getAnnotation(Executes.class);
            if (executesAnnotation != null) {
                try {
                    commandGen.executes(processingEnv, element, executesAnnotation, target, commandGen, type, className);
                } catch (IOException e) {
                    processingEnv.getMessager().printMessage(Kind.ERROR, "GoAgnostic encountered an error: " + e.getMessage(), element);
                }
            }
        });
        //One for the outer, and everything else is inner
        type.getEnclosedElements().forEach((element) -> {

            Command commandAnnotation = element.getAnnotation(Command.class);
            if (commandAnnotation != null) {
                try {
                    handleClass(commandAnnotation, (TypeElement) element, commandGen, processingEnv, target, 1, mod);
                } catch (IOException e) {
                    processingEnv.getMessager().printMessage(Kind.ERROR, "GoAgnostic encountered an error: " + e.getMessage(), element);
                }
            }

        });


        //Call finalise.
        commandGen.finalise(target, StringUtils.lowerWithSlashes(mod.group() + "." + mod.modid() + ".") + command.name() + "command.class");
        //And now we write the class
        
        File buildDir = ProcessorUtils.getBuildDir(processingEnv);
        File outputDir = new File(buildDir, "goagnostic/" + target.getImplName());

        HashMap<String, String> map = new HashMap<>();
        map.put("goagnosticcommandname", command.name());
        //1.12 requires this to be separate, maybe other versions will too
        map.put("goagnosticcommandclassname", command.name() + "command");
        
        byte[] remapped = ProcessorUtils.remapFile(commandGen.getRemappedBytes(), mod, ((TypeElement) commandElement).getQualifiedName().toString(), map);

        File outputFile = new File(outputDir, StringUtils.lowerWithSlashes(mod
                        .group() + "." + mod.modid() + ".") + command.name() + "command.class");
                        
                    if (!outputFile.getParentFile().exists()) outputFile.getParentFile().mkdirs();
                        
                    FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                    fileOutputStream.write(remapped);
                    fileOutputStream.flush();
                    fileOutputStream.close();
        
    }


    private static void handleClass(Command command, TypeElement type, CommandGen commandGen, ProcessingEnvironment processingEnv, Target target, int nestLevel, Mod mod) throws IOException {

        processingEnv.getMessager().printMessage(Kind.NOTE, "Nest Level: " + nestLevel);
        String className;

        if (nestLevel > 0) {
            className = replaceLastSlashes(type.getQualifiedName().toString().replace(".", "/"), nestLevel);
            commandGen.subcommandStart(command.name(), target, commandGen, className);
        }
        else {
            className = type.getQualifiedName().toString().replace(".", "/");
        }
        
        type.getEnclosedElements().forEach((element) -> {

            Require requireAnnotation = element.getAnnotation(Require.class);
            if (requireAnnotation != null) {
                try {
                    commandGen.requires(processingEnv, element, requireAnnotation, target, commandGen, type, className);
                } catch (IOException e) {
                    processingEnv.getMessager().printMessage(Kind.ERROR, "GoAgnostic encountered an error: " + e.getMessage(), element);
                }
            }

        });
        type.getEnclosedElements().forEach((element) -> {

            Executes executesAnnotation = element.getAnnotation(Executes.class);
            if (executesAnnotation != null) {
                try {
                    commandGen.executes(processingEnv, element, executesAnnotation, target, commandGen, type, className);
                } catch (IOException e) {
                    processingEnv.getMessager().printMessage(Kind.ERROR, "GoAgnostic encountered an error: " + e.getMessage(), element);
                }
            }
        });
        //One for the outer, and everything else is inner
        type.getEnclosedElements().forEach((element) -> {

            Command commandAnnotation = element.getAnnotation(Command.class);
            if (commandAnnotation != null) {
                try {
                    handleClass(commandAnnotation, (TypeElement) element, commandGen, processingEnv, target, nestLevel + 1, mod);
                } catch (IOException e) {
                    processingEnv.getMessager().printMessage(Kind.ERROR, "GoAgnostic encountered an error: " + e.getMessage(), element);
                }
            }

        });
        if (nestLevel > 0) {
            commandGen.subcommandEnd(command.name(), target, commandGen, className, mod, processingEnv);
        }
    }


    //Helper stuff
    public static String replaceLastSlashes(String input, int count) {
        StringBuilder builder = new StringBuilder(input);
        int searchFrom = builder.length();

        for (int i = 0; i < count; i++) {
            int index = builder.lastIndexOf("/", searchFrom - 1);

            if (index == -1) {
                break;
            }

            builder.setCharAt(index, '$');
            searchFrom = index;
        }

        return builder.toString();
    }

}
