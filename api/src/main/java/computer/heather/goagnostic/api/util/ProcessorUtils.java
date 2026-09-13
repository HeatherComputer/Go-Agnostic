package computer.heather.goagnostic.api.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

import computer.heather.goagnostic.api.annotations.Mod;

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

    public static byte[] remapFile(InputStream file, Mod mod) throws IOException {

        byte[] bytes = readAllBytes(file);

        ClassReader reader = new ClassReader(bytes);
        ClassWriter writer = new ClassWriter(reader, 0);
        

        Remapper remapper = new Remapper() {
            
            @Override
            public String map(String internalName) {
                return 
                    internalName.replace("goagnosticmodgroup", mod.group().replace(".", "/"))
                        .replace("goagnosticmodid", mod.modid());
            };
        };


        ClassRemapper classRemapper = new ClassRemapper(writer, remapper);

        reader.accept(classRemapper, 0);
        
        byte[] remapped = writer.toByteArray();
        return remapped;


    }


    public static byte[] readAllBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int read = 0;
        
        System.out.println(in);

        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        return out.toByteArray();
    }

}
