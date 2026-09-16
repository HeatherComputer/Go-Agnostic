package computer.heather.goagnostictest;


import computer.heather.goagnostic.api.annotations.Mod;
import computer.heather.goagnostic.api.annotations.Mod.Target;
import computer.heather.goagnostic.api.baseclasses.GoAgnosticMod;


@Mod(modName = "Test Mod Name", modid = "testmodid", targets = { Target.NEO_1_21, Target.FORGE_1_12 }, 
version = "1", group = "Test Mod Group", authors = { "Heather" }, description = "This is a test mod for GoAgnostic", license = "BSD-3-Clause")
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
