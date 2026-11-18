package goagnosticmodgroup.goagnosticmodid.util;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import computer.heather.goagnostic.api.command.CommandResult;
import goagnosticmodgroup.goagnosticmodid.goagnosticmodid;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.FolderName;

public class Server implements computer.heather.goagnostic.api.util.server.Server {

    @Override
    public void sendMessage(String message) {
        goagnosticmodid.server.sendMessage(new StringTextComponent(message), Util.NIL_UUID);
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
        return goagnosticmodid.server.getWorldPath(FolderName.ROOT).toFile().getParentFile();
    }

    @Override
    public CommandResult executeCommand(String command) {
        StringBuilder builder = new StringBuilder();
        AtomicBoolean success = new AtomicBoolean(); //this is just used to deal with effectively final bs
        ServerWorld serverWorld = goagnosticmodid.server.overworld();
        //ugh
        ICommandSource source = new ICommandSource() {

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

            @Override
            public void sendMessage(ITextComponent component, UUID uuid) {
                builder.append(component.getString() + "\n");
            }
            
        };
        CommandSource stack = new CommandSource(source, Vector3d.atLowerCornerOf(serverWorld.getSharedSpawnPos()), Vector2f.ZERO, serverWorld, 4, "goagnosticmodid command runner",
         new StringTextComponent("goagnosticmodid command runner"), goagnosticmodid.server, null)
            .withCallback((ctx, succeeded, result) -> {
                success.set(succeeded);
            });
            
        
        goagnosticmodid.server.getCommands().performCommand(stack, command);

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
