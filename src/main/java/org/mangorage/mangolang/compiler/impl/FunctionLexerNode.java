package org.mangorage.mangolang.compiler.impl;

import org.mangorage.mangolang.compiler.BlockContext;
import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.compiler.LexerNode;
import org.mangorage.mangolang.compiler.LexerOutput;
import org.mangorage.mangolang.instruction.InstructionSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FunctionLexerNode implements LexerNode {
    private static final Pattern FUNCTION_SIGNATURE = Pattern.compile("([a-zA-Z_]\\w*)\\s*\\((.*)\\)");

    @Override
    public LexerOutput handle(String[] parts, String name, Stack<BlockContext> blocks, List<Byte> out, CompilerContext ctx, InstructionSet set) {
        if (name.equals("function")) {
            if (parts.length < 2) {
                throw new RuntimeException("Function declaration requires a name");
            }

            String signature = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length)).trim();
            Matcher matcher = FUNCTION_SIGNATURE.matcher(signature);
            if (!matcher.matches()) {
                throw new RuntimeException("Invalid function declaration: " + signature);
            }

            String funcName = matcher.group(1);
            List<String> parameters = parseParameters(matcher.group(2));
            BlockContext b = new BlockContext(BlockContext.Type.FUNCTION, out.size());
            blocks.push(b);

            // Note: Using 'jump' instead of 'call' to skip over the function body
            // prevents accidentally pushing a junk frame to your callStack!
            out.add((byte) set.requireOpcode("jump"));
            out.add((byte) 0); // placeholder low
            out.add((byte) 0); // placeholder high

            ctx.beginFunction(funcName, out.size(), parameters);
            return new LexerOutput(true);
        }

        return new LexerOutput(false);
    }

    private List<String> parseParameters(String rawParameters) {
        String trimmed = rawParameters.trim();
        if (trimmed.isEmpty()) {
            return List.of();
        }

        List<String> parameters = new ArrayList<>();
        for (String parameter : trimmed.split(",")) {
            String name = parameter.trim();
            if (!name.matches("[a-zA-Z_]\\w*")) {
                throw new RuntimeException("Invalid function parameter: " + name);
            }
            parameters.add(name);
        }
        return parameters;
    }
}
