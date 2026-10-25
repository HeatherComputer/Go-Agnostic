package goagnosticmodgroup.goagnosticmodid.util;

import javax.annotation.Nullable;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.command.CommandTreeBase;


public abstract class GACommandBase extends CommandTreeBase {

    @Override
    public String getUsage(ICommandSender sender) {
        //TODO consider improving this in future
        return getName();
    }

        
    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }
    
    
    //Default allow permission, means the actual command just adds the method IF the user wants a check
    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    

    //Modified version of super's execute, because CommandTreeBase doesn't support subcommandS AND executions
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        //Check for a subcommand first
        if (args.length > 0) {
            ICommand cmd = getSubCommand(args[0]);
            if (cmd != null) {
                //Can't ditch permission checks
                if (cmd.checkPermission(server, sender)) cmd.execute(server, sender, shiftArgs(args));
                else throw new CommandException("commands.generic.permission");
                
                return;
            }
        }
        //If we're here either there's no called subcommand or there's no args
        gaexecute(server, sender, args);
    }

    //This gets overridden
    public void gaexecute(MinecraftServer server, ICommandSender sender, String[] args) {
        sender.sendMessage(new TextComponentString(getUsage(sender)));
    }

    //This should be visible in super.. it's not. Literally just copy paste of super's impl
    private static String[] shiftArgs(@Nullable String[] s) {
        if(s == null || s.length == 0)
        {
            return new String[0];
        }

        String[] s1 = new String[s.length - 1];
        System.arraycopy(s, 1, s1, 0, s1.length);
        return s1;
    }
}
