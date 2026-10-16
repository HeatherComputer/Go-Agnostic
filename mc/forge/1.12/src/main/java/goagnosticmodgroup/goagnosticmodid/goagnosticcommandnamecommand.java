package goagnosticmodgroup.goagnosticmodid;

import goagnosticmodgroup.goagnosticmodid.util.GACommandBase;

public class goagnosticcommandnamecommand extends GACommandBase {

    public goagnosticcommandnamecommand()
    {
        System.out.println("REGISTERING");
        //addSubcommand(new Start());
    }
    
    @Override
    public String getName()
    {
        return "goagnosticcommandname";
    }
}