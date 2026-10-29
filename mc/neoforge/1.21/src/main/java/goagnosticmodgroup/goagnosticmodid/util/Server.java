package goagnosticmodgroup.goagnosticmodid.util;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import computer.heather.goagnostic.api.command.CommandResult;
import goagnosticmodgroup.goagnosticmodid.goagnosticmodid;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelResource;

public class Server implements computer.heather.goagnostic.api.util.server.Server {

    @Override
    public void sendMessage(String message) {
        goagnosticmodid.server.sendSystemMessage(Component.literal(message));
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
        return goagnosticmodid.server.getWorldPath(LevelResource.ROOT).toFile().getParentFile();
    }

    @Override
    public CommandResult executeCommand(String command) {
        StringBuilder builder = new StringBuilder();
        AtomicBoolean success = new AtomicBoolean(); //this is just used to deal with effectively final bs
        //ugh
        CommandSource source = new CommandSource() {

            @Override
            public void sendSystemMessage(Component component) {
                builder.append(component.getString() + "\n");
            }

            @Override
            public boolean acceptsSuccess() {
                return true;
            }

            @Override
            public boolean acceptsFailure() {
                return true;
            }

            @Override
            public boolean shouldInformAdmins() {
                return true;
            }
            
        };
        CommandSourceStack stack = goagnosticmodid.server.createCommandSourceStack().withSource(source)
            .withCallback((succeeded, result) -> {
                success.set(succeeded);
            });
        
        goagnosticmodid.server.getCommands().performPrefixedCommand(stack, command);

        return new CommandResult(builder.toString(), success.get());
    }

    @Override
    public Player[] getPlayers() {
        return goagnosticmodid.server.getPlayerList()
                .getPlayers()
                .stream()
                .map(Player::new)
                .toArray(Player[]::new);
    }
    
}
