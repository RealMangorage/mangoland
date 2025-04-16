package org.mangorage.mangoland.engine.internal.instruction;

import org.mangorage.mangoland.engine.api.ByteArrayKey;
import org.mangorage.mangoland.engine.api.instruction.Instruction;
import org.mangorage.mangoland.engine.api.instruction.InstructionSet;
import org.mangorage.mangoland.engine.api.instruction.InstructionSetBuilder;

import java.util.HashMap;
import java.util.Map;

public class InstructionSetBuilderImpl implements InstructionSetBuilder {
    private final Map<ByteArrayKey, Instruction> instructionMap = new HashMap<>();
    private final Map<String, ByteArrayKey> packageToInstruction = new HashMap<>();

    public InstructionSetBuilderImpl() {}

    @Override
    public InstructionSetBuilder register(final String packageId, final byte[] instId, final Instruction instruction) {
        final var id = ByteArrayKey.of(instId);
        instructionMap.put(id, instruction);
        packageToInstruction.put(packageId, id);
        return this;
    }

    public InstructionSet build() {
        return new InstructionSetImpl(
                Map.copyOf(instructionMap),
                Map.copyOf(packageToInstruction)
        );
    }
}
