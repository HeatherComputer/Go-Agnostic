package computer.heather.goagnostic.api;

import java.util.HashMap;

public class Api {
    /*
    This is the main API class. It is used to access the majority of Go!Agnostic features.
    Its methods are all static and can be called from anywhere.

    Methods throw exceptions if they are called too early.
    */

    public static Logger logger;
    public static HashMap<String, String> modList;

    public static Logger getLogger() {
        //We need version specific implementation.

        /*
        Design choice made here - Go!Agnostic is designed to be shadowed / shaded, and remapped.
        This means that we can effectively assume that only one mod is using Go!Agnostic - any other mods using it have their own "copy" bundled.
        This makes version specific implementation much easier.
        */
       
        return logger;
    }

    public static HashMap<String, String> getModList() {
        //This one's simple - a map of modid to mod name. 
        //Should ideally be populated on init.
        return modList;
    }
}
