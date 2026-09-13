package computer.heather.goagnostic.api.util;

import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

public class ProcessorUtils {
    

    public static boolean extendsBaseClass(ProcessingEnvironment processingEnv, Element element, String baseClassName) {
        TypeElement baseClass = processingEnv
                .getElementUtils()
                .getTypeElement(baseClassName);
    
        if (baseClass == null) {
            return false;
        }
    
        Types types = processingEnv.getTypeUtils();
    
        return types.isSubtype(
                types.erasure(element.asType()),
                types.erasure(baseClass.asType())
        );
    }

}
