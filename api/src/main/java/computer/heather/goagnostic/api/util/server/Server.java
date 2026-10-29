package computer.heather.goagnostic.api.util.server;

import java.io.File;

import computer.heather.goagnostic.api.command.CommandResult;
import computer.heather.goagnostic.api.command.CommandSource;

public interface Server extends CommandSource {
    
    default boolean isPlayer() {
        return false;
    }

    boolean isDedicated();

    File getWorldDir();

    CommandResult executeCommand(String command);


}
