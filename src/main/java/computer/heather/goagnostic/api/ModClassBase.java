package computer.heather.goagnostic.api;

import computer.heather.goagnostic.api.event.InitEvent;
import computer.heather.goagnostic.api.event.ServerStartedEvent;

/*
This is what all mods should extend.
This is enforced by the annotation processor.
Most methods do not need to be overriden if the mod does not require them.
*/

public abstract class ModClassBase {
    
        
    public void init(InitEvent event) {}
    public void serverStarted(ServerStartedEvent event) {}


}
