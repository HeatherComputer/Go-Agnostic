package goagnosticmodgroup.goagnosticmodid.util;

import java.io.File;
import java.util.StringJoiner;

import computer.heather.goagnostic.api.command.CommandResult;
import goagnosticmodgroup.goagnosticmodid.goagnosticmodid;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class Server implements computer.heather.goagnostic.api.util.server.Server{

    @Override
    public void sendMessage(String message) {
        goagnosticmodid.server.sendMessage(new TextComponentString(message));
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
        if (isDedicated()) {
            return new File(goagnosticmodid.server.getFolderName());
        }
        return new File("saves/" + goagnosticmodid.server.getFolderName());
    }

    @Override
    public CommandResult executeCommand(String command) {
        StringJoiner joiner = new StringJoiner("\n");
        
        int result = goagnosticmodid.server.commandManager.executeCommand(new ICommandSender() {

            @Override
            public String getName() {
                return "goagnosticmodid command runner";
            }

            @Override
            public boolean canUseCommand(int permLevel, String commandName) {
                return true;
            }

            @Override
            public World getEntityWorld() {
                return goagnosticmodid.server.getEntityWorld();
            }

            @Override
            public MinecraftServer getServer() {
                return goagnosticmodid.server;
            }

            @Override 
            public void sendMessage(ITextComponent component) {
                joiner.add(component.getFormattedText());
            }
            
        }, command);

        return new CommandResult(joiner.toString(), result > 0);

    }
    
}
