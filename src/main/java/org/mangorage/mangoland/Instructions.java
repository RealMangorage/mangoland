package org.mangorage.mangoland;

import org.mangorage.mangoland.persistance.Persistence;
import org.mangorage.mangoland.types.AddInstruction;
import org.mangorage.mangoland.types.IntegerToStringInstruction;
import org.mangorage.mangoland.types.InvalidInstruction;
import org.mangorage.mangoland.types.PrintInstruction;
import org.mangorage.mangoland.util.ByteUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**

     INST   INST    INST    INST
     SPOT  HEADER   DATA    END
    [1234] [1234]  [....]  [1234]
 */

public final class Instructions {
    private static final Map<Integer, Instruction> INSTRUCTION_MAP = new HashMap<>();
    private static final Map<String, Integer> PACKAGE_TO_INSTRUCTION = new HashMap<>();

    static void register(final String packageId, int instId, Instruction instruction) {
        INSTRUCTION_MAP.put(instId, instruction);
        PACKAGE_TO_INSTRUCTION.put(packageId, instId);
    }

    static {
        register("org.mangorage#print", 0, new PrintInstruction());
        register("org.mangorage#add", 1, new AddInstruction());
        register("org.mangorage#IntegerToString", 2, new IntegerToStringInstruction());
    }

    private static final byte[] INST_START = new byte[]{0x0, 0x7, 0xf, 0x1f};
    private static final byte[] INST_END = new byte[]{0x1, 0x4, 0xe, 0x1e};

    public static void process(byte[] instructions) {
        Persistence persistence = new Persistence();
        for (byte[] instruction : ByteUtil.extractBetween(instructions, INST_START, INST_END)) {
            INSTRUCTION_MAP.getOrDefault(
                    ByteUtil.bytesToInt(Arrays.copyOfRange(instruction, 0, 4)),
                    InvalidInstruction.INSTANCE
            ).process(Arrays.copyOfRange(instruction, 4, instruction.length), persistence);
        }
    }

    public static byte[] compile(String[] code) {
        byte[] bytes = new byte[0];
        for (String line : code) {
            byte[] codeForLine = null;


            check: for (Map.Entry<String, Integer> set : PACKAGE_TO_INSTRUCTION.entrySet()) {
                if (line.startsWith(set.getKey())) {
                    byte[] start = ByteUtil.merge(INST_START, ByteUtil.intToBytesLE(set.getValue()));

                    Instruction instruction = INSTRUCTION_MAP.get(set.getValue());
                    byte[] remainder = instruction.compile(line.substring(line.indexOf(";") + 2));

                    byte[] end = ByteUtil.merge(start, remainder);

                    codeForLine = ByteUtil.merge(end, INST_END);
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
                        "org.mangorage#add; '1' '1' -> '$1'",
                        "org.mangorage#add; '$1' '1' -> '$1'",
                        "org.mangorage#add; '$1' '$1' -> '$1'",

                        "org.mangorage#IntegerToString; '$1'",
                        "org.mangorage#print; '$1'"
                }),
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE
        );

        process(
                Files.readAllBytes(Path.of("myprogram.mangoland"))
        );
    }

}
