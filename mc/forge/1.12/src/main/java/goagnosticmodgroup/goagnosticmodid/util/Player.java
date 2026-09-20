package goagnosticmodgroup.goagnosticmodid.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;

public class Player implements computer.heather.goagnostic.api.util.player.Player {


    public Player(EntityPlayer player) {
        this.player = player;
    }

    private final EntityPlayer player;


    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public String getUUID() {
        return player.getName();
    }

    @Override
    public int getX() {
        return player.getPosition().getX();
    }

    @Override
    public int getY() {
        return player.getPosition().getY();
    }

    @Override
    public int getZ() {
        return player.getPosition().getZ();
    }

    @Override
    public boolean isCrouching() {
        return player.isSneaking();
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(new TextComponentString(message));
    }
    
}
