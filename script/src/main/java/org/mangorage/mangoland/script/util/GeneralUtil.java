package org.mangorage.mangoland.script.util;

import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.ByteArrayKey;

import org.mangorage.mangoland.engine.api.parameter.Parameter;
import org.mangorage.mangoland.engine.constants.InstructionConstants;
import org.mangorage.mangoland.engine.util.ByteUtil;

import java.util.Arrays;

public final class GeneralUtil {
    public static Parameter[] getParameters(final byte[] instruction, final CompileEnv env) {
        final var params = ByteUtil.extractBetween(instruction, InstructionConstants.PARAMETER_START.get(), InstructionConstants.PARAMETER_END.get());
        return Arrays.stream(params)
                .map(param -> {
                    final var dataType = ByteArrayKey.of(Arrays.copyOfRange(param, 0, 4));
                    final var data =  Arrays.copyOfRange(param, 4, param.length);
                    return env.getDataType(dataType).createParameter(data);
                })
                .toArray(Parameter[]::new);
    }
}
