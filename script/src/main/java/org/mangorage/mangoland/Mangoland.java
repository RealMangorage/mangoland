package org.mangorage.mangoland;

import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.env.builder.CompileEnvBuilder;
import org.mangorage.mangoland.engine.api.instruction.InstructionSet;
import org.mangorage.mangoland.engine.api.instruction.InstructionSetBuilder;
import org.mangorage.mangoland.script.ScriptDataTypes;
import org.mangorage.mangoland.script.datatypes.DataTypeType;
import org.mangorage.mangoland.script.datatypes.IntegerType;
import org.mangorage.mangoland.script.datatypes.StringType;
import org.mangorage.mangoland.script.datatypes.VariableType;
import org.mangorage.mangoland.script.instructions.AddInstruction;
import org.mangorage.mangoland.script.instructions.ParseInstruction;
import org.mangorage.mangoland.script.instructions.PrintInstruction;
import org.mangorage.mangoland.engine.util.ByteUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**

     INST   INST    INST    INST
     SPOT  HEADER   DATA    END
    [1234] [1234]  [....]  [1234]
 */

public final class Mangoland {
    private static final InstructionSet INSTRUCTION_SET = InstructionSetBuilder.of()
            .register("org.mangorage#print", ByteUtil.intToBytes(0), new PrintInstruction())
            .register("org.mangorage#add", ByteUtil.intToBytes(1), new AddInstruction())
            .register("org.mangorage#parse", ByteUtil.intToBytes(2), new ParseInstruction())
            .build();

    private static final CompileEnv ENV = CompileEnvBuilder.create()
            .register("var", ScriptDataTypes.VARIABLE, new VariableType())
            .register("data_type", ScriptDataTypes.DATA_TYPE, new DataTypeType())
            .register("string", ScriptDataTypes.STRING_TYPE, new StringType())
            .register("integer",ScriptDataTypes.INTEGER_TYPE, new IntegerType())
            .build();

    static {
        // Stored Variable Struct
        // [TYPE] [LENGTH] [DATA]


        // Type -> 0x0, 0x0, 0x0, 0x0 -> VARIABLE

        // #add -> [INST START] [PARAM START] [TYPE -> 4 bytes] [LENGTH -> 4 bytes] [VALUE -> X (length) bytes] [PARAM END] [INST STOP]
    }

    public static void main(String[] args) throws IOException {
        // Arrays.toString(INST_END)


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
                }, ENV),
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE
        );

        INSTRUCTION_SET.process(
                Files.readAllBytes(Path.of("myprogram.mangoland")),
                ENV
        );
    }

}
