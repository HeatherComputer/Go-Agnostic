package computer.heather.goagnostic.gradle;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;


/*
 * We need to bundle GoAgnostic's api, and relocate it to avoid conflicts.
 * We then need to remap GoAgnostic's api code, generated code AND user code so they all target the bundled api.
 */
public class GoAgnosticRemapper {

    public static final String originalPrefix = "computer/heather/goagnostic/api";
    public static final String modlessRelocatedPrefix = "/internal/goagnostic/api"; //When remapping we prepend the modid!


    public static void remapJar(File toRemap, String modid) throws IOException {

        final String relocatedPrefix = modid + modlessRelocatedPrefix;

        //We can't rewrite the jar whilst we're reading from it, so write to a temporary jar first.
        File tempJar = new File(toRemap.getParentFile(), toRemap.getName() + ".remapping");

        Remapper remapper = new Remapper(Opcodes.ASM9) {

            @Override
            public String map(String internalName) {

                //Anything outside of GoAgnostic's api does not need its class name changed.
                if (!internalName.equals(originalPrefix) && !internalName.startsWith(originalPrefix + "/")) {
                    return internalName;
                }

                return relocatedPrefix + internalName.substring(originalPrefix.length());
            }
        };


        try (
                ZipInputStream inputStream = new ZipInputStream(new FileInputStream(toRemap));
                ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(tempJar))
        ) {

            ZipEntry inputEntry;

            while ((inputEntry = inputStream.getNextEntry()) != null) {

                String inputName = inputEntry.getName();
                String outputName = inputName;


                //DDon't copy over directories
                if (inputEntry.isDirectory()) {
                    continue;
                }


                if (inputName.endsWith(".class")) {

                    byte[] inputBytes = readAllBytes(inputStream);

                    /*
                     * Every class goes through the remapper, not just GoAgnostic classes.
                     *
                     * This means user and generated classes that reference the api are also updated
                     * to reference the relocated version.
                     */
                    byte[] outputBytes = remapClass(inputBytes, remapper);


                    //ClassRemapper changes the name inside the class, but it does not change the jar entry's name.
                    if (inputName.startsWith(originalPrefix + "/")) {
                        outputName = relocatedPrefix + inputName.substring(originalPrefix.length());
                    }


                    ZipEntry outputEntry = new ZipEntry(outputName);
                    outputEntry.setTime(inputEntry.getTime());

                    outputStream.putNextEntry(outputEntry);
                    outputStream.write(outputBytes);
                    outputStream.closeEntry();

                } else {

                    //Not a class, just copy it straight over.
                    ZipEntry outputEntry = new ZipEntry(outputName);
                    outputEntry.setTime(inputEntry.getTime());

                    outputStream.putNextEntry(outputEntry);

                    byte[] buffer = new byte[8192];
                    int read;

                    while ((read = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, read);
                    }

                    outputStream.closeEntry();
                }


                inputStream.closeEntry();
            }

        } catch (IOException | RuntimeException e) {

            //Don't leave a half-written temporary jar behind.
            tempJar.delete();

            throw e;
        }


        //The remap succeeded, so replace the original jar.
        Files.move(
                tempJar.toPath(),
                toRemap.toPath(),
                StandardCopyOption.REPLACE_EXISTING
        );
    }


    private static byte[] remapClass(byte[] classBytes, Remapper remapper) {

        ClassReader classReader = new ClassReader(classBytes);
        ClassWriter classWriter = new ClassWriter(classReader, 0);

        ClassRemapper classRemapper = new ClassRemapper(classWriter, remapper);

        classReader.accept(classRemapper, 0);

        return classWriter.toByteArray();
    }


    private static byte[] readAllBytes(InputStream inputStream) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[8192];
        int read;

        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }

        return outputStream.toByteArray();
    }
}