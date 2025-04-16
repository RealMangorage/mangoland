package org.mangorage.mangoland.engine.internal.instruction;

import org.mangorage.mangoland.engine.api.ByteArrayKey;
import org.mangorage.mangoland.engine.api.Persistence;
import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.env.RuntimeEnv;
import org.mangorage.mangoland.engine.api.instruction.Instruction;
import org.mangorage.mangoland.engine.api.instruction.InstructionSet;
import org.mangorage.mangoland.engine.api.instruction.InvalidInstruction;
import org.mangorage.mangoland.engine.constants.InstructionConstants;
import org.mangorage.mangoland.engine.util.ByteUtil;

import java.util.Arrays;
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

    @Override
    public byte[] compile(final String[] code, final CompileEnv env) {
        byte[] bytes = new byte[0];
        for (final String line : code) {
            byte[] codeForLine = null;

            for (final var set : packageToInstruction.entrySet()) {
                if (line.startsWith(set.getKey())) {
                    final byte[] start = ByteUtil.merge(InstructionConstants.INSTRUCTION_START.get(), set.getValue().get());

                    final Instruction instruction = instructionMap.get(set.getValue());
                    final byte[] remainder = instruction.compile(line.substring(line.indexOf(";") + 2), env);

                    final byte[] end = ByteUtil.merge(start, remainder);

                    codeForLine = ByteUtil.merge(end, InstructionConstants.INSTRUCTION_END.get());
                }
            }

            if (codeForLine != null)
                bytes = ByteUtil.merge(bytes, codeForLine);
        }
        return bytes;
    }
}
