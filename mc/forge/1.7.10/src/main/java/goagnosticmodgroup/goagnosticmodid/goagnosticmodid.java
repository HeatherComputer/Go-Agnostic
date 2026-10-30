package goagnosticmodgroup.goagnosticmodid;

import net.minecraft.command.CommandHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;

import org.apache.logging.log4j.Logger;

import computer.heather.goagnostic.api.baseclasses.GoAgnosticMod;
import computer.heather.goagnostic.api.util.placeholders.GoAgnosticModPlaceholder;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import goagnosticmodgroup.goagnosticmodid.util.Player;
import goagnosticmodgroup.goagnosticmodid.util.Server;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.function.Consumer;

@Mod(modid = goagnosticmodid.MOD_ID, name = goagnosticmodid.MOD_NAME, acceptableRemoteVersions = "*")
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
    public void preInit(FMLPreInitializationEvent event) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
    {
        LOGGER = event.getModLog();
        //Reflection's ugly. But this is the only way to instantiate the user mod class without knowing its name at compile time. This class does need to be compilable.
        Constructor<?> constructor = goAgnosticModClass.getDeclaredConstructor(String.class, String.class, GoAgnosticMod.Logger.class);
        goAgnosticMod = (GoAgnosticMod) constructor.newInstance(MOD_NAME, MOD_ID, new GoAgnosticMod.Logger(LOGGER::info, LOGGER::warn, LOGGER::error));
        goAgnosticMod.internal_setServerSupplier(() -> new Server());
        LOGGER.info("Go!Agnostic Mod {} ({}) has been initialized.", MOD_NAME, MOD_ID);
    }



    public goagnosticmodid()
    {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event)
    {
        try {
            //CommandHandler.Registrar.registerCommands(event);
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
    public void onPlayerConnected(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = new Player(event.player);
        goAgnosticMod.playerJoin(player);
    }

    @SubscribeEvent 
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = new Player(event.player);
        goAgnosticMod.playerLeave(player);
    }

    @SubscribeEvent
    public void onTickStart(TickEvent.ServerTickEvent event) {
        if (!event.phase.equals(TickEvent.Phase.START)) return;
        goAgnosticMod.serverTick();
    }

}
