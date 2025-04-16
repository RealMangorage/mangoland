package org.mangorage.mangoland.script;

import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.env.builder.CompileEnvBuilder;
import org.mangorage.mangoland.script.datatypes.StringType;
import org.mangorage.mangoland.script.util.StringUtil;

public class Test {

    public static void main(String[] args) {
        /*
                "print (string) 'Hello!'",
                "add (var) '1' + (integer) '1'",
                "print (var) '1'"
         */


        CompileEnv env = CompileEnvBuilder.create()
                .register("string", ScriptDataTypes.STRING_TYPE)
                .build();

        var result = StringUtil.extractQuotedStrings("print (string) 'Hello!'", env);
        var a = 1;
    }
}
