package goagnosticmodgroup.goagnosticmodid.util;

import java.util.function.Consumer;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import computer.heather.goagnostic.api.command.CommandContext;

import net.minecraft.commands.CommandSourceStack;

public class CommandHandler implements Command<CommandSourceStack> {

    private final Consumer<CommandContext> runner;

    public CommandHandler(Consumer<CommandContext> runner) {
        this.runner = runner;
    }

    @Override
    public int run(com.mojang.brigadier.context.CommandContext<CommandSourceStack> in)
            throws CommandSyntaxException {
        

        CommandSourceStack source = in.getSource();
        CommandContext ctx;
        if (source.isPlayer()) {
            Player player = new Player(source.getPlayer());
            ctx = new CommandContext(player);
        }
        else {
            ctx = null; //TODO!
        }

        runner.accept(ctx);

        return 0;

    }

}