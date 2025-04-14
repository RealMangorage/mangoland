package org.mangorage.mangoland.script.util;

import org.mangorage.mangoland.engine.api.ByteArrayKey;

public final class ParameterConstants {
    public static final ByteArrayKey PARAMETER_START = ByteArrayKey.of(new byte[]{0x14, 0x24, 0xc, 0x1d});
    public static final ByteArrayKey PARAMETER_END   = ByteArrayKey.of(new byte[]{0x66, 0x13, 0x42, 0x7F});
}
