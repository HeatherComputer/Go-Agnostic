package goagnosticmodgroup.goagnosticmodid.util.chat;

import computer.heather.goagnostic.api.util.chat.ChatMessage;
import computer.heather.goagnostic.api.util.player.Player;
import net.neoforged.neoforge.event.ServerChatEvent;

public class Message implements ChatMessage {

    private final Player sender;
    private String text;
    private ServerChatEvent event;


    public Message(Player sender, String text, ServerChatEvent event) {
        this.sender = sender;
        this.text = text;
        this.event = event;
    }

    @Override
    public Player getSender() {
        return sender;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String in) {
        this.text = in;
    }

    @Override
    public void blockMessage() {
        event.setCanceled(true);
    }
    
}
