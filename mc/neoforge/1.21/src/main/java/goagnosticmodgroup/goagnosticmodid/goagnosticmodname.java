package goagnosticmodgroup.goagnosticmodid;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import computer.heather.goagnostic.api.baseclasses.GoAgnosticMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

@Mod(goagnosticmodname.MOD_ID)
public class goagnosticmodname {

    private final GoAgnosticMod goAgnosticMod = null; //Null here. Will defer to user code!

    public static final String MOD_NAME = "goagnosticmodname";
    public static final String MOD_ID = "goagnosticmodid";

    private static final Logger LOGGER = LogUtils.getLogger();

    public goagnosticmodname(IEventBus modEventBus) {
        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(this);

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
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

}
