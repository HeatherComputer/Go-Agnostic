package computer.heather.goagnostic.api.annotations;

import java.lang.annotation.Target;

import computer.heather.goagnostic.api.command.args.ArgumentType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;



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

    @Target(ElementType.METHOD)
    public static @interface Require {

    }

    //Runtime retention lets our runtime parsers handle it!
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Repeatable(value = Arguments.class)
    public static @interface Argument {
        //Must be unique, and I think this is how we're doing it? Unsure..
        String name();
        ArgumentType type();

        //Unused for most stuff but still just... here, for the sake of where the fuck else should it go

        //int min() default Integer.MIN_VALUE;
        //int max() default Integer.MAX_VALUE;
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Arguments {
        Argument[] value();
    }

}   
