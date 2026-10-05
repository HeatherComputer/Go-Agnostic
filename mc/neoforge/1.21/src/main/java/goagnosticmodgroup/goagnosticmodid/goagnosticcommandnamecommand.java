package goagnosticmodgroup.goagnosticmodid;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = "goagnosticmodid")
public class goagnosticcommandnamecommand {
    public static void register(CommandDispatcher<CommandSourceStack> stack) {
        stack.register(Commands.literal("goagnosticcommandname"));
    }


    public static boolean require(CommandSourceStack source) {
        return true;
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event){
        goagnosticcommandnamecommand.register(event.getDispatcher());
    }
}
