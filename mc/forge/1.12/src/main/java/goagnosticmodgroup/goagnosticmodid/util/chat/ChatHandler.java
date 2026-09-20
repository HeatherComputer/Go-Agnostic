package goagnosticmodgroup.goagnosticmodid.util.chat;

import computer.heather.goagnostic.api.util.chat.ChatManager;
import goagnosticmodgroup.goagnosticmodid.goagnosticmodid;
import goagnosticmodgroup.goagnosticmodid.util.Player;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatHandler {
    

    public ChatHandler() {
        ChatManager.internal_setSystemSender(this::sendMessage);
    }

    public void sendMessage(String message) {
        ITextComponent msg = new TextComponentString(message);
        goagnosticmodid.server.getPlayerList().getPlayers().forEach((player) -> player.sendMessage(msg));
    }

    @SubscribeEvent 
    public void onChatMessage(ServerChatEvent event) {
        Player player = new Player(event.getPlayer());
        Message message = new Message(player, event.getComponent().getUnformattedText(), event);
        ChatManager.internal_onChat(message);
    }

}
