package computer.heather.goagnostic.gradle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.jvm.tasks.Jar;

import computer.heather.goagnostic.internal.util.ProcessorUtils;

public class GoAgnosticPlugin implements Plugin<Project> {

    private static TaskProvider<Jar> jarTask;


    @Override
    public void apply(Project target) {
        

        jarTask = target.getTasks().named("jar", Jar.class);

        //Make our custom task

        TaskProvider<GoAgnosticTask> customTask = target.getTasks().register(
            "GoAgnosticFinalise", GoAgnosticTask.class, task -> {
                task.dependsOn(jarTask);

                task.getInputJar().set(
                    jarTask.flatMap(Jar::getArchiveFile)
                );
                task.getProjectVersion().set(
                    target.getVersion().toString()
                );
            }
        );
        

        //Our task needs to run once the build task is finished
        target.getPluginManager().withPlugin("java", ignored -> {
            target.getTasks()
                .named("build")
                .configure(task ->
                        task.finalizedBy(customTask)
                );
            }
        );



    }
    public static abstract class GoAgnosticTask extends DefaultTask {


        @InputFile
        public abstract RegularFileProperty getInputJar();
        @Input
        public abstract Property<String> getProjectVersion();


        @TaskAction
        public void run() throws IOException {
            getLogger().lifecycle("Merging generated classes with user provided classes...");

            //Get the name we need to use as a base
            String baseName = jarTask.get().getArchiveBaseName().get();

            //Get a version
            String version = getProjectVersion().get().equals("") ? "" : "-" + getProjectVersion().get();

            //Get the user's classes from their jar file
            File inputFile = getInputJar().getAsFile().get();

            FileInputStream fileInputStream = new FileInputStream(inputFile);
            ZipInputStream originalInputStream = new ZipInputStream(fileInputStream);

            
            File buildDir = ProcessorUtils.getBuildDir(inputFile);
            //Get the modid
            File metadataDir = new File(buildDir, "goagnostic");
            File idFile = new File(metadataDir, "modid.txt");
            BufferedReader idReader = new BufferedReader(new FileReader(idFile));
            String modid = idReader.readLine();

            //Get the targets list
            File targetsFile = new File(metadataDir, "targets.txt");

            BufferedReader targets = new BufferedReader(new FileReader(targetsFile));
            
            targets.lines().forEach((target) -> {

                getLogger().lifecycle(target);
                
                File outputZip = new File(buildDir + "/libs/", baseName + "-" + target + version + ".jar");
                try {
                    FileOutputStream outputStream = new FileOutputStream(outputZip);
                    ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
                    
                    ZipEntry inputEntry;
                    byte[] bytes = new byte[4000];

                    //Write all files from the existing jar to our new one
                    while ((inputEntry = originalInputStream.getNextEntry()) != null) { 

                        zipOutputStream.putNextEntry(new ZipEntry(inputEntry.getName()));
                        if (!inputEntry.isDirectory()) {
                            while (true) {
                                int i = originalInputStream.read(bytes);
                                if (i < 0) break;
                                    zipOutputStream.write(bytes, 0, i);
                            }
                        }
                        
                    }

                    //Now Go!Agnostic's api
                    ZipInputStream inputStream = new ZipInputStream(new FileInputStream(getGoAgnosticJar()));
                    while ((inputEntry = inputStream.getNextEntry()) != null) { 
                        if (!inputEntry.getName().contains("computer/heather/goagnostic/api")) continue;
                        zipOutputStream.putNextEntry(new ZipEntry(inputEntry.getName()));
                        if (!inputEntry.isDirectory()) {
                            while (true) {
                                int i = inputStream.read(bytes);
                                if (i < 0) break;
                                    zipOutputStream.write(bytes, 0, i);
                            }
                        }
                        
                    }
                    

                
                    //And now the new files
                    Path targetDir = new File(metadataDir, target).toPath() ;

                    Files.walkFileTree(targetDir, new SimpleFileVisitor<Path>() {
                        @Override

                        public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                            Path targetFile;
                            targetFile = targetDir.relativize(file);
                            zipOutputStream.putNextEntry(new ZipEntry(targetFile.toString().replace(File.separatorChar, '/')));
                            byte[] bytes = new byte[4000];
                            
                            try {
                                FileInputStream is = new FileInputStream(file.toFile());
                                while (true) {
                                    int i = is.read(bytes);
                                    if (i < 0) break;
                                    zipOutputStream.write(bytes, 0, i);
                                }
                                is.close();
                            } finally {
                                zipOutputStream.closeEntry();
                            }

                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFileFailed(Path path, IOException exc) {
                            //Should never happen!
                            return FileVisitResult.CONTINUE;
                        }
                    });

                    zipOutputStream.close();
                    

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                try {
                    getLogger().lifecycle("Remapping jar: " + outputZip.getName());
                    GoAgnosticRemapper.remapJar(outputZip, modid);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            });

            targets.close();
            originalInputStream.close();

            //Finally, get rid of the original jar to avoid confusion
            inputFile.delete();

        }
    }


    
    private static File getGoAgnosticJar() throws IOException {

        File jar;

        try {
            jar = new File(GoAgnosticPlugin.class
                            .getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI()
            );

        } catch (Exception e) {
            throw new IOException("Failed to locate GoAgnostic jar", e);
        }


        if (!jar.exists()) {
            throw new IOException("GoAgnostic jar does not exist: " + jar);
        }


        if (!jar.isFile()) {
            throw new IOException("GoAgnostic is not running from a jar: " + jar);
        }


        return jar;
    }


}

