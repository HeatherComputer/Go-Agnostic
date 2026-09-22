package computer.heather.goagnostic.internal.annotationprocessors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import computer.heather.goagnostic.api.annotations.Mod;
import computer.heather.goagnostic.api.annotations.Mod.Target;
import computer.heather.goagnostic.internal.util.ProcessorUtils;
import computer.heather.goagnostic.internal.util.StringUtils;

public class ModProcessor {




    public void processTarget(Target target, RoundEnvironment roundEnv, ProcessingEnvironment processingEnv, Mod mod, Element modElement) throws IOException {
        //Remap the main class
        processingEnv.getMessager().printMessage(Kind.OTHER, target.getImplName() + "/goagnosticmodgroup/goagnosticmodid/goagnosticmodid.class.bin");
        byte[] remapped = ProcessorUtils.remapFile(ModProcessor.class.getResourceAsStream("/templates/GoAgnosticTemplate-" + target.getImplName() + "/goagnosticmodgroup/goagnosticmodid/goagnosticmodid.class.bin"), 
            mod, ((TypeElement) modElement).getQualifiedName().toString());

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
        
    }

}
