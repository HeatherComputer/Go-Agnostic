package computer.heather.goagnostic.api.event;

import java.nio.file.Path;

import computer.heather.goagnostic.api.Logger;

public class InitEvent {
    
    private final Logger logger;
    private final Path serverDir;
    private final Path worldDir;

    public InitEvent(Logger logger, Path serverDir, Path worldDir) {
        this.logger = logger;
        this.serverDir = serverDir;
        this.worldDir = worldDir;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getServerDir() {
        return serverDir;
    }

    public Path getWorldDir() {
        return worldDir;
    }

}
