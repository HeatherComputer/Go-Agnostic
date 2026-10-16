package goagnosticmodgroup.goagnosticmodid.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Function;

import computer.heather.goagnostic.api.annotations.Command.Argument;
import computer.heather.goagnostic.api.command.CommandContext;
import computer.heather.goagnostic.api.util.StringReader.ParseException;
import computer.heather.goagnostic.api.util.chat.ChatSymbols.Colour;
import goagnosticmodgroup.goagnosticmodid.goagnosticcommandnamecommand;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
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

        try {
            CommandContext ctx;
            //parse args
            Argument[] arguments = method.getDeclaredAnnotationsByType(Argument.class);


            if (sender instanceof EntityPlayer) {
                Player player = new Player((EntityPlayer) sender);
                ctx = CommandContext.create(player, String.join(" ", args), arguments);
            }
            else {
                ctx = null; //TODO!
            }
            method.invoke(null, ctx);
        } catch (Exception e) {
            sender.sendMessage(new TextComponentString(Colour.RED + "An error occured:"));
            sender.sendMessage(new TextComponentString(Colour.RED + e.getMessage()));
        }
    }


}
