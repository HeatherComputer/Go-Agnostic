package goagnosticmodgroup.goagnosticmodid;

import java.util.function.Consumer;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import computer.heather.goagnostic.api.command.CommandContext;
import goagnosticmodgroup.goagnosticmodid.util.Player;
import goagnosticmodgroup.goagnosticmodid.util.CommandHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class goagnosticcommandnamecommand {
    public static void register(CommandDispatcher<CommandSourceStack> stack) {
        stack.register(Commands.literal("goagnosticcommandname"));
    }


    public static boolean require(CommandSourceStack source) {
        return true;
    }
}
