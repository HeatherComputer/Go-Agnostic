package goagnosticmodgroup.goagnosticmodid.util;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
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
}
