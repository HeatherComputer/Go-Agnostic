package computer.heather.goagnostic.api.command;

public interface CommandSource {
    
    public boolean isPlayer();

    public void sendMessage(String message);

    public int permissionLevel();
}
