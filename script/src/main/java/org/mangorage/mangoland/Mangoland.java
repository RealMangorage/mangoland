package org.mangorage.mangoland;

import org.mangorage.mangland.java.api.IScriptProvider;
import org.mangorage.mangland.java.api.ScriptProvider;
import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.env.builder.CompileEnvBuilder;
import org.mangorage.mangoland.engine.api.instruction.InstructionSet;
import org.mangorage.mangoland.engine.api.instruction.InstructionSetBuilder;
import org.mangorage.mangoland.script.ScriptDataTypes;
import org.mangorage.mangoland.script.instructions.AddInstruction;
import org.mangorage.mangoland.script.instructions.IfInstruction;
import org.mangorage.mangoland.script.instructions.ParseInstruction;
import org.mangorage.mangoland.script.instructions.PrintInstruction;
import org.mangorage.mangoland.engine.util.ByteUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
    This will compile and execute the example script define in
    the Main function


    An example of how to get mangoland (ml) to be used

    You can have your own custom set of Instruction {@link org.mangorage.mangoland.engine.api.instruction.Instruction}
    You can have your own custom set of Data Types {@link org.mangorage.mangoland.engine.api.datatype.DataType}

    Below you will find how to get these parts into executable/comping form.

    Read the JavaDocs on the linked Classes for more info on how they are used!


    This class/module servers as an example of how to use the engine to create
    your OWN language of your choosing.
 */

@ScriptProvider(id = "mangoland", version = 1)
public final class Mangoland implements IScriptProvider {
    private final InstructionSet INSTRUCTION_SET = InstructionSetBuilder.of()
            .register("print", ByteUtil.intToBytes(0), new PrintInstruction())
            .register("add", ByteUtil.intToBytes(1), new AddInstruction())
            .register("parse", ByteUtil.intToBytes(2), new ParseInstruction())
            .register("if", ByteUtil.intToBytes(3), new IfInstruction())
            .build();

    private final CompileEnv ENV = CompileEnvBuilder.create()
            .register("var", ScriptDataTypes.VARIABLE)
            .register("data_type", ScriptDataTypes.DATA_TYPE)
            .register("string", ScriptDataTypes.STRING_TYPE)
            .register("integer", ScriptDataTypes.INTEGER_TYPE)
            .build();

    @Override
    public byte[] compile(final String code) {
        return INSTRUCTION_SET.compile(
                code.split("\n"),
                ENV
        );
    }

    @Override
    public void execute(final byte[] instructions) {
        INSTRUCTION_SET.process(
                instructions,
                ENV
        );
    }

    public static void main(final String[] args) throws IOException {
        IScriptProvider provider = new Mangoland();
        Files.write(
                Path.of("myprogram.mangoland"),
                provider.compile(
                        """
                        if (string) '1' == (string) '12' -> (integer) '1',
                        print (string) 'Hello!',
                        add (integer) '1' + (integer) '1' (var)'1',
                        parse (var) '1' as (string) '2',
                        print (var) '2'
                        """
                ),
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE
        );

        provider.execute(
                Files.readAllBytes(
                        Path.of("myprogram.mangoland")
                )
        );
    }
}
