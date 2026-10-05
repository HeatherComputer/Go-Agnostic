package computer.heather.goagnostic.internal.commandgen.neoforge;

import java.io.IOException;

import computer.heather.goagnostic.internal.annotationprocessors.ModProcessor;
import computer.heather.goagnostic.internal.commandgen.CommandGen;
import computer.heather.goagnostic.internal.util.ProcessorUtils;

public class NeoforgeCommandGens {
    
    public static class NEO_1_21 extends CommandGen {

        public NEO_1_21(String implName) throws IOException {
            super(
                ProcessorUtils.readAllBytes(NEO_1_21.class.getResourceAsStream("/templates/GoAgnosticTemplate-" + implName + "/goagnosticmodgroup/goagnosticmodid/goagnosticcommandnamecommand.class.bin"))
            );
            //TODO Auto-generated constructor stub
        }

        @Override
        public void executes() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'executes'");
        }

    }

}
