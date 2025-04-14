package org.mangorage.mangoland.script.datatypes;

import org.mangorage.mangoland.engine.api.DataType;
import org.mangorage.mangoland.engine.api.ByteArrayKey;
import org.mangorage.mangoland.engine.util.ByteUtil;
import org.mangorage.mangoland.script.ScriptDataTypes;

public class StringType implements DataType<String> {
    @Override
    public ByteArrayKey getDataType() {
        return ScriptDataTypes.STRING_TYPE;
    }

    @Override
    public String asObject(byte[] data) {
        return new String(
                data
        );
    }

    @Override
    public byte[] cast(DataType<?> from, byte[] data) {
        if (ScriptDataTypes.INTEGER_TYPE.equals(from.getDataType())) {
            // Convert Integer to String
            return String.valueOf(ByteUtil.bytesToInt(data)).getBytes();
        }
        return data;
    }
}
