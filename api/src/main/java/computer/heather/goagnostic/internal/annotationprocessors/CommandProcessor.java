package computer.heather.goagnostic.internal.annotationprocessors;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import computer.heather.goagnostic.api.annotations.Command;
import computer.heather.goagnostic.api.annotations.Mod;
import computer.heather.goagnostic.api.annotations.Mod.Target;
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
        
    }


}
