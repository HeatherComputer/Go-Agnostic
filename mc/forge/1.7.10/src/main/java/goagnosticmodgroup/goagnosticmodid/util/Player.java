package goagnosticmodgroup.goagnosticmodid.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

public class Player implements computer.heather.goagnostic.api.util.player.Player{ 

    private EntityPlayer player;

    public Player(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void sendMessage(String message) {
        player.addChatMessage(new ChatComponentText(message));
    }

    @Override
    public int permissionLevel() {
        //what the fuck is this
        int[] perms = new int[] {4, 3, 2, 1, 0};
        for (int i : perms) {
            if (player.canCommandSenderUseCommand(i, "")) return i;
        }
        return 0;
    }

    @Override
    public String getName() {
        return player.getGameProfile().getName();
    }

    @Override
    public String getUUID() {
        return player.getGameProfile().getId().toString();
    }

    @Override
    public int getX() {
        return (int) player.posX;
    }

    @Override
    public int getY() {
        return (int) player.posY;
    }

    @Override
    public int getZ() {
        return (int) player.posZ;
    }

    @Override
    public boolean isCrouching() {
        return player.isSneaking();
    }
    
}
