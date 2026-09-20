package computer.heather.goagnostic.internal.annotationprocessors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Set;
import java.util.jar.JarFile;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import computer.heather.goagnostic.api.annotations.Mod;
import computer.heather.goagnostic.internal.util.ModTomlRemapper;
import computer.heather.goagnostic.internal.util.ProcessorUtils;
import computer.heather.goagnostic.internal.util.StringUtils;

@SupportedAnnotationTypes("computer.heather.goagnostic.api.annotations.Mod")
public class ModProcessor extends AbstractProcessor {



    public static final String[] otherClasses = new String[] {
        "/util/Player",
        "/util/chat/ChatHandler",
        "/util/chat/Message"
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

        for (Element element : roundEnv.getElementsAnnotatedWith(Mod.class)) {
            //processingEnv.getMessager().printMessage(Kind.WARNING, element.getKind().toString());
            if (element.getKind() == ElementKind.CLASS) {
                // First, check the class extends ModClassBase.
                if (!ProcessorUtils.extendsBaseClass(processingEnv, element, "computer.heather.goagnostic.api.baseclasses.GoAgnosticMod")) {
                    processingEnv.getMessager().printMessage(Kind.ERROR, "The class annotated with @Mod must extend GoAgnosticMod", element);
                    return false;
                }

                //This is a list of dirs for the GoAgnosticFinalise gradle task to package.
                ArrayList<String> dirs = new ArrayList<>();


                Mod mod = element.getAnnotation(Mod.class);
                // We need to generate a class for each target specified in the annotation. We can get the targets from the annotation's values.
                Mod.Target[] targets = mod.targets();
                for (Mod.Target target : targets) {
                    
                    dirs.add(target.getImplName());

                    try {
                        
                        //Remap the main class
                        processingEnv.getMessager().printMessage(Kind.OTHER, target.getImplName() + "/goagnosticmodgroup/goagnosticmodid/goagnosticmodid.class.bin");
						byte[] remapped = ProcessorUtils.remapFile(ModProcessor.class.getResourceAsStream("/templates/GoAgnosticTemplate-" + target.getImplName() + "/goagnosticmodgroup/goagnosticmodid/goagnosticmodid.class.bin"), 
                            mod, ((TypeElement) element).getQualifiedName().toString());

                        File buildDir = ProcessorUtils.getBuildDir(processingEnv);
                        File outputDir = new File(buildDir, "goagnostic/" + target.getImplName());

                        //And now we write all the files!
                        File outputFile = new File(outputDir, StringUtils.lowerWithSlashes(mod
                                .group() + "." + mod.modid() + "." + mod.modid()) + ".class");
                        if (!outputFile.getParentFile().exists()) outputFile.getParentFile().mkdirs();
                        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                        fileOutputStream.write(remapped);
                        fileOutputStream.flush();
                        fileOutputStream.close();

                        //Now remap other classes
                        for (String className : otherClasses) {
                            
                            processingEnv.getMessager().printMessage(Kind.OTHER, target.getImplName() + className);
                            remapped = ProcessorUtils.remapFile(ModProcessor.class.getResourceAsStream("/templates/GoAgnosticTemplate-" + target.getImplName() + "/goagnosticmodgroup/goagnosticmodid" + className + ".class.bin"), 
                                mod, ((TypeElement) element).getQualifiedName().toString());
                            
                            outputFile = new File(outputDir, StringUtils.lowerWithSlashes(mod
                                .group() + "." + mod.modid() + ".") + className + ".class");
                                
                            if (!outputFile.getParentFile().exists()) outputFile.getParentFile().mkdirs();
                                
                            fileOutputStream = new FileOutputStream(outputFile);
                            fileOutputStream.write(remapped);
                            fileOutputStream.flush();
                            fileOutputStream.close();

                        }
                        

                    } catch (IOException e) {
                        processingEnv.getMessager().printMessage(Kind.ERROR, "GoAgnostic encountered an error: " + e.getMessage(), element);
					}

                    //Now we need to output a correctly generated mods.toml (or similar for the version in use)
                    String inputLocation = "/templates/GoAgnosticTemplate-" + target.getImplName() + "/" + target.getModTomlTemplatePath();
                    try {
                        FileObject outputLocation = processingEnv.getFiler().createResource(javax.tools.StandardLocation.CLASS_OUTPUT, "", target.getModTomlTemplatePath(), element);

                        //We should defer this to another class, really.

                        //This is cursed but may work. Toml should be in the right spot, and then the right spot relative to the manually outputted class
                        //ModTomlRemapper.remapModToml(inputLocation, outputLocation, mod, ModProcessor.class);

                        File buildDir = ProcessorUtils.getBuildDir(processingEnv);
                        File outputDir = new File(buildDir, "goagnostic/" + target.getImplName());
                        File outputFile = new File(outputDir, target.getModTomlTemplatePath());
                        if (!outputFile.getParentFile().exists()) outputFile.getParentFile().mkdirs();
                        ModTomlRemapper.remapModToml(inputLocation, outputFile, mod, getClass());

                        //HI FUTURE HEATHER THIS TEST WORKED NEXT STEP IS FUCKING PACKAGING THE JARS 

                    } catch (IOException e) {
                        processingEnv.getMessager().printMessage(Kind.ERROR, "GoAgnostic encountered an error: " + e.getMessage(), element);
                    }

                }

                //And now we write that list of dirs
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
                        processingEnv.getMessager().printMessage(Kind.ERROR, "GoAgnostic encountered an error: " + e.getMessage(), element);
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
                        processingEnv.getMessager().printMessage(Kind.ERROR, "GoAgnostic encountered an error: " + e.getMessage(), element);
                }



            } else {
                processingEnv.getMessager().printMessage(Kind.ERROR, "@Mod annotation can only be applied to classes", element);
            }
        }
        return false;
    }

}
