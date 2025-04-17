package org.mangorage.mangoland.script.datatypes;

import org.mangorage.mangoland.engine.api.datatype.DataType;
import org.mangorage.mangoland.engine.api.ByteArrayKey;
import org.mangorage.mangoland.engine.util.ByteUtil;
import org.mangorage.mangoland.script.ScriptDataTypes;

public class StringType implements DataType<String> {
    private final ByteArrayKey internalId;

    public StringType(final ByteArrayKey internalId) {
        this.internalId = internalId;
    }

    @Override
    public ByteArrayKey getInternalId() {
        return internalId;
    }


    @Override
    public String asObject(final byte[] data) {
        return new String(
                data
        );
    }

    @Override
    public byte[] cast(final DataType<?> from, final byte[] data) {
        if (ScriptDataTypes.INTEGER_TYPE.equals(from)) {
            // Convert Integer to String
            return String.valueOf(ByteUtil.bytesToInt(data)).getBytes();
        }
        return data;
    }

    @Override
    public byte[] compile(final String code) {
        return code.getBytes();
    }
}
