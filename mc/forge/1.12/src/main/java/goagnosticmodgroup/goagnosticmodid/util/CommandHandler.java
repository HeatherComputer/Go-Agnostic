package goagnosticmodgroup.goagnosticmodid.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Function;

import computer.heather.goagnostic.api.command.CommandContext;
import goagnosticmodgroup.goagnosticmodid.goagnosticcommandnamecommand;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class CommandHandler {

    public static class RequireChecker {
        public static boolean checkPermission(MinecraftServer server, ICommandSender sender, Function<CommandContext, Boolean> checker){
                CommandContext ctx;
                if (sender instanceof EntityPlayer) {
                    Player player = new Player((EntityPlayer) sender);
                    ctx = new CommandContext(player, null);
                }
                else {
                    ctx = null; //TODO!
                }
                return checker.apply(ctx);
 
        }
    }

    //This isn't copied across by default, which lets our commandgen nicely sort it whilst also adding modification
    public static class Registrar {

        //This function is what our commandgen injects into
        public static Class<?>[] getCommands() {
            return new Class<?>[] {
            };
        }

        public static void registerCommands(FMLServerStartingEvent event) throws InstantiationException, IllegalAccessException {
            for (Class<?> clasz : getCommands()) {
                event.registerServerCommand((ICommand) clasz.newInstance());
            }
        }

    }


    public static void execute(MinecraftServer server, ICommandSender sender, String[] args, Method method) {

        CommandContext ctx;
        if (sender instanceof EntityPlayer) {
            Player player = new Player((EntityPlayer) sender);
            ctx = new CommandContext(player, null);
        }
        else {
            ctx = null; //TODO!
        }
        try {
            method.invoke(ctx);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
