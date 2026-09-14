package computer.heather.goagnostic.api.annotationprocessors;

import java.io.IOException;
import java.io.InputStream;
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

import computer.heather.goagnostic.api.annotations.Mod;
import computer.heather.goagnostic.api.util.ModTomlRemapper;
import computer.heather.goagnostic.api.util.ProcessorUtils;

@SupportedAnnotationTypes("computer.heather.goagnostic.api.annotations.Mod")
public class ModProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        boolean duplicate = false;
        processingEnv.getMessager().printMessage(Kind.WARNING, "Starting ModProcessor");

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
            processingEnv.getMessager().printMessage(Kind.WARNING, element.getKind().toString());
            if (element.getKind() == ElementKind.CLASS) {
                // First, check the class extends ModClassBase.
                if (!ProcessorUtils.extendsBaseClass(processingEnv, element, "computer.heather.goagnostic.api.baseclasses.GoAgnosticMod")) {
                    processingEnv.getMessager().printMessage(Kind.ERROR, "The class annotated with @Mod must extend GoAgnosticMod", element);
                    return false;
                }

                
                Mod mod = element.getAnnotation(Mod.class);
                // We need to generate a class for each target specified in the annotation. We can get the targets from the annotation's values.
                Mod.Target[] targets = mod.targets();
                for (Mod.Target target : targets) {
                    processingEnv.getMessager().printMessage(Kind.WARNING, target.getImplName());

                    try {
                        processingEnv.getMessager().printMessage(Kind.WARNING, "/templates/GoAgnosticTemplate-" + target.getImplName() + "/goagnosticmodgroup/goagnosticmodid/goagnosticmodid.class.bin");
						byte[] remapped = ProcessorUtils.remapFile(ModProcessor.class.getResourceAsStream("/templates/GoAgnosticTemplate-" + target.getImplName() + "/goagnosticmodgroup/goagnosticmodid/goagnosticmodid.class.bin"), 
                            mod, ((TypeElement) element).getQualifiedName().toString());

                        processingEnv.getFiler().createClassFile(mod
                                .group().toLowerCase().replace("/", ".").replace(" ",".") + "." + mod.modid() + "." + mod.modid(), element)
                            .openOutputStream()
                            .write(remapped);

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

                    //Now we need to output a correctly generated mods.toml (or similar for the version in use)
                    String inputLocation = "/templates/GoAgnosticTemplate-" + target.getImplName() + "/" + target.getModTomlTemplatePath();
                    try {
                        FileObject outputLocation = processingEnv.getFiler().createResource(javax.tools.StandardLocation.CLASS_OUTPUT, "", target.getModTomlTemplatePath(), element);

                        //We should defer this to another class, really.
                        ModTomlRemapper.remapModToml(inputLocation, outputLocation, mod, ModProcessor.class);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }


            } else {
                processingEnv.getMessager().printMessage(Kind.ERROR, "@Mod annotation can only be applied to classes", element);
            }
        }
        return false;
    }

}
