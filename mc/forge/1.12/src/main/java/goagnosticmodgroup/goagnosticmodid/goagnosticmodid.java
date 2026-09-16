package goagnosticmodgroup.goagnosticmodid;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.Logger;

import computer.heather.goagnostic.api.baseclasses.GoAgnosticMod;
import computer.heather.goagnostic.api.util.placeholders.GoAgnosticModPlaceholder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;



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



    @EventHandler
    public void preInit(FMLPreInitializationEvent event) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        LOGGER = event.getModLog();
        //Reflection's ugly. But this is the only way to instantiate the user mod class without knowing its name at compile time. This class does need to be compilable.
        Constructor<?> constructor = goAgnosticModClass.getDeclaredConstructor(String.class, String.class, GoAgnosticMod.Logger.class);
        goAgnosticMod = (GoAgnosticMod) constructor.newInstance(MOD_NAME, MOD_ID, new GoAgnosticMod.Logger(LOGGER::info, LOGGER::warn, LOGGER::error));
        LOGGER.info("Go!Agnostic Mod {} ({}) has been initialized.", MOD_NAME, MOD_ID);
    }


    public goagnosticmodid()
    {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

        @EventHandler
    public void onServerStarting(FMLServerStartingEvent event)
    {
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

}
