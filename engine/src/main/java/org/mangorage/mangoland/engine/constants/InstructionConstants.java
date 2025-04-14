package org.mangorage.mangoland.engine.constants;

import org.mangorage.mangoland.engine.api.ByteArrayKey;

public final class InstructionConstants {
    public static final ByteArrayKey INSTRUCTION_START = ByteArrayKey.of(new byte[]{0x0, 0x7, 0xf, 0x1f});
    public static final ByteArrayKey INSTRUCTION_END   = ByteArrayKey.of(new byte[]{0x1, 0x4, 0xe, 0x1e});
}
