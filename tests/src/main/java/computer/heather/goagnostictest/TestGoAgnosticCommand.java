package computer.heather.goagnostictest;

import java.util.Random;

import computer.heather.goagnostic.api.annotations.Command;
import computer.heather.goagnostic.api.annotations.Command.Executes;
import computer.heather.goagnostic.api.annotations.Command.Require;
import computer.heather.goagnostic.api.command.CommandContext;

@Command(name = "gatest") 
public class TestGoAgnosticCommand {


    @Require
    public static boolean check(CommandContext ctx) {
        return new Random().nextBoolean();
    }

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
