package computer.heather.goagnostic.api.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

import computer.heather.goagnostic.api.annotations.Mod;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
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

    public static byte[] remapFile(InputStream file, Mod mod, String annotatedClass) throws IOException {

        byte[] bytes = readAllBytes(file);

        ClassReader reader = new ClassReader(bytes);
        ClassWriter writer = new ClassWriter(reader, 0);
        

        Remapper remapper = new Remapper() {
            
            @Override
            public String map(String internalName) {
                System.out.println(internalName);

                if ("computer/heather/goagnostic/api/util/placeholders/GoAgnosticModPlaceholder".equals(internalName)) {
                    return StringUtils.standardWithSlashes(annotatedClass);
                }
                
                return internalName
                        .replace("goagnosticmodgroup", StringUtils.lowerWithSlashes(mod.group()))
                        .replace("goagnosticmodid", mod.modid());
            };
        };


        ClassVisitor visitor = new ClassRemapper(writer, remapper) {
            @Override 
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                
                if ("goagnosticmodgroup".equals(value)) {
                    value =StringUtils.lowerWithSlashes(mod.group());
                } else if ("goagnosticmodid".equals(value)) {
                    value = mod.modid();
                } else if ("goagnosticmodname".equals(value)) {
                    value = mod.modName();
                } else if ("goagnosticmodversion".equals(value)) {
                    value = mod.version();
                }

                return super.visitField(access, name, descriptor, signature, value);

            }

            @Override 
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                return new MethodVisitor(Opcodes.ASM9, mv) {
                    @Override
                    public void visitLdcInsn(Object value) {
                        if ("goagnosticmodgroup".equals(value)) {
                            value = StringUtils.lowerWithSlashes(mod.group());
                        } else if ("goagnosticmodid".equals(value)) {
                            value = mod.modid();
                        } else if ("goagnosticmodname".equals(value)) {
                            value = mod.modName();
                        } else if ("goagnosticmodversion".equals(value)) {
                            value = mod.version();
                        }
                        super.visitLdcInsn(value);
                    }
                };
            }

            @Override
            public AnnotationVisitor visitAnnotation(
                    String descriptor,
                    boolean visible) {

                AnnotationVisitor av =
                        super.visitAnnotation(descriptor, visible);

                return new AnnotationVisitor(Opcodes.ASM9, av) {

                    @Override
                    public void visit(String name, Object value) {

                        if ("goagnosticmodgroup".equals(value)) {
                            value = StringUtils.lowerWithSlashes(mod.group());

                        } else if ("goagnosticmodid".equals(value)) {
                            value = mod.modid();

                        } else if ("goagnosticmodname".equals(value)) {
                            value = mod.modName();

                        } else if ("goagnosticmodversion".equals(value)) {
                            value = mod.version();
                        }

                        super.visit(name, value);
                    }
                };
            }


        };
        //ClassRemapper classRemapper = new ClassRemapper(writer, remapper);

        reader.accept(visitor, 0);
        
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
