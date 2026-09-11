package goagnosticmodgroup.goagnosticmodname;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

@Mod(goagnosticmodname.MOD_ID)
public class goagnosticmodname {

    public static final String MOD_NAME = "goagnosticmodname";
    public static final String MOD_ID = "goagnosticmodid";

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final ArrayList<String> players = new ArrayList<>();

    public goagnosticmodname(IEventBus modEventBus) {
        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(this);

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {

    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {

    }

}
