package org.mangorage.mangoland.core.datatype;

import org.mangorage.mangoland.core.misc.ByteArrayKey;

import java.util.HashMap;
import java.util.Map;

public final class DataTypes {
    public static final ByteArrayKey STRING_TYPE = ByteArrayKey.of(new byte[4]);
    public static final ByteArrayKey INTEGER_TYPE = ByteArrayKey.of(new byte[4]);

    private final Map<ByteArrayKey, DataType> DATA_TYPE_MAP = new HashMap<>();

    // typeName is unused, just so I can just know what the heck it is, from dev side...
    public void registerDataType(final String typeName, final ByteArrayKey dataTypeId, final DataType dataType) {
        DATA_TYPE_MAP.put(dataTypeId, dataType);
    }
}
