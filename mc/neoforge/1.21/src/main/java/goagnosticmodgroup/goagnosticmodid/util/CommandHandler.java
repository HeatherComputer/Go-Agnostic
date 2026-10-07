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

import net.minecraft.commands.CommandSourceStack;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class CommandHandler implements Command<CommandSourceStack> {

    private static final Field ARGUMENTS = getArgumentsField();

    private final Consumer<CommandContext> runner;

    public CommandHandler(Consumer<CommandContext> runner) {
        this.runner = runner;
    }

    @Override
    public int run(com.mojang.brigadier.context.CommandContext<CommandSourceStack> in)
            throws CommandSyntaxException {
        

        CommandSourceStack source = in.getSource();
        
        Map<String, Object> args = new HashMap<>();
        try {
			Map<String, ParsedArgument> args2 = getArgs(in);
            for (String key : args2.keySet()) {
                args.put(key, args2.get(key).getResult());
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            return 1;
		}

        CommandContext ctx;
        if (source.isPlayer()) {
            Player player = new Player(source.getPlayer());
            ctx = new CommandContext(player, args);
        }
        else {
            ctx = null; //TODO!
        }

        runner.accept(ctx);

        return 0;

    }

    public static class RequireChecker implements Predicate<CommandSourceStack> {

        private final Function<CommandContext, Boolean> checker;

        public RequireChecker(Function<CommandContext, Boolean> checker) {
            this.checker = checker;
        }

        @Override
        public boolean test(CommandSourceStack source) { 
            CommandContext ctx;
            if (source.isPlayer()) {
                Player player = new Player(source.getPlayer());
                ctx = new CommandContext(player, null);
            }
            else {
                ctx = null; //TODO!
            }
            return checker.apply(ctx).booleanValue();
        }

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

	private static Map<String, ParsedArgument> getArgs(com.mojang.brigadier.context.CommandContext<CommandSourceStack> in) throws Exception {
        return (Map<String, ParsedArgument>) ARGUMENTS.get(in);
    }

}