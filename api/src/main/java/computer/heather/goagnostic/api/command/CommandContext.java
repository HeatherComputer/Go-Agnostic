package computer.heather.goagnostic.api.command;

import java.util.Map;

/*
A context class needed for commands.
This is the sole thing passed into the execute method of a command. Args can be obtained from it. (how?)
*/
public class CommandContext {
    
    public final CommandSource source;
    private final Map<String, ?> args;

    public CommandContext(CommandSource source, Map<String,?> args) {
        this.source = source;
        this.args = args;
    }

    @SuppressWarnings("unchecked")
	public <T> T getArgument(String arg) {
        return (T) args.get(arg);
    }

}
