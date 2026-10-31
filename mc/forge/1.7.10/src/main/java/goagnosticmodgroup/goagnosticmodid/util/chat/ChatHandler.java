package goagnosticmodgroup.goagnosticmodid.util.chat;

import java.util.List;

import computer.heather.goagnostic.api.util.chat.ChatManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import goagnosticmodgroup.goagnosticmodid.goagnosticmodid;
import goagnosticmodgroup.goagnosticmodid.util.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.ServerChatEvent;

public class ChatHandler {
    

    public ChatHandler() {
        ChatManager.internal_setSystemSender(this::sendMessage);
    }

    public void sendMessage(String message) {
        ChatComponentText msg = new ChatComponentText(message);        
        List<EntityPlayer> playerList = goagnosticmodid.server.getConfigurationManager().playerEntityList;
        for(EntityPlayer player : playerList) {
            player.addChatComponentMessage(msg);
        }
    }

    @SubscribeEvent 
    public void onChatMessage(ServerChatEvent event) {
        Player player = new Player(getPlayerFromName(event.username));
        Message message = new Message(player, event.message, event);
        ChatManager.internal_onChat(message);
    }


    //wtf
    @SuppressWarnings("unchecked")
    private EntityPlayer getPlayerFromName(String name) {
        List<EntityPlayer> playerList = goagnosticmodid.server.getConfigurationManager().playerEntityList;
        for(EntityPlayer player : playerList) {
            if (player.getGameProfile().getName().equals(name)) {
                return player;
            }
        }
        //should never happen
        return null;
    }

}
