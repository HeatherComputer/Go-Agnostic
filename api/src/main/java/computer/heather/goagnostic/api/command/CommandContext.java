package computer.heather.goagnostic.api.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import computer.heather.goagnostic.api.annotations.Command.Argument;
import computer.heather.goagnostic.api.util.StringReader;
import computer.heather.goagnostic.api.util.StringReader.ParseException;

/*
A context class needed for commands.
This is the sole thing passed into the execute method of a command. Args can be obtained from it.
*/
public class CommandContext {
    
    public final CommandSource source;
    private final Map<String, Object> args;

    public CommandContext(CommandSource source, Map<String, Object> args) {
        this.source = source;
        this.args = args;
    }

    @SuppressWarnings("unchecked")
	public <T> T getArgument(String arg) {
        return (T) args.get(arg);
    }

    /*
    Parses arguments and returns a commandcontext containing them.
    Requires the actual argument string, and a list of expected arguments.
     */
    public static CommandContext create(CommandSource source, String args, Argument[] arguments) throws ParseException {
        StringReader reader = new StringReader(args);
        ArrayList<Object> parsedArgs = new ArrayList<>();

        for (Argument argument : arguments) {
            reader.skipWhitespace();
            if (!reader.canRead()) break;
            parsedArgs.add(argument.type().parse(reader));
        }

        if (parsedArgs.size() != arguments.length) {
            throw new ParseException("Parsed arguments did not match expected argument length.", "Expected count:", String.valueOf(arguments.length), 
                "Parsed count:", String.valueOf(parsedArgs.size()), 
                "Input string:", args);
        }

        HashMap<String, Object> finalArgs = new HashMap<>();
        for (int i=0;i<arguments.length;i++) {
            finalArgs.put(arguments[i].name(), parsedArgs.get(i));
        }

        return new CommandContext(source, finalArgs);
    }


}
