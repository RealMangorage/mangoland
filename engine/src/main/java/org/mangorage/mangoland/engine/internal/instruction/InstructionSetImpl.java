package org.mangorage.mangoland.engine.internal.instruction;

import org.mangorage.mangoland.engine.api.ByteArrayKey;
import org.mangorage.mangoland.engine.api.Persistence;
import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.env.RuntimeEnv;
import org.mangorage.mangoland.engine.api.instruction.Instruction;
import org.mangorage.mangoland.engine.api.instruction.InstructionSet;
import org.mangorage.mangoland.engine.api.instruction.InvalidInstruction;
import org.mangorage.mangoland.engine.constants.InstructionConstants;
import org.mangorage.mangoland.engine.exception.CompileException;
import org.mangorage.mangoland.engine.util.ByteUtil;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class InstructionSetImpl implements InstructionSet {
    private final Map<ByteArrayKey, Instruction> instructionMap;
    private final Map<String, ByteArrayKey> packageToInstruction;

    public InstructionSetImpl(final Map<ByteArrayKey, Instruction> instructionMap, final Map<String, ByteArrayKey> packageToInstruction) {
        this.instructionMap = instructionMap;
        this.packageToInstruction = packageToInstruction;
    }

    @Override
    public void process(final byte[] instructions, final CompileEnv env) {
        final Persistence persistence = Persistence.of();
        int skip = 0;

        for (final byte[] instruction : ByteUtil.extractBetween(instructions, InstructionConstants.INSTRUCTION_START.get(), InstructionConstants.INSTRUCTION_END.get())) {
            if (skip > 0) {
                skip--;
                continue;
            }

            skip = instructionMap.getOrDefault(
                    ByteArrayKey.of(
                            Arrays.copyOfRange(
                                    instruction,
                                    0,
                                    4
                            )
                    ),
                    InvalidInstruction.INSTANCE
            ).execute(
                    Arrays.copyOfRange(
                            instruction,
                            4,
                            instruction.length
                    ),
                    RuntimeEnv.of(
                            env,
                            persistence
                    )
            );
        }
    }

    record ExceptionInfo(String error, Exception e) {};

    @Override
    public byte[] compile(final String[] code, final CompileEnv env) {
        byte[] compiledCode = new byte[0];

        final List<ExceptionInfo> errors = new ArrayList<>();

        int lineNumber = 0;
        for (final String line : code) {
            lineNumber++;
            final int spot = line.indexOf(" ");
            final var instKey = packageToInstruction.get(spot == -1 ? line : line.substring(0, spot));
            final var inst = instructionMap.get(instKey);

            if (inst != null) {
                try {
                    final byte[] remainder = inst.compile(line, env);

                    compiledCode = ByteUtil.merge(
                            compiledCode,
                            InstructionConstants.INSTRUCTION_START.get(),
                            instKey.get(),
                            remainder,
                            InstructionConstants.INSTRUCTION_END.get());
                } catch (CompileException e) {
                    errors.add(
                            new ExceptionInfo(
                                    """
                                    Line: %s
                                    Code:
                                    %s
                                    Error:
                                    %s
                                    """.formatted(
                                            lineNumber,
                                            line,
                                            e.getMessage()
                                    ),
                                    e
                            )
                    );
                }
            }
        }

        final int numOfErrors = errors.size();


        if (!errors.isEmpty()) {
            final var finalMessage = new StringBuilder();
            finalMessage.append("\n-----------------------------------");
            finalMessage.append("\nError has occurred while compiling");
            finalMessage.append("\nNumber of Errors: " + numOfErrors);
            for (var errorInfo : errors.stream().limit(20).toList()) {
                finalMessage.append("\n------------------------------------\n");
                finalMessage.append(errorInfo.error());

                final var sw = new StringWriter();
                final var pw = new PrintWriter(sw);
                errorInfo.e().printStackTrace(pw);
                finalMessage.append(sw);
            }
            final var exception = new IllegalStateException(finalMessage.toString());
            exception.setStackTrace(new StackTraceElement[0]);
            throw exception;
        }

        return compiledCode;
    }
}
