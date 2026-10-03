package computer.heather.goagnostic.api.annotations;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;



/*
Oh boy, this is gonna be complex.
Especially on later versions...

MAYBE NOT Commands MUST extend GoAgnosticCommand, just like @Mod.
Todo - figure out what I need to give to the register method?

*/
@Target(ElementType.TYPE)
public @interface Command {

    String name();


    @Target(ElementType.METHOD)
    public static @interface Executes {

    }

}   
