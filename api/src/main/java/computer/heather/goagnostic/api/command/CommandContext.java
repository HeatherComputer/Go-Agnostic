package computer.heather.goagnostic.api.command;

/*
A context class needed for commands.
This is the sole thing passed into the execute method of a command. Args can be obtained from it. (how?)
*/
public class CommandContext {
    
    public final CommandSource source;

    public CommandContext(CommandSource source) {
        this.source = source;
    }

}
