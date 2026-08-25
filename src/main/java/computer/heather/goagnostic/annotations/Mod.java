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
        NEO_1_21;


        public String getImplName() {
            switch (this) {

                case NEO_1_21:
                    return "null"; // TODO: Implement this properly when the implementation is ready.

                default:
                    throw new IllegalArgumentException("Unknown target: " + this);
                    
            }
        }
    }


}   
