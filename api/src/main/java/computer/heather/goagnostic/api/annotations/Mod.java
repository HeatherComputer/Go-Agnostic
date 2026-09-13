package computer.heather.goagnostic.api.annotations;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Target(ElementType.TYPE)
public @interface Mod {
    

    String modid();
    String modName();
    int version();
    String group();

    Target[] targets();
    


    public static enum Target {
        NEO_1_21;


        public String getImplName() {
            switch (this) {

                case NEO_1_21:
                    return "neoforge-1.21";

                default:
                    throw new IllegalArgumentException("Unknown target: " + this);
                    
            }
        }
    }


}   
