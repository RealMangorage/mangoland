package org.mangorage.mangoland;

import org.mangorage.mangoland.core.datatype.DataTypes;
import org.mangorage.mangoland.core.datatype.impl.IntegerType;
import org.mangorage.mangoland.core.datatype.impl.StringType;
import org.mangorage.mangoland.core.datatype.impl.VariableType;
import org.mangorage.mangoland.core.instruction.Instruction;
import org.mangorage.mangoland.core.instruction.impl.AddInstruction;
import org.mangorage.mangoland.core.instruction.impl.ParseInstruction;
import org.mangorage.mangoland.core.misc.ByteArrayKey;
import org.mangorage.mangoland.core.persistance.Persistence;
import org.mangorage.mangoland.core.instruction.impl.InvalidInstruction;
import org.mangorage.mangoland.core.instruction.impl.PrintInstruction;
import org.mangorage.mangoland.core.datatype.DataType;
import org.mangorage.mangoland.core.util.ByteUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mangorage.mangoland.core.misc.ByteConstants.INST_END;
import static org.mangorage.mangoland.core.misc.ByteConstants.INST_START;

/**

     INST   INST    INST    INST
     SPOT  HEADER   DATA    END
    [1234] [1234]  [....]  [1234]
 */

public final class Mangoland {
    private static final Map<ByteArrayKey, Instruction> INSTRUCTION_MAP = new HashMap<>();
    private static final Map<String, ByteArrayKey> PACKAGE_TO_INSTRUCTION = new HashMap<>();
    private static final DataTypes DATA_TYPES = new DataTypes();


    static void registerInst(final String packageId, final byte[] instId, final Instruction instruction) {
        final var id = ByteArrayKey.of(instId);

        INSTRUCTION_MAP.put(id, instruction);
        PACKAGE_TO_INSTRUCTION.put(packageId, id);
    }

    // typeName is unused, just so I can just know what the heck it is, from dev side...
    static void registerDataType(final String typeName, final ByteArrayKey dataTypeId, final DataType dataType) {
        DATA_TYPES.registerDataType(typeName, dataTypeId, dataType);
    }

    static {
        registerInst("org.mangorage#print", ByteUtil.intToBytesLE(0), new PrintInstruction());
        registerInst("org.mangorage#add", ByteUtil.intToBytesLE(1), new AddInstruction());
        registerInst("org.mangorage#parse", ByteUtil.intToBytesLE(2), new ParseInstruction());


        registerDataType("var", DataTypes.VARIABLE, new VariableType());
        registerDataType("string", DataTypes.STRING_TYPE, new StringType());
        registerDataType("integer", DataTypes.INTEGER_TYPE, new IntegerType());

        // Stored Variable Struct
        // [TYPE] [LENGTH] [DATA]


        // Type -> 0x0, 0x0, 0x0, 0x0 -> VARIABLE

        // #add -> [INST START] [PARAM START] [TYPE -> 4 bytes] [LENGTH -> 4 bytes] [VALUE -> X (length) bytes] [PARAM END] [INST STOP]
    }

    public static void process(byte[] instructions) {
        Persistence persistence = new Persistence();
        for (byte[] instruction : ByteUtil.extractBetween(instructions, INST_START.get(), INST_END.get())) {
            INSTRUCTION_MAP.getOrDefault(
                    ByteArrayKey.of(Arrays.copyOfRange(instruction, 0, 4)),
                    InvalidInstruction.INSTANCE
            ).process(Arrays.copyOfRange(instruction, 4, instruction.length), persistence, DATA_TYPES);
        }
    }

    public static byte[] compile(String[] code) {
        byte[] bytes = new byte[0];
        for (String line : code) {
            byte[] codeForLine = null;


            check: for (var set : PACKAGE_TO_INSTRUCTION.entrySet()) {
                if (line.startsWith(set.getKey())) {
                    byte[] start = ByteUtil.merge(INST_START.get(), set.getValue().get());

                    Instruction instruction = INSTRUCTION_MAP.get(set.getValue());
                    byte[] remainder = instruction.compile(line.substring(line.indexOf(";") + 2), DATA_TYPES);

                    byte[] end = ByteUtil.merge(start, remainder);

                    codeForLine = ByteUtil.merge(end, INST_END.get());
                    break check;
                }
            }

            if (codeForLine != null)
                bytes = ByteUtil.merge(bytes, codeForLine);
        }
        return bytes;
    }


    public static void main(String[] args) throws IOException {
        // Arrays.toString(INST_END)


        Files.write(
                Path.of("myprogram.mangoland"),
                compile(new String[] {
                        "org.mangorage#add '2' '3' -> '$1'",
                        "org.mangorage#add '2' '$1' -> '$1'",
                        "org.mangorage#parse",
                        "org.mangorage#print; '$1'",

                }),
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE
        );

        process(
                Files.readAllBytes(Path.of("myprogram.mangoland"))
        );
    }

}
