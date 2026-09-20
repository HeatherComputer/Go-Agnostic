package goagnosticmodgroup.goagnosticmodid.util.chat;

import computer.heather.goagnostic.api.util.chat.ChatManager;
import goagnosticmodgroup.goagnosticmodid.goagnosticmodid;
import goagnosticmodgroup.goagnosticmodid.util.Player;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.ServerChatEvent;

public class ChatHandler {
    

    public ChatHandler() {
        ChatManager.internal_setSystemSender(this::sendMessage);
    }


    public void sendMessage(String message) {
        goagnosticmodid.server.sendSystemMessage(Component.literal(message));
    }


    @SubscribeEvent
    public void onChat(ServerChatEvent event) {
        Player player = new Player(event.getPlayer());
        Message message = new Message(player, event.getRawText(), event);
        ChatManager.internal_onChat(message);
    }


}
