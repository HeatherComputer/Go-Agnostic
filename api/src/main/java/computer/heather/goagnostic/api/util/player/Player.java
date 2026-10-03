package computer.heather.goagnostic.api.util.player;

import computer.heather.goagnostic.api.command.CommandSource;

/*
Sometimes a mod needs to interact with a player in some capacity. Very little will be available here:
For now, name, uuid, coordinates, whether or not they're crouching, and sending messages
maybe at some point we could consider some basic item handling, somehow?
*/

public interface Player extends CommandSource {


    public String getName();
    public String getUUID();
    
    public int getX();
    public int getY();
    public int getZ();

    public boolean isCrouching();


    default boolean isPlayer() {
        return true;
    }

} 
