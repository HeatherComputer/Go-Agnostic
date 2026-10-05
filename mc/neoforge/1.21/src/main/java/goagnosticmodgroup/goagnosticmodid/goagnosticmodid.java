package goagnosticmodgroup.goagnosticmodid;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import computer.heather.goagnostic.api.baseclasses.GoAgnosticMod;
import computer.heather.goagnostic.api.util.placeholders.GoAgnosticModPlaceholder;
import goagnosticmodgroup.goagnosticmodid.util.Player;
import goagnosticmodgroup.goagnosticmodid.util.chat.ChatHandler;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

@Mod(goagnosticmodid.MOD_ID)
public class goagnosticmodid {

    private static final Class<? extends GoAgnosticMod> goAgnosticModClass = GoAgnosticModPlaceholder.class; //Placeholder here. AccessTransformer will replace this with the user mod class at compile time!
    
    //And an instance of it
    private final GoAgnosticMod goAgnosticMod;

    public static final String MOD_NAME = "goagnosticmodname";
    public static final String MOD_ID = "goagnosticmodid";
    public static final String MOD_GROUP = "goagnosticmodgroup";

    public static MinecraftServer server;

    private static final Logger LOGGER = LogUtils.getLogger();

    public goagnosticmodid(IEventBus modEventBus) 
            throws NoSuchMethodException, SecurityException, InstantiationException, 
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        //Reflection's ugly. But this is the only way to instantiate the user mod class without knowing its name at compile time. This class does need to be compilable.
        Constructor<?> constructor = goAgnosticModClass.getDeclaredConstructor(String.class, String.class, GoAgnosticMod.Logger.class);
        goAgnosticMod = (GoAgnosticMod) constructor.newInstance(MOD_NAME, MOD_ID, new GoAgnosticMod.Logger(LOGGER::info, LOGGER::warn, LOGGER::error));
        LOGGER.info("Go!Agnostic Mod {} ({}) has been initialized.", MOD_NAME, MOD_ID);

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new ChatHandler());
        
        goAgnosticMod.internal_setPlayerSupplier(() ->
            server.getPlayerList()
                .getPlayers()
                .stream()
                .map(Player::new)
                .toArray(Player[]::new)
        );
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        server = event.getServer();
        goAgnosticMod.onServerStarting();
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        goAgnosticMod.onServerStarted();
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        goAgnosticMod.onServerStopping();
    }

    @SubscribeEvent 
    public void onPlayerConnect(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = new Player(event.getEntity()); 
        goAgnosticMod.playerJoin(player);
    }

    @SubscribeEvent 
    public void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = new Player(event.getEntity());
        goAgnosticMod.playerLeave(player);
    }


}
