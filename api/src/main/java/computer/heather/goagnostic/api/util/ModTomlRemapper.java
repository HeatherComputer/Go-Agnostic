package computer.heather.goagnostic.api.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.tools.FileObject;

import computer.heather.goagnostic.api.annotations.Mod;

public class ModTomlRemapper {

    public static void remapModToml(String inputLocation, FileObject outputLocation, Mod mod, Class<?> loaderClass) {
        
        InputStream inputStream = loaderClass.getResourceAsStream(inputLocation);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputLocation.openOutputStream()))) {

            //Read each line from the input file, and replace the placeholders with the actual values from the Mod annotation, and write the modified line to the output file.


            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replace("goagnosticmodgroup", mod.group())
                           .replace("goagnosticmodid", mod.modid())
                           .replace("goagnosticmodname", mod.modName())
                           .replace("goagnosticmodversion", mod.version())
                           .replace("goagnosticmodauthors", String.join(", ", mod.authors()))
                           .replace("goagnosticmoddescription", mod.description())
                           .replace("goagnosticmodlicense", mod.license());
                writer.write(line);
                writer.newLine();
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    








}
