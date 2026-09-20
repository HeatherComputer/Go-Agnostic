package goagnosticmodgroup.goagnosticmodid.util;

import net.minecraft.network.chat.Component;

public class Player implements computer.heather.goagnostic.api.util.player.Player {
    

    private final net.minecraft.world.entity.player.Player playerEntity;

    public Player(net.minecraft.world.entity.player.Player playerEntity) {
        this.playerEntity = playerEntity;
    }

    @Override
    public String getName() {
        return playerEntity.getName().getString();
    }

    @Override
    public String getUUID() {
        return playerEntity.getUUID().toString();
    }

    @Override
    public int getX() {
        return playerEntity.getBlockX();
    }

    @Override
    public int getY() {
        return playerEntity.getBlockY();
    }

    @Override
    public int getZ() {
        return playerEntity.getBlockZ();
    }

    @Override
    public boolean isCrouching() {
        return playerEntity.isCrouching();
    }

    @Override
    public void sendMessage(String message) {
        playerEntity.sendSystemMessage(Component.literal(message));
    }


}
