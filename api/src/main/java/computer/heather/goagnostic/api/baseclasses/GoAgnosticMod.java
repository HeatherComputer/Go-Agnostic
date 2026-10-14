package computer.heather.goagnostic.api.baseclasses;

import java.util.function.Consumer;
import java.util.function.Supplier;

import computer.heather.goagnostic.api.util.player.Player;

public class GoAgnosticMod {

    /*
    
    Hi! 
    Use @override to override any of the below non-final methods in your own mod class.
    Anything that's not a valid override will be ignored by Go!Agnostic. That of course doesn't mean you can't have them present and use them on your own, they just won't be called by Go!Agnostic.

    Logger class here means there's a simple logger - Go!Agnostic will map these to each separate modloader / version's logging system for you.

    Non-abstract methods do not have to be overridden! Depending on the method they'll either do nothing or have some basic default behaviour.
    Abstract methods of course must be overriden.

    Final non-internal methods are there to be used as desired.

    Some methods are internal and shouldn't be interacted with. 

    */
    

    public final String MOD_NAME;
    public final String MOD_ID;
    public final Logger LOGGER;

    private Supplier<Player[]> playerSupplier;

    private static GoAgnosticMod INSTANCE;


    //Instance shenanigans to let the mod instance be accessible anywhere.
    //Instance can only ever be set once!
    public static GoAgnosticMod getInstance() {
        return INSTANCE;
    }

    private static void setInstance(GoAgnosticMod instance) throws Exception {
        if (instance != null) INSTANCE = instance;
        //Disabled for now, POSSIBLY forever
        //else throw new Exception("Cannot set GoAgnosticMod instance multiple times!");
    }

    public GoAgnosticMod(String modName, String modId, Logger logger) throws Exception {
        MOD_NAME = modName;
        MOD_ID = modId;
        LOGGER = logger;
        setInstance(INSTANCE);
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


    public final Player[] getPlayers() {
        return playerSupplier.get();
    }

    public final void internal_setPlayerSupplier(Supplier<Player[]> supplier) {
        this.playerSupplier = supplier;
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
