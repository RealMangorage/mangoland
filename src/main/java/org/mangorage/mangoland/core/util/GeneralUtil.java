package org.mangorage.mangoland.core.util;

import org.mangorage.mangoland.core.datatype.DataTypes;
import org.mangorage.mangoland.core.misc.ByteArrayKey;
import org.mangorage.mangoland.core.misc.ByteConstants;

import java.util.Arrays;

public final class GeneralUtil {
    public static Parameter[] getParameters(byte[] instruction, DataTypes dataTypes) {
        var params = ByteUtil.extractBetween(instruction, ByteConstants.PARAMETER_START.get(), ByteConstants.PARAMETER_END.get());
        return Arrays.stream(params)
                .map(param -> {
                    var dataType = ByteArrayKey.of(Arrays.copyOfRange(param, 0, 4));
                    var length = ByteUtil.bytesToInt(Arrays.copyOfRange(param, 4, 8));
                    return new Parameter(dataTypes.getDataType(dataType), length, Arrays.copyOfRange(param, 8, param.length));
                })
                .toArray(Parameter[]::new);
    }
}
