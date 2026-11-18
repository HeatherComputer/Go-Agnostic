package goagnosticmodgroup.goagnosticmodid;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import computer.heather.goagnostic.api.baseclasses.GoAgnosticMod;
import computer.heather.goagnostic.api.util.placeholders.GoAgnosticModPlaceholder;
import goagnosticmodgroup.goagnosticmodid.util.Player;
import goagnosticmodgroup.goagnosticmodid.util.chat.ChatHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

@Mod(goagnosticmodid.MOD_ID)
public class goagnosticmodid
{

    private final GoAgnosticMod goAgnosticMod;

    public static final String MOD_NAME = "goagnosticmodname";
    public static final String MOD_ID = "goagnosticmodid";
    public static final String MOD_GROUP = "goagnosticmodgroup";

    public static MinecraftServer server;

    private static final Logger LOGGER = LogManager.getLogger();

    private static final Class<? extends GoAgnosticMod> goAgnosticModClass = GoAgnosticModPlaceholder.class; //Placeholder here. AccessTransformer will replace this with the user mod class at compile time!

    public goagnosticmodid() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        //Reflection's ugly. But this is the only way to instantiate the user mod class without knowing its name at compile time. This class does need to be compilable.
        Constructor<?> constructor = goAgnosticModClass.getDeclaredConstructor(String.class, String.class, GoAgnosticMod.Logger.class);
        goAgnosticMod = (GoAgnosticMod) constructor.newInstance(MOD_NAME, MOD_ID, new GoAgnosticMod.Logger(LOGGER::info, LOGGER::warn, LOGGER::error));
        LOGGER.info("Go!Agnostic Mod {} ({}) has been initialized.", MOD_NAME, MOD_ID);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ChatHandler());
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event)
    {
        server = event.getServer();
        goAgnosticMod.onServerStarting();
    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        goAgnosticMod.onServerStarted();
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        goAgnosticMod.onServerStopping();
    }

    @SubscribeEvent
    public void onPlayerConnected(PlayerLoggedInEvent event) {
        Player player = new Player(event.getPlayer()); 
        goAgnosticMod.playerJoin(player);
    }

    @SubscribeEvent
    public void onPlayerDisconnected(PlayerLoggedOutEvent event) {
        Player player = new Player(event.getPlayer()); 
        goAgnosticMod.playerLeave(player);
    }
    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        if (!event.phase.equals(TickEvent.Phase.START)) return;
        goAgnosticMod.serverTick();
    }

}
