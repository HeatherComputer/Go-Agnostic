package computer.heather.goagnostic.api.annotations;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Target(ElementType.TYPE)
public @interface Command {
    

    String commandName();


}   
