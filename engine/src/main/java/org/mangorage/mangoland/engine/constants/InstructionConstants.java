package org.mangorage.mangoland.engine.constants;

import org.mangorage.mangoland.engine.api.ByteArrayKey;

public final class InstructionConstants {
    public static final ByteArrayKey INSTRUCTION_START  = ByteArrayKey.of(new byte[]{0x0, 0x7, 0xf, 0x1f});
    public static final ByteArrayKey INSTRUCTION_END    = ByteArrayKey.of(new byte[]{0x1, 0x4, 0xe, 0x1e});

    public static final ByteArrayKey PARAMETER_START    = ByteArrayKey.of(new byte[]{0x14, 0x24, 0xc, 0x1d});
    public static final ByteArrayKey PARAMETER_END      = ByteArrayKey.of(new byte[]{0x66, 0x13, 0x42, 0x7F});
}
