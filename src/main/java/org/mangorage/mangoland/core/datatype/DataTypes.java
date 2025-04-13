package org.mangorage.mangoland.core.datatype;

import org.mangorage.mangoland.core.misc.ByteArrayKey;

import java.util.HashMap;
import java.util.Map;

public final class DataTypes {
    public static final ByteArrayKey VARIABLE = ByteArrayKey.of(new byte[] { 0x2e, 0x2A, 0x77, 0x01 });
    public static final ByteArrayKey STRING_TYPE = ByteArrayKey.of(new byte[] { 0x45, 0x60, 0x13, 0x7F });
    public static final ByteArrayKey INTEGER_TYPE = ByteArrayKey.of(new byte[] { 0x3B, 0x09, 0x5C, 0x22 });
    public static final ByteArrayKey DATA_TYPE = ByteArrayKey.of(new byte[] { 0x3B, 0x09, 0x5C, 0x23 });

    private final Map<ByteArrayKey, DataType<?>> dataMap = new HashMap<>();
    private final Map<String, DataType<?>> dataMapByKeyword = new HashMap<>();

    // typeName is unused, just so I can just know what the heck it is, from dev side...
    public void registerDataType(final String keyword, final ByteArrayKey dataTypeId, final DataType<?> dataType) {
        dataMap.put(dataTypeId, dataType);
        dataMapByKeyword.put(keyword, dataType);
    }

    public DataType<?> getDataType(ByteArrayKey byteArrayKey) {
        return dataMap.get(byteArrayKey);
    }

    public DataType<?> getDataType(String keyword) {
        return dataMapByKeyword.get(keyword);
    }
}
