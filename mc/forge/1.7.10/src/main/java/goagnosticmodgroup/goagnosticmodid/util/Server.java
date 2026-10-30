package goagnosticmodgroup.goagnosticmodid.util;

import java.io.File;
import java.util.List;
import java.util.StringJoiner;

import computer.heather.goagnostic.api.command.CommandResult;
import computer.heather.goagnostic.api.util.player.Player;
import goagnosticmodgroup.goagnosticmodid.goagnosticmodid;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class Server implements computer.heather.goagnostic.api.util.server.Server {
    

    @Override
    public void sendMessage(String message) {
        goagnosticmodid.server.addChatMessage(new ChatComponentText(message));
    }

    @Override
    public int permissionLevel() {
        return 4;
    }

    @Override
    public boolean isDedicated() {
        return goagnosticmodid.server.isDedicatedServer();
    }

    @Override
    public File getWorldDir() {
        return new File(goagnosticmodid.server.getFolderName());
    }

    @Override
    public CommandResult executeCommand(String command) {
        StringJoiner joiner = new StringJoiner("\n");
        
        int result = goagnosticmodid.server.getCommandManager().executeCommand(new ICommandSender() {

            @Override
            public String getCommandSenderName() {
                return "goagnosticmodid command runner";
            }

            @Override
            public World getEntityWorld() {
                return goagnosticmodid.server.getEntityWorld();
            }

            //This method is completely undocumented. This is what the RCON commandrunner doesw though
            @Override
            public IChatComponent func_145748_c_() {
                return new ChatComponentText(this.getCommandSenderName());
            }

            @Override
            public void addChatMessage(IChatComponent message) {
                joiner.add(message.getFormattedText());
            }

            @Override
            public boolean canCommandSenderUseCommand(int permissionLevel, String command) {
                return true;
            }

            public ChunkCoordinates getPlayerCoordinates()
            {
                return new ChunkCoordinates(0, 0, 0);
            }
            
        }, command);

        return new CommandResult(joiner.toString(), result > 0);
    }

    @SuppressWarnings("unchecked")
    public Player[] getPlayers() {
        List<EntityPlayerMP> onlinePlayers = goagnosticmodid.server.getConfigurationManager().playerEntityList;
        Player[] result = new Player[onlinePlayers.size()];
        for (int i = 0; i < onlinePlayers.size(); i++) {
            result[i] = new goagnosticmodgroup.goagnosticmodid.util.Player(onlinePlayers.get(i));
        }
        return result;
    }
    
}
