package org.mangorage.mangoland.script;

import org.mangorage.mangoland.engine.api.ByteArrayKey;

public final class ScriptDataTypes {
    public static final ByteArrayKey VARIABLE = ByteArrayKey.of(new byte[] { 0x2e, 0x2A, 0x77, 0x01 });
    public static final ByteArrayKey STRING_TYPE = ByteArrayKey.of(new byte[] { 0x45, 0x60, 0x13, 0x7F });
    public static final ByteArrayKey INTEGER_TYPE = ByteArrayKey.of(new byte[] { 0x3B, 0x09, 0x5C, 0x22 });
    public static final ByteArrayKey DATA_TYPE = ByteArrayKey.of(new byte[] { 0x3B, 0x09, 0x5C, 0x23 });
}
