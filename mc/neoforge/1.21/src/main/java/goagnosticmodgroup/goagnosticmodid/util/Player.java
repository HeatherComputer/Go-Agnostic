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

    @Override
    public int permissionLevel() {
        //what the fuck is this
        int[] perms = new int[] {4, 3, 2, 1, 0};
        for (int i : perms) {
            if (playerEntity.hasPermissions(i)) return i;
        }
        return 0;
    }


}
