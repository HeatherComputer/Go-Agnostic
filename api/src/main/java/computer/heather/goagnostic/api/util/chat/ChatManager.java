package computer.heather.goagnostic.api.util.chat;

import java.util.function.Consumer;

public class ChatManager {
    
    private static Consumer<ChatMessage> chatListener = (msg) -> {};
    private static Consumer<String> systemSender;

    //Mods use this if they're interested in listening to chat.
    public static void setChatListener(Consumer<ChatMessage> consumer) {
        chatListener = consumer;
    }


    //Send a message.
    public static void sendSystemMessage(String message) {
        systemSender.accept(message);
    }


    //Go!Agnostic's version-specific impl uses this. If a mod uses this, it'll just fire its own chat listener...
    public static void internal_onChat(ChatMessage message) {

        chatListener.accept(message);

    }

    public static void internal_setSystemSender(Consumer<String> sender) {
        systemSender = sender;
    }

}
