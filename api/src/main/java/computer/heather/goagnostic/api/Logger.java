package computer.heather.goagnostic.api;

import java.util.function.Consumer;

/*
This is the logger that classes using Go!Agnostic are able to use.
It has the same usage irrespective of versions, and uses version / loader specific logging implementations under the hood.
*/


public class Logger {

    private final Consumer<String> info;
    private final Consumer<String> warn;
    private final Consumer<String> error;

    public Logger(
        Consumer<String> info,
        Consumer<String> warn,
        Consumer<String> error
    ) {
        this.info = info;
        this.warn = warn;
        this.error = error;
    }


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
