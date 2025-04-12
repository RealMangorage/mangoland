package org.mangorage.mangoland.types;

import org.mangorage.mangoland.Instruction;
import org.mangorage.mangoland.data.ByteConstants;
import org.mangorage.mangoland.persistance.Persistence;
import org.mangorage.mangoland.util.ByteUtil;
import org.mangorage.mangoland.util.StringUtil;
import java.util.Arrays;

public final class AddInstruction implements Instruction {
    @Override
    public void process(byte[] instruction, Persistence persistence) {
        int offset = 0;

        // ---- PARAMETER ONE ----
        int a;
        if (startsWithVariable(instruction, offset)) {
            offset += ByteConstants.VARIABLE_INST.length;
            int end = findMarker(instruction, offset, ByteConstants.VARIABLE_END);
            byte[] varId = Arrays.copyOfRange(instruction, offset, end);
            a = ByteUtil.bytesToInt(persistence.getVariable(varId)); // assuming you only get 4 bytes here
            offset = end + ByteConstants.VARIABLE_END.length;
        } else {
            a = ByteUtil.bytesToInt(Arrays.copyOfRange(instruction, offset, offset + 4));
            offset += 4;
        }

        // ---- PARAMETER TWO ----
        int b;
        if (startsWithVariable(instruction, offset)) {
            offset += ByteConstants.VARIABLE_INST.length;
            int end = findMarker(instruction, offset, ByteConstants.VARIABLE_END);
            byte[] varId = Arrays.copyOfRange(instruction, offset, end);
            b = ByteUtil.bytesToInt(persistence.getVariable(varId)); // assuming you only get 4 bytes here
            offset = end + ByteConstants.VARIABLE_END.length;
        } else {
            b = ByteUtil.bytesToInt(Arrays.copyOfRange(instruction, offset, offset + 4));
            offset += 4;
        }

        // ---- OUTPUT VARIABLE ----
        if (!startsWithVariable(instruction, offset)) {
            throw new IllegalArgumentException("Expected variable marker at output position");
        }
        offset += ByteConstants.VARIABLE_INST.length;
        int end = findMarker(instruction, offset, ByteConstants.VARIABLE_END);
        byte[] outputId = Arrays.copyOfRange(instruction, offset, end);

        persistence.setVariable(outputId, ByteUtil.intToBytesLE(a + b));
    }

    @Override
    public byte[] compile(String code) {
        var array = StringUtil.extractQuotedStrings(code);
        if (array.length < 3) return new byte[0];

        byte[] bytes = new byte[0];

        // PARAM 1
        bytes = ByteUtil.merge(bytes, compileParam(array[0]));

        // PARAM 2
        bytes = ByteUtil.merge(bytes, compileParam(array[1]));

        // OUTPUT
        bytes = ByteUtil.merge(bytes, compileParam(array[2]));

        return bytes;
    }

    private byte[] compileParam(String param) {
        if (param.startsWith("$")) {
            // Variable: Exclude '$' and append VARIABLE_END
            byte[] varId = param.replaceFirst("\\$", "").getBytes();
            return ByteUtil.merge(ByteConstants.VARIABLE_INST, ByteUtil.merge(varId, ByteConstants.VARIABLE_END));
        } else {
            // Integer: Convert to 4-byte array
            return ByteUtil.intToBytesLE(Integer.parseInt(param));
        }
    }

    // Helper methods to check and find markers for variables
    private boolean startsWithVariable(byte[] instruction, int offset) {
        return Arrays.equals(ByteConstants.VARIABLE_INST, Arrays.copyOfRange(instruction, offset, offset + ByteConstants.VARIABLE_INST.length));
    }

    private int findMarker(byte[] instruction, int start, byte[] marker) {
        for (int i = start; i < instruction.length - marker.length + 1; i++) {
            if (Arrays.equals(marker, Arrays.copyOfRange(instruction, i, i + marker.length))) {
                return i;
            }
        }
        throw new IllegalArgumentException("Marker not found in byte array");
    }
}



