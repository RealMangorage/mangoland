package org.mangorage.mangoland.script.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class StringUtil {
    private static final Pattern quotePattern = Pattern.compile("'(.*?)'");

    public static String[] extractQuotedStrings(String input) {
        List<String> result = new ArrayList<>();
        var matcher = quotePattern.matcher(input);
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
        return result.toArray(new String[0]);
    }
}
