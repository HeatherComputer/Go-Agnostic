package computer.heather.goagnostictest;

import computer.heather.goagnostic.api.annotations.Command;
import computer.heather.goagnostic.api.annotations.Command.Executes;
import computer.heather.goagnostic.api.command.CommandContext;

@Command(name = "gatest") 
public class TestGoAgnosticCommand {



    @Executes
    public static void run(CommandContext ctx) {
        ctx.source.sendMessage("Hi! This test command works!!");
    }


    @Command(name = "red")
    public static class RedCommand {

        @Executes
        public static void run(CommandContext ctx) {
            
        }

    }



}
