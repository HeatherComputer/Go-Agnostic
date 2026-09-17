package computer.heather.goagnostic.api.baseclasses;

import java.util.function.Consumer;

import computer.heather.goagnostic.api.util.player.Player;

public class GoAgnosticMod {

    /*
    
    Hi! 
    Use @override to override any of the below non-final methods in your own mod class.
    Anything that's not a valid override will be ignored by Go!Agnostic. That of course doesn't mean you can't have them present and use them on your own, they just won't be called by Go!Agnostic.

    Logger class here means there's a simple logger - Go!Agnostic will map these to each separate modloader / version's logging system for you.

    Non-abstract methods do not have to be overridden! Depending on the method they'll either do nothing or have some basic default behaviour.
    Abstract methods of course must be overriden.

    */
    

    public final String MOD_NAME;
    public final String MOD_ID;
    public final Logger LOGGER;



    public GoAgnosticMod(String modName, String modId, Logger logger) {
        MOD_NAME = modName;
        MOD_ID = modId;
        LOGGER = logger;
    }

    /*
    Let's put lifecycle events in here - other events may be elsewhere?
    Default implementation for all does nothing, but can be overridden as desired.
    What even counts as lifecycle for this? Do tick events count?
    */

    public void onServerStarting() {
        // Default implementation does nothing
    }

    public void onServerStarted() {
        // Default implementation does nothing
    }

    public void onServerStopping() {
        // Default implementation does nothing
    }

    public void serverTick() {
        //Again, default implementation does nothing
    }

    public void playerJoin(Player player) {
        //last comment of this type
    }
    
    public void playerLeave(Player player) {
        
    }



    //Logger class, this is a good spot for it I think

    public static final class Logger {

        public Logger(Consumer<String> info, Consumer<String> warn, Consumer<String> error) {
            this.info = info;
            this.warn = warn;
            this.error = error;
        }

        private final Consumer<String> info;
        private final Consumer<String> warn;
        private final Consumer<String> error;

        public void info(String message) {
            info.accept(message);
        }

        public void warn(String message) {
            warn.accept(message);
        }

        public void error(String message) {
            error.accept(message);
        }


    }





}
