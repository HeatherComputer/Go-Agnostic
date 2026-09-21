package computer.heather.goagnostictest;


import computer.heather.goagnostic.api.annotations.Mod;
import computer.heather.goagnostic.api.annotations.Mod.Target;
import computer.heather.goagnostic.api.baseclasses.GoAgnosticMod;
import computer.heather.goagnostic.api.util.chat.ChatManager;
import computer.heather.goagnostic.api.util.chat.ChatMessage;
import computer.heather.goagnostic.api.util.chat.ChatSymbols.Colour;
import computer.heather.goagnostic.api.util.player.Player;


@Mod(modName = "Test Mod Name", modid = "testmodid", targets = { Target.NEO_1_21, Target.FORGE_1_12 }, 
version = "1", group = "Test Mod Group", authors = { "Heather" }, description = "This is a test mod for GoAgnostic", license = "BSD-3-Clause")
public class TestGoAgnosticMod extends GoAgnosticMod {

    /*
    
    Time to test this out!

    */



    public TestGoAgnosticMod(String modName, String modId, GoAgnosticMod.Logger logger) {
        super(modName, modId, logger);
        ChatManager.setChatListener(this::onChat);
    }

    @Override
    public void onServerStarting() {
        LOGGER.info("Server is starting! From my test mod!!");
    }

    @Override
    public void onServerStarted() {
        LOGGER.info("Server has started! From my test mod!!");
    }

    @Override
    public void onServerStopping() {
        LOGGER.info("Server is stopping! From my test mod!!");
    }

    @Override 
    public void playerJoin(Player player) {
        player.sendMessage(Colour.AQUA + "Hi!!");
    }

    public void onChat(ChatMessage message) {
        if (message.getSender().isCrouching()) {
            message.blockMessage();
            message.getSender().sendMessage(Colour.RED + "You are muted!");
        }
        else {
            ChatManager.sendSystemMessage("Oi Oi");
        }
        if (message.getText().contains("marco")) {
            for (Player player : getPlayers()) {
                LOGGER.info(player.getName());
                player.sendMessage("POLO!");
            }
        } 
    }




}
