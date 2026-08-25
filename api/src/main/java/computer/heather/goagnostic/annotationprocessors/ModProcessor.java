package computer.heather.goagnostic.annotationprocessors;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import computer.heather.goagnostic.annotations.Mod;

@SupportedAnnotationTypes("computer.heather.goagnostic.annotations.Mod")
public class ModProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        //Check size of annotations set. This only supports one annotation usage, so if multiple are present throw an error!
        if (annotations.size() > 1) {
            processingEnv.getMessager().printMessage(Kind.ERROR, "Multiple annotations found. Only one @Mod annotation is allowed.");
            return false;
        }

        //From here on, we can safely assume that there is only one annotation present in the set.
        //We still use an iterator to loop through for completions sake.


        for (TypeElement annotation : annotations) {
            if (annotation.getQualifiedName().contentEquals("computer.heather.goagnostic.annotations.Mod")) {
                if (annotation.getKind().isClass()) {
                    // First, check the class extends ModClassBase.
                    if (!processingEnv.getTypeUtils().isAssignable(annotation.asType(), processingEnv.getElementUtils().getTypeElement("computer.heather.goagnostic.api.ModClassBase").asType())) {
                        processingEnv.getMessager().printMessage(Kind.ERROR, "Classes annotated with @Mod must extend ModClassBase", annotation);
                    }

                    // We need to generate a class for each target specified in the annotation. We can get the targets from the annotation's values.
                    Mod.Target[] targets = annotation.getAnnotation(Mod.class).targets();
                    for (Mod.Target target : targets) {
                        
                    }


                } else {
                    processingEnv.getMessager().printMessage(Kind.ERROR, "@Mod annotation can only be applied to classes", annotation);
                }
            }
        }
        return false;
    }

}
