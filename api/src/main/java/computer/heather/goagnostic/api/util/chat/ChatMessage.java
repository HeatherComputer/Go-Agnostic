package computer.heather.goagnostic.api.util.chat;

import computer.heather.goagnostic.api.util.player.Player;



//What can we do with chat messages, especially when newer versions have signing?
public interface ChatMessage {
    

    public Player getSender();

    public String getText();

    //WARNING: On newer versions, this will lead to a message's signature being removed!
    //A workaround is to block the message and send a system message that visually looks the same.
    public void setText(String in);

    //Simply stops the message from being sent.
    public void blockMessage();


}
