package org.mangorage.mangoland.script.util;

import org.mangorage.mangoland.engine.api.datatype.DataType;
import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.parameter.Parameter;
import org.mangorage.mangoland.engine.api.variable.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class StringUtil {
    private static final Pattern quotePattern = Pattern.compile("(?:\\(([^)]+)\\)\\s*)?'([^']*)'");

    public static Parameter[] extractQuotedStrings(final String input, final CompileEnv env) {
        final List<Parameter> result = new ArrayList<>();
        final var matcher = quotePattern.matcher(input);
        while (matcher.find()) {
            final String type = matcher.group(1); // might be null if no type was there
            final String value = matcher.group(2);

            final DataType<?> dataType = env.getDataType(type);
            result.add(
                    Parameter.of(
                            Variable.of(
                                    dataType,
                                    dataType.compile(value)
                            )
                    )
            );
        }
        return result.toArray(Parameter[]::new);
    }

}
