package computer.heather.goagnostic.internal.annotationprocessors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;

import computer.heather.goagnostic.api.annotations.Command;
import computer.heather.goagnostic.api.annotations.Mod;
import computer.heather.goagnostic.internal.util.ModTomlRemapper;
import computer.heather.goagnostic.internal.util.ProcessorUtils;
import computer.heather.goagnostic.internal.util.StringUtils;

@SupportedAnnotationTypes({
    "computer.heather.goagnostic.api.annotations.Mod",
    "computer.heather.goagnostic.api.annotations.Command"
})
public class ProcessorBase extends AbstractProcessor {



    public static final String[] otherClasses = new String[] {
        "/util/Player",
        "/util/chat/ChatHandler",
        "/util/chat/Message",
        "/util/CommandHandler",
        "/util/CommandHandler$RequireChecker"
    };




    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        boolean duplicate = false;
        processingEnv.getMessager().printMessage(Kind.NOTE, "Starting ModProcessor");

        //Check if @mod is used multiple times. Other annotations are not dictated by this ofc
        for (TypeElement annotation : annotations) {
            if (annotation.getQualifiedName().contentEquals("computer.heather.goagnostic.api.annotations.Mod")) {
                if (duplicate) {
                    processingEnv.getMessager().printMessage(Kind.ERROR, "Multiple annotations found. Only one @Mod annotation is allowed.");
                    return false;
                }
                duplicate = true;
            }
        }

        //We know there's only one.
        if (roundEnv.getElementsAnnotatedWith(Mod.class).isEmpty()) return false; //Something has gone very wrong

        Element modElement = roundEnv.getElementsAnnotatedWith(Mod.class).iterator().next();
        Mod mod = modElement.getAnnotation(Mod.class);
        // We need to generate different classes for each target specified in the annotation. We can get the targets from the annotation's values.
        Mod.Target[] targets = mod.targets();
        
        //Store a list of target dirs we've outputted to
        ArrayList<String> dirs = new ArrayList<>();

        //Now we invoke modprocessor and commandprocessor with each target

        ModProcessor modProcessor = new ModProcessor();
        CommandProcessor commandProcessor = new CommandProcessor();

        for (Mod.Target target : targets) {

            dirs.add(target.getImplName());

            //Process the main mod class
            processingEnv.getMessager().printMessage(Kind.NOTE, "Starting ModProcessor");
            try {
                modProcessor.processTarget(target, roundEnv, processingEnv, mod, modElement);
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Kind.ERROR, "GoAgnostic encountered an error: " + e.getMessage(), modElement);
            }

            //And do the same with commands
            Set<? extends Element> commands = roundEnv.getElementsAnnotatedWith(Command.class);
            for (Element commandElement : commands) {
                try {
                    commandProcessor.processTarget(target, roundEnv, processingEnv, mod, commandElement);
                } catch (IOException e) {
                    processingEnv.getMessager().printMessage(Kind.ERROR, "GoAgnostic encountered an error: " + e.getMessage(), commandElement);
                }
            }


            //Basic remapping for non-annotation classes
            try {

                processingEnv.getMessager().printMessage(Kind.NOTE, "Remapping other classes");
                File buildDir = ProcessorUtils.getBuildDir(processingEnv);
                File outputDir = new File(buildDir, "goagnostic/" + target.getImplName());
                
                for (String className : otherClasses) {


                    processingEnv.getMessager().printMessage(Kind.OTHER, target.getImplName() + className);
                    byte[] remapped = ProcessorUtils.remapFile(ModProcessor.class.getResourceAsStream("/templates/GoAgnosticTemplate-" + target.getImplName() + "/goagnosticmodgroup/goagnosticmodid" + className + ".class.bin"), 
                        mod, ((TypeElement) modElement).getQualifiedName().toString());
                    
                    File outputFile = new File(outputDir, StringUtils.lowerWithSlashes(mod
                        .group() + "." + mod.modid() + ".") + className + ".class");
                        
                    if (!outputFile.getParentFile().exists()) outputFile.getParentFile().mkdirs();
                        
                    FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                    fileOutputStream.write(remapped);
                    fileOutputStream.flush();
                    fileOutputStream.close();

                }

            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Kind.ERROR, "GoAgnostic encountered an error: " + e.getMessage(), modElement);
            }
            
         
            //Now we need to output a correctly generated mods.toml (or similar for the version in use)
            String inputLocation = "/templates/GoAgnosticTemplate-" + target.getImplName() + "/" + target.getModTomlTemplatePath();
            try {
                //This is cursed but may work. Toml should be in the right spot, and then the right spot relative to the manually outputted class
                //ModTomlRemapper.remapModToml(inputLocation, outputLocation, mod, ModProcessor.class);

                File buildDir = ProcessorUtils.getBuildDir(processingEnv);
                File outputDir = new File(buildDir, "goagnostic/" + target.getImplName());
                File outputFile = new File(outputDir, target.getModTomlTemplatePath());
                if (!outputFile.getParentFile().exists()) outputFile.getParentFile().mkdirs();
                ModTomlRemapper.remapModToml(inputLocation, outputFile, mod, getClass());


            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Kind.ERROR, "GoAgnostic encountered an error: " + e.getMessage(), modElement);
            }   

                        


        }


        //And now we write the aforementioned list of dirs
        try {
            File buildDir = ProcessorUtils.getBuildDir(processingEnv);
            File outputDir = new File(buildDir, "goagnostic/");
            File outputFile = new File(outputDir, "targets.txt");
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
            for (String target : dirs) {
                writer.write(target);
                writer.write("\n");
            } 
            writer.close();  

        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Kind.ERROR, "GoAgnostic encountered an error: " + e.getMessage(), modElement);
        }

        //We also write a modid file
        try {
            File buildDir = ProcessorUtils.getBuildDir(processingEnv);
            File outputDir = new File(buildDir, "goagnostic/");
            File outputFile = new File(outputDir, "modid.txt");
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
            writer.write(mod.modid());
            writer.close();

        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Kind.ERROR, "GoAgnostic encountered an error: " + e.getMessage(), modElement);
        }


        return false;
    }

}
