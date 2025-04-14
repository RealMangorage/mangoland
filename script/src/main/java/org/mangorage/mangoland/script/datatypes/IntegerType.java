package org.mangorage.mangoland.script.datatypes;

import org.mangorage.mangoland.engine.api.DataType;
import org.mangorage.mangoland.engine.api.ByteArrayKey;
import org.mangorage.mangoland.script.util.ByteUtil;
import org.mangorage.mangoland.script.ScriptDataTypes;

public final class IntegerType implements DataType<Integer> {

    @Override
    public ByteArrayKey getDataType() {
        return ScriptDataTypes.INTEGER_TYPE;
    }

    @Override
    public Integer asObject(byte[] data) {
        return ByteUtil.bytesToInt(data);
    }

    @Override
    public byte[] cast(DataType<?> from, byte[] input) {
        if (ScriptDataTypes.STRING_TYPE.equals(from.getDataType())) {
            // Parse the byte array into an integer, handle negative sign
            int result = 0;
            int sign = 1;
            int i = 0;

            // Check for sign
            if (input[0] == '-') {
                sign = -1;
                i++; // Skip the '-' character
            } else if (input[0] == '+') {
                i++; // Skip the '+' character (optional)
            }

            // Process the rest of the digits
            for (; i < input.length; i++) {
                byte b = input[i];
                if (b < '0' || b > '9') {
                    throw new IllegalArgumentException("Invalid byte in array: " + b);
                }
                result = result * 10 + (b - '0');
            }
            result *= sign; // Apply the sign to the final result

            // Convert integer to byte array (big-endian)
            return ByteUtil.intToBytes(result);
        }
        return input;
    }

}
