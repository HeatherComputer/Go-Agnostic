package computer.heather.goagnostic.internal.commandgen;

import java.io.IOException;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import computer.heather.goagnostic.api.annotations.Command.Argument;
import computer.heather.goagnostic.api.annotations.Command.Executes;
import computer.heather.goagnostic.api.annotations.Command.Require;
import computer.heather.goagnostic.api.annotations.Mod;
import computer.heather.goagnostic.api.annotations.Mod.Target;
import computer.heather.goagnostic.internal.asm.InstructionParser;
import computer.heather.goagnostic.internal.commandgen.forge.ForgeCommandGens;
import computer.heather.goagnostic.internal.commandgen.neoforge.NeoforgeCommandGens;


public abstract class CommandGen {
    /*


    so we need to heavily use asm here, to build classes bit by bit
    there'd be a prepare and finalise, and then stuff for requires, executes, and subcommands
    this is for brigadier at least. unsure about older
    remapping - changing `goagnosticmodid` to the actual mod id for example - can be done AFTERWARDS, using the remapper that ProcessorUtils already has.

    so.. what do we need exactly?

     */

    protected final ClassReader reader;
    protected final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    protected final ClassNode classNode;
    

    public CommandGen(byte[] originalClassBytes) {
        this.reader = new ClassReader(originalClassBytes);;
        this.classNode = new ClassNode();
        reader.accept(classNode, 0);

    }

    /*
    For brigadier, this could be all that's needed...? In this file at least.
    This is because everything relevant ends up in the `register` method.
     */

    public void injectIntoMethod(int instructionIndex, String methodName, String toInject) {

        //Get the target method. An NPE will be thrown if it doesn't exist
        MethodNode methodNode = classNode.methods.stream()
            .filter((method) -> {
                return method.name.equals(methodName);
            })
            .findFirst()
            .orElse(null);
        
        //System.out.println(methodNode);


        int index = 0;
        //Allows for injection at the very start, before any instructions
        if (instructionIndex == 0) {
            InsnList instructions = InstructionParser.parseInstructionsFromString(toInject);
            methodNode.instructions.insert(instructions);
        }
        for (AbstractInsnNode instruction : methodNode.instructions) {
            if (instruction.getOpcode() < 0) continue;
            index++;
            if (index != instructionIndex) continue;

            InsnList instructions = InstructionParser.parseInstructionsFromString(toInject);
            methodNode.instructions.insert(instruction, instructions);
        }
    }

    //Creates an empty method, then you can use injectIntoMethod to inject
    //For access, see ASM's Opcodes.ACC_PUBLIC and similar
    //Don't forget to inject returns etc!!
    public void createMethod(String name) {
        createMethod(Opcodes.ACC_PUBLIC, name, "()V)");
    }
    public void createMethod(int access, String name, String descriptor) {
        MethodNode methodNode = new MethodNode(access, name, descriptor, null, null);
        for (MethodNode node : classNode.methods) {
            if (node.name.equals(methodNode.name) && node.desc.equals(methodNode.desc)) return;
        }
        classNode.methods.add(methodNode);
    }


    public abstract void executes(ProcessingEnvironment processingEnv, Element element, Executes executesAnnotation, Target target, CommandGen commandGen, TypeElement type, String className) throws IOException;

    public abstract void requires(ProcessingEnvironment processingEnv, Element element, Require requiresAnnotation, Target target, CommandGen commandGen, TypeElement type, String className) throws IOException;

    public abstract void subcommandStart(String name, Target target, CommandGen commandGen, String className) throws IOException;
    public abstract void subcommandEnd(String name, Target target, CommandGen commandGen, String className, Mod mod, ProcessingEnvironment processingEnv) throws IOException;

    public abstract void argumentstart(Target target, CommandGen commandGen, Argument argument) throws IOException;
    public abstract void argumentEnd(Target target, CommandGen commandGen, Argument argument, boolean lastArg) throws IOException;

    public void finalise(Target target, String className) {}
    


    public static CommandGen getCommandGen(Mod.Target target, String qualifiedName) throws IOException {
        switch (target) {

            case NEO_1_21:
                return new NeoforgeCommandGens.NEO_1_21(target.getImplName());
            
            case FORGE_1_12:
                return new ForgeCommandGens.FORGE_1_12(target.getImplName(), qualifiedName);

            default:
                throw new IllegalArgumentException("Unknown target: " + target.getImplName());
                
        }
    }

    public byte[] getRemappedBytes() {
        classNode.accept(writer);
        return writer.toByteArray();
    }


    //A completely dummy commandgen!
    public static class DUMMY extends CommandGen {

        public DUMMY(byte[] originalClassBytes) {
            super(originalClassBytes);
        }

        @Override
        public void executes(ProcessingEnvironment processingEnv, Element element, Executes executesAnnotation,
                Target target, CommandGen commandGen, TypeElement type, String className) throws IOException {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'executes'");
        }

        @Override
        public void requires(ProcessingEnvironment processingEnv, Element element, Require requiresAnnotation,
                Target target, CommandGen commandGen, TypeElement type, String className) throws IOException {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'requires'");
        }

        @Override
        public void subcommandStart(String name, Target target, CommandGen commandGen, String className)
                throws IOException {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'subcommandStart'");
        }

        @Override
        public void subcommandEnd(String name, Target target, CommandGen commandGen, String className, Mod mod, ProcessingEnvironment processingEnv) throws IOException {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'subcommandEnd'");
        }

        @Override
        public void argumentstart(Target target, CommandGen commandGen, Argument argument) throws IOException {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'argumentstart'");
        }

        @Override
        public void argumentEnd(Target target, CommandGen commandGen, Argument argument, boolean lastArg)
                throws IOException {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'argumentEnd'");
        }
        
    }
}
