package org.mangorage.mangolang.compiler;

import org.mangorage.mangolang.instruction.InstructionSet;

import java.util.List;
import java.util.Stack;

public interface LexerNode {
    LexerOutput handle(String[] parts, String name, Stack<BlockContext> blocks, List<Byte> out, CompilerContext ctx, InstructionSet set);
}
