package computer.heather.goagnostic.annotations;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Target(ElementType.TYPE)
public @interface Mod {
    

    String modid();
    String modName();
    String version();

    Target[] targets();
    


    public static enum Target {
        NEO_1_21
    }


}   
