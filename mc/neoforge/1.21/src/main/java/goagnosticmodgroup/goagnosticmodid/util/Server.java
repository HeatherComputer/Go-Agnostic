package goagnosticmodgroup.goagnosticmodid.util;

import java.io.File;

import computer.heather.goagnostic.api.command.CommandResult;
import goagnosticmodgroup.goagnosticmodid.goagnosticmodid;

public class Server implements computer.heather.goagnostic.api.util.server.Server {

    @Override
    public void sendMessage(String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendMessage'");
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getWorldDir'");
    }

    @Override
    public CommandResult executeCommand(String command) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeCommand'");
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
