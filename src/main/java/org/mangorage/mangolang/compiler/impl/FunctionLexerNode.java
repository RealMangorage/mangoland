package org.mangorage.mangolang.compiler.impl;

import org.mangorage.mangolang.compiler.BlockContext;
import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.compiler.LexerNode;
import org.mangorage.mangolang.compiler.LexerOutput;
import org.mangorage.mangolang.instruction.InstructionSet;

import java.util.List;
import java.util.Stack;

public final class FunctionLexerNode implements LexerNode {

    @Override
    public LexerOutput handle(String[] parts, String name, Stack<BlockContext> blocks, List<Byte> out, CompilerContext ctx, InstructionSet set) {
        if (name.equals("function")) {
            String funcName = parts[1];
            BlockContext b = new BlockContext(BlockContext.Type.FUNCTION, out.size());
            blocks.push(b);

            // Note: Using 'jump' instead of 'call' to skip over the function body
            // prevents accidentally pushing a junk frame to your callStack!
            out.add((byte) set.requireOpcode("jump"));
            out.add((byte) 0); // placeholder low
            out.add((byte) 0); // placeholder high

            ctx.registerFunction(funcName, out.size());
            return new LexerOutput(true);
        }

        return new LexerOutput(false);
    }
}
