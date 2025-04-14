package org.mangorage.mangoland.script.util;

import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.ByteArrayKey;
import org.mangorage.mangoland.engine.util.ByteUtil;

import java.util.Arrays;

public final class GeneralUtil {
    public static Parameter[] getParameters(byte[] instruction, CompileEnv env) {
        var params = ByteUtil.extractBetween(instruction, ParameterConstants.PARAMETER_START.get(), ParameterConstants.PARAMETER_END.get());
        return Arrays.stream(params)
                .map(param -> {
                    var dataType = ByteArrayKey.of(Arrays.copyOfRange(param, 0, 4));
                    return new Parameter(env.getDataType(dataType), Arrays.copyOfRange(param, 4, param.length));
                })
                .toArray(Parameter[]::new);
    }
}
