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
    @Argument(name="player", type=ArgumentType.PLAYER)
    @Argument (name = "bool", type = ArgumentType.BOOLEAN)
    @Argument (name = "int", type = ArgumentType.INTEGER)
    @Argument (name = "word", type = ArgumentType.WORD)
    @Argument (name = "quote", type = ArgumentType.QUOTABLE_STRING)
    @Argument (name = "rest", type = ArgumentType.GREEDY_STRING)
    public static void run(CommandContext ctx) {
        Player player = ctx.getArgument("player");
        ctx.source.sendMessage(player.getUUID());

        boolean bool = ctx.getArgument("bool");
        ctx.source.sendMessage(String.valueOf(bool));

        int integer = ctx.getArgument("int");
        ctx.source.sendMessage(String.valueOf(integer));

        String word = ctx.getArgument("word");
        ctx.source.sendMessage(String.valueOf(word));

        String quote = ctx.getArgument("quote");
        ctx.source.sendMessage(String.valueOf(quote));

        String rest = ctx.getArgument("rest");
        ctx.source.sendMessage(String.valueOf(rest));
    }


}
