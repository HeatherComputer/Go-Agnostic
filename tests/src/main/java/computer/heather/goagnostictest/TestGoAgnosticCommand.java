package computer.heather.goagnostictest;

import java.util.Random;

import computer.heather.goagnostic.api.annotations.Command;
import computer.heather.goagnostic.api.annotations.Command.Argument;
import computer.heather.goagnostic.api.annotations.Command.Executes;
import computer.heather.goagnostic.api.annotations.Command.Require;
import computer.heather.goagnostic.api.command.CommandContext;
import computer.heather.goagnostic.api.command.args.ArgumentType;
import computer.heather.goagnostic.api.util.chat.ChatSymbols.Colour;
import computer.heather.goagnostic.api.util.player.Player;

@Command(name = "gatest") 
public class TestGoAgnosticCommand {


    @Require
    public static boolean check(CommandContext ctx) {
        if (ctx.source.isPlayer()) {
            return !((Player)ctx.source).isCrouching();
        }
        return false;
    }

    @Executes
    public static void run(CommandContext ctx) {
        ctx.source.sendMessage("Hi! This test command works!!");
    }


    @Command(name = "red")
    public static class RedCommand {
        
        @Require
        public static boolean check(CommandContext ctx) {
            if (ctx.source.isPlayer()) {
                return !((Player)ctx.source).isCrouching();
            }
            return false;
        }

        @Executes
        @Argument(name="text", type=ArgumentType.GREEDY_STRING)
        public static void run(CommandContext ctx) {
            ctx.source.sendMessage(Colour.RED + ctx.getArgument("text"));
        }


    }


    @Command(name = "blue")
    public static class BlueCommand {
        
        @Require
        public static boolean check(CommandContext ctx) {
            if (ctx.source.isPlayer()) {
                return !((Player)ctx.source).isCrouching();
            }
            return false;
        }

        @Executes
        @Argument(name="text", type=ArgumentType.GREEDY_STRING)
        public static void run(CommandContext ctx) {
            ctx.source.sendMessage(Colour.BLUE + ctx.getArgument("text"));
        }


    }



}
