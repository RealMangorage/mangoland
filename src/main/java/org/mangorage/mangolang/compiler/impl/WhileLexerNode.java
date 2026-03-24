package org.mangorage.mangolang.compiler.impl;

import org.mangorage.mangolang.compiler.BlockContext;
import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.compiler.CompilerEmitUtil;
import org.mangorage.mangolang.compiler.LexerNode;
import org.mangorage.mangolang.compiler.LexerOutput;
import org.mangorage.mangolang.instruction.InstructionSet;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public final class WhileLexerNode implements LexerNode {
    @Override
    public LexerOutput handle(String[] parts, String name, Stack<BlockContext> blocks, List<Byte> out, CompilerContext ctx, InstructionSet set) {
        if (name.equals("while")) {
            String loopLabel = ctx.consumePendingLoopLabel();
            int startAddress = out.size();
            BlockContext block = new BlockContext(BlockContext.Type.WHILE, startAddress, loopLabel);

            int doIndex = -1;
            for (int i = 1; i < parts.length; i++) {
                if (parts[i].equalsIgnoreCase("do")) {
                    doIndex = i;
                    break;
                }
            }

            if (doIndex == -1) {
                throw new RuntimeException("While statement requires 'do'");
            }

            if (doIndex > 1) {
                String condition = String.join(" ", Arrays.copyOfRange(parts, 1, doIndex));
                CompilerEmitUtil.emitSimpleCondition(condition, out, ctx, set);
                block.setCondJumpAddress(out.size());
                out.add((byte) set.requireOpcode("jump_if_false"));
                out.add((byte) 0);
                out.add((byte) 0);
                out.add((byte) 0);
                out.add((byte) 0);
            }

            blocks.push(block);
            return new LexerOutput(true);
        }
        return new LexerOutput(false);
    }
}
