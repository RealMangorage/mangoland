package org.mangorage.mangoland.engine.api.instruction;

import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.env.RuntimeEnv;
import org.mangorage.mangoland.engine.exception.CompileException;

/**
    AAn Instruction will have 12 Bytes of data, in the simplest form.

    An Instruction can have {@link org.mangorage.mangoland.engine.api.parameter.Parameter}'s

    Each Parameter takes ~12 Bytes, for its marker START/END and the {@link org.mangorage.mangoland.engine.api.datatype.DataType}
    Associated.

    Read on how parameters work in {@link org.mangorage.mangoland.engine.api.parameter.Parameter}
    Read on how DataTypes work in {@link org.mangorage.mangoland.engine.api.datatype.DataType}


    Byte Array Representation of an Instruction

    NO PARAMETERS -> [INST START, 4 Bytes] [INST ID, 4 Bytes] [INST END, 4 bytes] (12 Bytes)

    1 Parameter [INST START, 4 Bytes] [INST ID, 4 Bytes] [PARAM START, 4 Bytes] [DataType, 4 bytes] [Parameter Data, any bytes] [PARAM END, 4 Bytes] [INST END, 4 bytes] (Atleast 24 bytes)
 */
public interface Instruction {
    int execute(final byte[] instruction, final RuntimeEnv env);

    byte[] compile(final String code, final CompileEnv env);
}
