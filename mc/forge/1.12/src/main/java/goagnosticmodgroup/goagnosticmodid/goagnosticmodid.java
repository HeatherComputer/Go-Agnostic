package goagnosticmodgroup.goagnosticmodid;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.Logger;

import computer.heather.goagnostic.api.baseclasses.GoAgnosticMod;
import computer.heather.goagnostic.api.util.placeholders.GoAgnosticModPlaceholder;
import goagnosticmodgroup.goagnosticmodid.util.CommandHandler;
import goagnosticmodgroup.goagnosticmodid.util.Player;
import goagnosticmodgroup.goagnosticmodid.util.chat.ChatHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;



@Mod(modid = goagnosticmodid.MOD_ID, name = goagnosticmodid.MOD_ID, version = goagnosticmodid.MOD_VERSION)
@EventBusSubscriber
public class goagnosticmodid
{

    
    private static final Class<? extends GoAgnosticMod> goAgnosticModClass = GoAgnosticModPlaceholder.class; //Placeholder here. AccessTransformer will replace this with the user mod class at compile time!
    
    //And an instance of it
    private GoAgnosticMod goAgnosticMod;

    public static final String MOD_NAME = "goagnosticmodname";
    public static final String MOD_ID = "goagnosticmodid";
    public static final String MOD_GROUP = "goagnosticmodgroup";
    public static final String MOD_VERSION = "goagnosticmodversion";

    private static Logger LOGGER;

    public static MinecraftServer server;



    @EventHandler
    public void preInit(FMLPreInitializationEvent event) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        LOGGER = event.getModLog();
        //Reflection's ugly. But this is the only way to instantiate the user mod class without knowing its name at compile time. This class does need to be compilable.
        Constructor<?> constructor = goAgnosticModClass.getDeclaredConstructor(String.class, String.class, GoAgnosticMod.Logger.class);
        goAgnosticMod = (GoAgnosticMod) constructor.newInstance(MOD_NAME, MOD_ID, new GoAgnosticMod.Logger(LOGGER::info, LOGGER::warn, LOGGER::error));
        goAgnosticMod.internal_setPlayerSupplier(this::getPlayers);
        LOGGER.info("Go!Agnostic Mod {} ({}) has been initialized.", MOD_NAME, MOD_ID);
    }


    public goagnosticmodid()
    {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ChatHandler());
    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event) throws InstantiationException, IllegalAccessException
    {
        try {
            CommandHandler.Registrar.registerCommands(event);
        } catch (NoClassDefFoundError e) {
            //Mod has no commands
        }
        server = event.getServer();
        goAgnosticMod.onServerStarting();
        
    }

    @EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        goAgnosticMod.onServerStarted();   
    }

    @EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        goAgnosticMod.onServerStopping();
    }

    @SubscribeEvent  
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = new Player(event.player);
        goAgnosticMod.playerJoin(player);
    }

    @SubscribeEvent 
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = new Player(event.player);
        goAgnosticMod.playerLeave(player);
    }






    /*
    Below consists of some internal methods that aren't useful to the user.
    */

    public Player[] getPlayers() {
        EntityPlayer[] onlinePlayers = server.getPlayerList().getPlayers().toArray(new EntityPlayer[0]);
        Player[] result = new Player[onlinePlayers.length];
        for (int i = 0; i < onlinePlayers.length; i++) {
            result[i] = new Player(onlinePlayers[i]);
        }
        return result;
    }

}
