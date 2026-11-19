package goagnosticmodgroup.goagnosticmodid.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import computer.heather.goagnostic.api.command.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.entity.player.ServerPlayerEntity;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class CommandHandler implements Command<CommandSource> {

    private static final Field ARGUMENTS = getArgumentsField();

    private final Consumer<CommandContext> runner;

    public CommandHandler(Consumer<CommandContext> runner) {
        this.runner = runner;
    }

    @Override
    public int run(com.mojang.brigadier.context.CommandContext<CommandSource> in)
            throws CommandSyntaxException {
        

        CommandSource source = in.getSource();
        
        Map<String, Object> args = new HashMap<>();
        try {
			Map<String, ParsedArgument> args2 = getArgs(in);
            for (String key : args2.keySet()) {
                //Convert special arg types - such as players - to their Go!Agnostic comparison
                args.put(key, convertArgument(args2.get(key).getResult(), source));
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            return 1;
		}

        CommandContext ctx;
        try {
            Player player = new Player(source.getPlayerOrException());
            ctx = new CommandContext(player, args);
        } catch (Exception e) {
            ctx = new CommandContext(new Server(), args); 
        }
        runner.accept(ctx);

        return 0;

    }

    public static class RequireChecker implements Predicate<CommandSource> {

        private final Function<CommandContext, Boolean> checker;

        public RequireChecker(Function<CommandContext, Boolean> checker) {
            this.checker = checker;
        }

        @Override
        public boolean test(CommandSource source) { 
            CommandContext ctx;
            try {
                Player player = new Player(source.getPlayerOrException());
                ctx = new CommandContext(player, null);
            } catch (Exception e) {
                ctx = new CommandContext(new Server(), null); 
            }
            return checker.apply(ctx).booleanValue();
        }

    }


    private static Object convertArgument(Object in, CommandSource source) throws CommandSyntaxException {
        
        if (in instanceof EntitySelector) {
            EntitySelector selector = (EntitySelector) in;
            ServerPlayerEntity player = selector.findSinglePlayer(source);
            return new Player(player);
        }

        return in;

    }


    private static Field getArgumentsField() {
        try {
            Field field = com.mojang.brigadier.context.CommandContext.class.getDeclaredField("arguments");
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            throw new RuntimeException("Cannot access field", e);
        }
    }

	private static Map<String, ParsedArgument> getArgs(com.mojang.brigadier.context.CommandContext<CommandSource> in) throws Exception {
        return (Map<String, ParsedArgument>) ARGUMENTS.get(in);
    }

}