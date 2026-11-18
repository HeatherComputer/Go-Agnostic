package goagnosticmodgroup.goagnosticmodid.util;

import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;

public class Player implements computer.heather.goagnostic.api.util.player.Player {
    

    private final net.minecraft.entity.player.PlayerEntity playerEntity;

    public Player(net.minecraft.entity.player.PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    @Override
    public String getName() {
        return playerEntity.getGameProfile().getName();
    }

    @Override
    public String getUUID() {
        return playerEntity.getGameProfile().getId().toString();
    }

    @Override
    public int getX() {
        return playerEntity.blockPosition().getX();
    }

    @Override
    public int getY() {
        return playerEntity.blockPosition().getY();
    }

    @Override
    public int getZ() {
        return playerEntity.blockPosition().getY();
    }

    @Override
    public boolean isCrouching() {
        return playerEntity.isCrouching();
    }

    @Override
    public void sendMessage(String message) {
        playerEntity.sendMessage(new StringTextComponent(message), Util.NIL_UUID);
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
