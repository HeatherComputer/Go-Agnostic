package computer.heather.goagnostic.api.annotations;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Target(ElementType.TYPE)
public @interface Mod {
    

    String modid();
    String modName();
    String version();
    String group();
    String[] authors() default {};
    String description() default "";
    String license() default "ARR";

    Target[] targets();
    


    public static enum Target {
        NEO_1_21,
        FORGE_1_12;


        public String getImplName() {
            switch (this) {

                case NEO_1_21:
                    return "neoforge-1.21";
                case FORGE_1_12:
                    return "forge-1.12";

                default:
                    throw new IllegalArgumentException("Unknown target: " + this);
                    
            }
        }

        public String getModTomlTemplatePath() {
            switch (this) {

                case NEO_1_21:
                    return "META-INF/neoforge.mods.toml";
                case FORGE_1_12:
                    return "mcmod.info";

                default:
                    throw new IllegalArgumentException("Unknown target: " + this);
                    
            }
        }

        public String[] getExtraTargets() {
            switch (this) {
                case FORGE_1_12: return new String[]{"/util/GACommandBase"};
                default : return new String[] {};
            }
        }
    }


}   
