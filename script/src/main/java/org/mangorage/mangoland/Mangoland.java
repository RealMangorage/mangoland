package org.mangorage.mangoland;

import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.env.builder.CompileEnvBuilder;
import org.mangorage.mangoland.engine.api.instruction.InstructionSet;
import org.mangorage.mangoland.engine.api.instruction.InstructionSetBuilder;
import org.mangorage.mangoland.script.ScriptDataTypes;
import org.mangorage.mangoland.script.instructions.AddInstruction;
import org.mangorage.mangoland.script.instructions.ParseInstruction;
import org.mangorage.mangoland.script.instructions.PrintInstruction;
import org.mangorage.mangoland.engine.util.ByteUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
/**
 An example of how to get mangoland (ml) to be used

    You can have your own custom set of Instruction {@link org.mangorage.mangoland.engine.api.instruction.Instruction}
    You can have your own custom set of Data Types {@link org.mangorage.mangoland.engine.api.datatype.DataType}

    Below you will find how to get these parts into executable/comping form.

    Read the JavaDocs on the linked Classes for more info on how they are used!


    This class/module servers as an example of how to use the engine to create
    your OWN language of your choosing.
 */

public final class Mangoland {
    private static final InstructionSet INSTRUCTION_SET = InstructionSetBuilder.of()
            .register("org.mangorage#print", ByteUtil.intToBytes(0), new PrintInstruction())
            .register("org.mangorage#add", ByteUtil.intToBytes(1), new AddInstruction())
            .register("org.mangorage#parse", ByteUtil.intToBytes(2), new ParseInstruction())
            .build();

    private static final CompileEnv ENV = CompileEnvBuilder.create()
            .register("var", ScriptDataTypes.VARIABLE)
            .register("data_type", ScriptDataTypes.DATA_TYPE)
            .register("string", ScriptDataTypes.STRING_TYPE)
            .register("integer",ScriptDataTypes.INTEGER_TYPE)
            .build();

    public static void main(String[] args) throws IOException {
        Files.write(
                Path.of("myprogram.mangoland"),
                INSTRUCTION_SET.compile(new String[] {
                        "org.mangorage#add '-2' '0' -> '$1'",

                        "org.mangorage#parse '$1' -> '$1' as '?string' from '?integer'",
                        "org.mangorage#print; '$1'",

                        "org.mangorage#parse '$1' -> '$1' as '?integer' from '?string'",
                        "org.mangorage#add '56' '$1' -> '$1'",
                        "org.mangorage#parse '$1' -> '$1' as '?string' from '?integer'",
                        "org.mangorage#print; '$1'",
                        "org.mangorage#print 'Finished!'"
                }, ENV),
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE
        );

        INSTRUCTION_SET.process(
                Files.readAllBytes(Path.of("myprogram.mangoland")),
                ENV
        );
    }

}
