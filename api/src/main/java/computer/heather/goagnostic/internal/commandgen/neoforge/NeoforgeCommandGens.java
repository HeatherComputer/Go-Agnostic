package computer.heather.goagnostic.internal.commandgen.neoforge;

import java.io.IOException;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import computer.heather.goagnostic.api.annotations.Command.Argument;
import computer.heather.goagnostic.api.annotations.Command.Executes;
import computer.heather.goagnostic.api.annotations.Command.Require;
import computer.heather.goagnostic.api.annotations.Mod.Target;
import computer.heather.goagnostic.api.command.args.ArgumentType;
import computer.heather.goagnostic.internal.annotationprocessors.CommandProcessor;
import computer.heather.goagnostic.internal.annotationprocessors.ModProcessor;
import computer.heather.goagnostic.internal.commandgen.CommandGen;
import computer.heather.goagnostic.internal.util.ProcessorUtils;

public class NeoforgeCommandGens {
    
    public static class NEO_1_21 extends CommandGen {

        int count = 1;

        public NEO_1_21(String implName) throws IOException {
            super(
                ProcessorUtils.readAllBytes(NEO_1_21.class.getResourceAsStream("/templates/GoAgnosticTemplate-" + implName + "/goagnosticmodgroup/goagnosticmodid/goagnosticcommandnamecommand.class.bin"))
            );
            //TODO Auto-generated constructor stub
        }

        @Override
        public void executes(ProcessingEnvironment processingEnv, Element element, Executes executesAnnotation,
                Target target, CommandGen commandGen, TypeElement type, String className) throws IOException {     
        
            Argument[] annotations = element.getAnnotationsByType(Argument.class);
            for (Argument argument : annotations) {
                argumentstart(target, commandGen, argument);
            }
                    
            //String className = type.getQualifiedName().toString().replace(".", "/");
                
            String toInject = new String(ProcessorUtils.readAllBytes(CommandProcessor.class.getResourceAsStream("/commandgen/" + target.getImplName() + "/executes" + (annotations.length > 0 ? "_args" : "_noargs"))))
                .replace("EXECUTEOWNER", className)
                .replace("EXECUTEFUNC", element.getSimpleName());


            //processingEnv.getMessager().printMessage(Kind.WARNING, toInject);

            injectIntoMethod(count, "register", toInject);
            count += (annotations.length > 0? 5 : 6);

            int i = 1;
            for (Argument argument : annotations) {
                argumentEnd(target, commandGen, argument, i==annotations.length);
                i++;
            }
        }

        @Override
        public void requires(ProcessingEnvironment processingEnv, Element element, Require requiresAnnotation,
                Target target, CommandGen commandGen, TypeElement type, String className) throws IOException {

            //String className = type.getQualifiedName().toString().replace(".", "/");
                
            String toInject = new String(ProcessorUtils.readAllBytes(CommandProcessor.class.getResourceAsStream("/commandgen/" + target.getImplName() + "/requires")))
                .replace("REQUIREOWNER", className)
                .replace("REQUIREFUNC", element.getSimpleName());


            //processingEnv.getMessager().printMessage(Kind.WARNING, toInject);

            injectIntoMethod(count, "register", toInject);
            count += 6;
        }

        @Override
        public void subcommandStart(String name, Target target, CommandGen commandGen, String className) throws IOException {
            String toInject = new String(ProcessorUtils.readAllBytes(CommandProcessor.class.getResourceAsStream("/commandgen/" + target.getImplName() + "/subcommandstart")))
                .replace("COMMANDNAME", name);
            injectIntoMethod(count, "register", toInject);
            count += 2;
        }

        @Override
        public void subcommandEnd(Target target, CommandGen commandGen, String className) throws IOException {            
            String toInject = new String(ProcessorUtils.readAllBytes(CommandProcessor.class.getResourceAsStream("/commandgen/" + target.getImplName() + "/subcommandend")));
            injectIntoMethod(count, "register", toInject);
            count += 2;
        }

        @Override
        public void argumentstart(Target target, CommandGen commandGen, Argument argument) throws IOException {
            String argType = getArgFileFromArgType(argument.type());
            String toInject = new String(ProcessorUtils.readAllBytes(CommandProcessor.class.getResourceAsStream("/commandgen/" + target.getImplName() + "/args/" + argType + "start")))
                .replace("ARGNAME", argument.name());
            injectIntoMethod(count, "register", toInject);
            count += 3;
        }

        @Override
        public void argumentEnd(Target target, CommandGen commandGen, Argument argument, boolean lastArg) throws IOException {
            String toInject = new String(ProcessorUtils.readAllBytes(CommandProcessor.class.getResourceAsStream("/commandgen/" + target.getImplName() + "/args/" + "argend")))
                .replace("ARGNAME", argument.name());
            if (!lastArg) toInject = toInject.replace("LiteralArgumentBuilder", "RequiredArgumentBuilder");
            injectIntoMethod(count, "register", toInject);
            count += 2;
        }


        private String getArgFileFromArgType(ArgumentType type) {
            switch (type) {
                case GREEDY_STRING: return "greedystring";
                case WORD: return "word";
                case QUOTABLE_STRING: return "string";
                case PLAYER: return "player";
                case INTEGER: return "integer";
                case BOOLEAN: return "boolean";
            
                default: return "";
            }
        }

    }

}//.getQualifiedName().toString().replace(".", "/")
