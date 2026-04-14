package computer.heather.goagnostic.api.event;

import java.nio.file.Path;

import computer.heather.goagnostic.api.Logger;

public class InitEvent {
    
    private final Logger logger;
    private final String worldName;
    private final Path worldDir;

    public InitEvent(Logger logger, String worldName, Path worldDir) {
        this.logger = logger;
        this.worldName = worldName;
        this.worldDir = worldDir;
    }

    public Logger getLogger() {
        return logger;
    }

    public String getWorldName() {
        return worldName;
    }

    public Path getWorldDir() {
        return worldDir;
    }

}
