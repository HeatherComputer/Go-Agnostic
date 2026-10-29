package computer.heather.goagnostic.api.command.args;

import computer.heather.goagnostic.api.baseclasses.GoAgnosticMod;
import computer.heather.goagnostic.api.util.StringReader;
import computer.heather.goagnostic.api.util.StringReader.ParseException;
import computer.heather.goagnostic.api.util.player.Player;

public enum ArgumentType {

    //String types
    WORD,
    GREEDY_STRING,
    QUOTABLE_STRING,

    //Int
    INTEGER,

    //Player
    PLAYER,

    //Boolean
    BOOLEAN;

    //What else do we need?

    public Object parse(StringReader reader) throws ParseException {

        switch(this) {
			case BOOLEAN:
				return reader.readBoolean();
			case GREEDY_STRING:
				return reader.readAll();
			case INTEGER:
				return reader.readInt();
			case PLAYER: {
                Player[] players = GoAgnosticMod.getInstance().getServer().getPlayers();
                String username = reader.readString(); 
                for (Player player : players) {
                    //Check username and then UUID.
                    if (player.getName().equals(username)) return player;
                    if (player.getUUID().equals(username)) return player;
                }
                //Throw if player couldn't be found
                throw new ParseException("Could not find an online player matching username/uuid!", username);
            }
			case QUOTABLE_STRING:
				return reader.readString();
			case WORD:
                return reader.readUnquotedString();            
        }

        return null;
        
    }

}