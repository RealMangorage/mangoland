package org.mangorage.mangolang.instruction.register;

import java.util.function.Function;

public enum ParamType {
    BOOLEAN(Boolean::parseBoolean),
    INT(Integer::parseInt);

    private final Function<String, Object> converter;

    ParamType(Function<String, Object> converter) {
        this.converter = converter;
    }

    public Object parse(String value) {
        return converter.apply(value);
    }
}
