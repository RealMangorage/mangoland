package org.mangorage.mangoland.engine.api.instruction;

import org.mangorage.mangoland.engine.internal.instruction.InstructionSetBuilderImpl;

/**
    Used to assemble a Set of {@link Instruction}'s
 */
public interface InstructionSetBuilder {
    static InstructionSetBuilder of() {
        return new InstructionSetBuilderImpl();
    }

    InstructionSetBuilder register(final String packageId, final byte[] instId, final Instruction instruction);
    InstructionSet build();
}
