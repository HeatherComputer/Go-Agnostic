package goagnosticmodgroup.goagnosticmodid.util.chat;

import computer.heather.goagnostic.api.util.chat.ChatManager;
import goagnosticmodgroup.goagnosticmodid.goagnosticmodid;
import goagnosticmodgroup.goagnosticmodid.util.Player;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ChatHandler {
    

    public ChatHandler() {
        ChatManager.internal_setSystemSender(this::sendMessage);
    }


    public void sendMessage(String message) {
        goagnosticmodid.server.sendMessage(new StringTextComponent(message), Util.NIL_UUID);
    }


    @SubscribeEvent
    public void onChat(ServerChatEvent event) {
        Player player = new Player(event.getPlayer());
        Message message = new Message(player, event.getComponent().getString(), event);
        ChatManager.internal_onChat(message);
    }


}