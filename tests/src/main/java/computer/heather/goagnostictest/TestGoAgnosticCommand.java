package computer.heather.goagnostictest;

import computer.heather.goagnostic.api.annotations.Command;
import computer.heather.goagnostic.api.annotations.Command.Executes;

@Command(name = "gatest") 
public class TestGoAgnosticCommand {



    @Executes
    public void run() {
        
    }


    @Command(name = "red")
    public static class RedCommand {

        @Executes
        public void run() {
            
        }

    }



}
