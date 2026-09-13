package computer.heather.goagnostic.api.annotationprocessors;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import computer.heather.goagnostic.api.annotations.Mod;

@SupportedAnnotationTypes("computer.heather.goagnostic.api.annotations.Mod")
public class ModProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        boolean duplicate = false;

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

        for (TypeElement annotation : annotations) {
            if (annotation.getQualifiedName().contentEquals("computer.heather.goagnostic.api.annotations.Mod")) {
                if (annotation.getKind().isClass()) {
                    // First, check the class extends ModClassBase.
                    if (!processingEnv.getTypeUtils().isAssignable(annotation.asType(), processingEnv.getElementUtils().getTypeElement("computer.heather.goagnostic.api.baseclasses.GoAgnosticMod").asType())) {
                        processingEnv.getMessager().printMessage(Kind.ERROR, "Classes annotated with @Mod must extend GoAgnosticMod", annotation);
                        return false;
                    }

                    // We need to generate a class for each target specified in the annotation. We can get the targets from the annotation's values.
                    Mod.Target[] targets = annotation.getAnnotation(Mod.class).targets();
                    for (Mod.Target target : targets) {
                        System.out.println(target.getImplName());
                    }


                } else {
                    processingEnv.getMessager().printMessage(Kind.ERROR, "@Mod annotation can only be applied to classes", annotation);
                }
            }
        }
        return false;
    }

}
