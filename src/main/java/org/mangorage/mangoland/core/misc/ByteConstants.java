package org.mangorage.mangoland.core.misc;

public final class ByteConstants {
    public static final ByteArrayKey INST_START      = ByteArrayKey.of(new byte[]{0x0, 0x7, 0xf, 0x1f});
    public static final ByteArrayKey INST_END        = ByteArrayKey.of(new byte[]{0x1, 0x4, 0xe, 0x1e});

    public static final ByteArrayKey PARAMETER_START = ByteArrayKey.of(new byte[]{0x14, 0x24, 0xc, 0x1d});
    public static final ByteArrayKey PARAMETER_END   = ByteArrayKey.of(new byte[]{0x66, 0x13, 0x42, 0x7F});
}
