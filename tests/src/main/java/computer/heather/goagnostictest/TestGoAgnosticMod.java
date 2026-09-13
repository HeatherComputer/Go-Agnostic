package computer.heather.goagnostictest;


import computer.heather.goagnostic.api.annotations.Mod;
import computer.heather.goagnostic.api.annotations.Mod.Target;
import computer.heather.goagnostic.api.baseclasses.GoAgnosticMod;


@Mod(modName = "testmodname", modid = "testmodid", targets = { Target.NEO_1_21 }, version = "1")
public class TestGoAgnosticMod extends GoAgnosticMod {

    /*
    
    Time to test this out!

    */



    public TestGoAgnosticMod(String modName, String modId, GoAgnosticMod.Logger logger) {
        super(modName, modId, logger);
    }

    public void onServerStarting() {
        LOGGER.info("Server is starting! From my test mod!!");
    }

    public void onServerStarted() {
        LOGGER.info("Server has started! From my test mod!!");
    }

    public void onServerStopping() {
        LOGGER.info("Server is stopping! From my test mod!!");
    }




}
