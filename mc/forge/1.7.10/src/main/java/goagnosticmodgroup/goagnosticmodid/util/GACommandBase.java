package goagnosticmodgroup.goagnosticmodid.util;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

//no subcommand to extend off of here...
public abstract class GACommandBase extends CommandBase {

    //Map of subcommands
    private final Map<String, ICommand> subcommands = new HashMap<>();

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return getCommandName(); //TODO consider improving
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        //Check for a subcommand first
        if (args.length > 0) {
            ICommand cmd = getSubCommand(args[0]);
            if (cmd != null) {
                //Can't ditch permission checks
                if (cmd.canCommandSenderUseCommand(sender)) cmd.processCommand(sender, shiftArgs(args));
                else throw new CommandException("commands.generic.permission");
                
                return;
            }
        }
        //If we're here either there's no called subcommand or there's no args
        gaexecute(sender, args);
    }

    //This gets overridden
    public void gaexecute(ICommandSender sender, String[] args) {
        sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
    
    
    //Default allow permission, means the actual command just adds the method IF the user wants a check
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    //Add a subcommand
    public void addSubcommand(ICommand command) {
        subcommands.put(command.getCommandName(), command);
    }

    //Get a subcommand
    @Nullable
    public ICommand getSubCommand(String command) {
        ICommand subcommand = subcommands.get(command);
        return subcommand;
    }

    //For figuring out what args subcommands need
    protected static String[] shiftArgs(@Nullable String[] s) {
        if(s == null || s.length == 0) {
            return new String[0];
        }

        String[] s1 = new String[s.length - 1];
        System.arraycopy(s, 1, s1, 0, s1.length);
        return s1;
    }
    
}
