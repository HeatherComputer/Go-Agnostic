package computer.heather.goagnostic.api.util.server;

import computer.heather.goagnostic.api.command.CommandSource;

public interface Server extends CommandSource {
    
    default boolean isPlayer() {
        return false;
    }


}
