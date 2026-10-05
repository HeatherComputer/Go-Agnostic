package computer.heather.goagnostic.internal.commandgen.neoforge;

import java.io.IOException;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import computer.heather.goagnostic.api.annotations.Command.Executes;
import computer.heather.goagnostic.api.annotations.Command.Require;
import computer.heather.goagnostic.api.annotations.Mod.Target;
import computer.heather.goagnostic.internal.annotationprocessors.CommandProcessor;
import computer.heather.goagnostic.internal.annotationprocessors.ModProcessor;
import computer.heather.goagnostic.internal.commandgen.CommandGen;
import computer.heather.goagnostic.internal.util.ProcessorUtils;

public class NeoforgeCommandGens {
    
    public static class NEO_1_21 extends CommandGen {

        int count = 5;

        public NEO_1_21(String implName) throws IOException {
            super(
                ProcessorUtils.readAllBytes(NEO_1_21.class.getResourceAsStream("/templates/GoAgnosticTemplate-" + implName + "/goagnosticmodgroup/goagnosticmodid/goagnosticcommandnamecommand.class.bin"))
            );
            //TODO Auto-generated constructor stub
        }

        @Override
        public void executes(ProcessingEnvironment processingEnv, Element element, Executes executesAnnotation,
                Target target, CommandGen commandGen, TypeElement type) throws IOException {     
        
                    
            String className = type.getQualifiedName().toString().replace(".", "/");
                
            String toInject = new String(ProcessorUtils.readAllBytes(CommandProcessor.class.getResourceAsStream("/commandgen/" + target.getImplName() + "/executes")))
                .replace("EXECUTEOWNER", className)
                .replace("EXECUTEFUNC", element.getSimpleName());


            //processingEnv.getMessager().printMessage(Kind.WARNING, toInject);

            commandGen.injectIntoMethod(count, "register", toInject);
            count += 6;
        }

        @Override
        public void requires(ProcessingEnvironment processingEnv, Element element, Require requiresAnnotation,
                Target target, CommandGen commandGen, TypeElement type) throws IOException {

            String className = type.getQualifiedName().toString().replace(".", "/");
                
            String toInject = new String(ProcessorUtils.readAllBytes(CommandProcessor.class.getResourceAsStream("/commandgen/" + target.getImplName() + "/requires")))
                .replace("REQUIREOWNER", className)
                .replace("REQUIREFUNC", element.getSimpleName());


            processingEnv.getMessager().printMessage(Kind.WARNING, toInject);

            commandGen.injectIntoMethod(count, "register", toInject);
            count += 6;
        }

    }

}//.getQualifiedName().toString().replace(".", "/")
