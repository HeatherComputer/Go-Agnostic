package goagnosticmodgroup.goagnosticmodid;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = "goagnosticmodid")
public class goagnosticcommandnamecommand {
    public static void register(CommandDispatcher<CommandSource> stack) {
        stack.register(Commands.literal("goagnosticcommandname"));
    }

    
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event){
        goagnosticcommandnamecommand.register(event.getDispatcher());
    }

}
