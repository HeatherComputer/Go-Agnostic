package computer.heather.goagnostic.internal.asm;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class InstructionParser {

    //A list of keywords and their relevant parsers
    public static final HashMap<String, Function<String, AbstractInsnNode>> PARSERS = new HashMap<String, Function<String, AbstractInsnNode>>();
    static {
        PARSERS.put("aload", InstructionParser::parseAload);
        PARSERS.put("checkcast", InstructionParser::parseCheckCast);
        PARSERS.put("dup", InstructionParser::parseDup);
        PARSERS.put("new", InstructionParser::parseNew);
        PARSERS.put("ldc", InstructionParser::parseLdc);

        PARSERS.put("iconst_0", InstructionParser::parseIconst0);
        PARSERS.put("iconst_1", InstructionParser::parseIconst1);

        PARSERS.put("ireturn", InstructionParser::parseIReturn);

        PARSERS.put("invokestatic", (input) -> parseMethod(input, Opcodes.INVOKESTATIC));
        PARSERS.put("invokeinterface", (input) -> parseMethod(input, Opcodes.INVOKEINTERFACE));
        PARSERS.put("invokevirtual", (input) -> parseMethod(input, Opcodes.INVOKEVIRTUAL));
        PARSERS.put("invokespecial", (input) -> parseMethod(input, Opcodes.INVOKESPECIAL));

        PARSERS.put("lambda", InstructionParser::parseLambda);
        
    }
    
    
    public static InsnList parseInstructionsFromString(String instructions) {
        InsnList list = new InsnList();

        for (String test: instructions.split("\\n")) {
            String instruction = test.trim();

            //This allows for comments. Is that even important?
            //System.out.println(instruction);
            if (instruction.isEmpty() || instruction.startsWith("//")) continue;

            String[] split = instruction.split(" ", 2);
            String parserString = instruction.split(" ", 2)[0];
            String rest = "";

            if (split.length > 1) {
                rest = split[1].trim();
            }


            Function<String,AbstractInsnNode> parser = PARSERS.get(parserString);
            AbstractInsnNode node = parser.apply(rest);

            list.add(node);
        }

        return list;

    }


    /*
    Below are instruction-specific parsers
    */

    public static AbstractInsnNode parseAload(String input) {
        return new VarInsnNode(Opcodes.ALOAD, Integer.parseInt(input));
    }

    public static AbstractInsnNode parseLdc(String input) {
        return new LdcInsnNode(input);
    }

    public static AbstractInsnNode parseNew(String input) {
        return new TypeInsnNode(Opcodes.NEW, input);
    }

    public static AbstractInsnNode parseDup(String ignored) {
        return new InsnNode(Opcodes.DUP);
    }

    public static AbstractInsnNode parseIconst0(String ignored) {
        return new InsnNode(Opcodes.ICONST_0);
    }

    public static AbstractInsnNode parseIconst1(String ignored) {
        return new InsnNode(Opcodes.ICONST_1);
    }

    public static AbstractInsnNode parseIReturn(String ignored) {
        return new InsnNode(Opcodes.IRETURN);
    }

    //This handles INVOKEDYNAMIC / INVOKESPECIAL / INVOKEINTERFACE / INVOKEVITUAL the same
    public static AbstractInsnNode parseMethod(String input, int opcode) {
        String[] parts = input.split(" ");
        MethodInsnNode node = new MethodInsnNode(opcode, parts[0], parts[1], parts[2]);
        return node;
    }


    public static AbstractInsnNode parseCheckCast(String input) {
        AbstractInsnNode node = new TypeInsnNode(Opcodes.CHECKCAST, input);
        return node;
    }



    //Lambdas and similar are arguably the most complex.. especially as they need Handles


    private static final Map<String, Integer> OPCODES =
        new HashMap<String, Integer>();

    static {
        OPCODES.put("invokestatic", Opcodes.H_INVOKESTATIC);
        OPCODES.put("invokevirtual", Opcodes.H_INVOKEVIRTUAL);
        OPCODES.put("invokeinterface", Opcodes.H_INVOKEINTERFACE);
    }


    //This is usable for basically any lambda
    private static final Handle LAMBDA_METAFACTORY = new Handle(
        Opcodes.H_INVOKESTATIC,
        "java/lang/invoke/LambdaMetafactory",
        "metafactory",
        "(Ljava/lang/invoke/MethodHandles$Lookup;" +
            "Ljava/lang/String;" +
            "Ljava/lang/invoke/MethodType;" +
            "Ljava/lang/invoke/MethodType;" +
            "Ljava/lang/invoke/MethodHandle;" +
            "Ljava/lang/invoke/MethodType;)" +
            "Ljava/lang/invoke/CallSite;",
        false
    );

    public static AbstractInsnNode parseLambda(String input) {

        //Each part of the instruction is separated by a space. For convenience, we've a lot of variables. We then need to get a Handle for the InvokeDynamicInsnNode we're about to instantiate:
        String[] parts = input.split(" ");

        //System.out.println(parts);

        String name = parts[0];
        

        String factoryDesc = parts[1];
        String interfaceMethodDesc = parts[2];
        String instantiatedDesc = parts[7];

        //These variables are for the handle
        int targetOpcode = OPCODES.get(parts[3]);
        String targetOwner = parts[4];
        String targetName = parts[5];
        String targetDesc = parts[6];

        Handle handle = new Handle(targetOpcode, targetOwner, targetName, targetDesc);

        InvokeDynamicInsnNode node = new InvokeDynamicInsnNode(name, factoryDesc, LAMBDA_METAFACTORY, Type.getMethodType(interfaceMethodDesc), handle, Type.getMethodType(instantiatedDesc));
        return node;
        
    }

}
