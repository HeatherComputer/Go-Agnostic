package computer.heather.goagnostic.api.baseclasses;

import java.util.function.Consumer;

public class GoAgnosticMod {
    

    public final String MOD_NAME;
    public final String MOD_ID;
    public final Logger LOGGER;



    public GoAgnosticMod(String modName, String modId, Logger logger) {
        MOD_NAME = modName;
        MOD_ID = modId;
        LOGGER = logger;
    }

    public void onServerStarting() {
        // Default implementation does nothing
    }

    public void onServerStarted() {
        // Default implementation does nothing
    }

    public void onServerStopping() {
        // Default implementation does nothing
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
