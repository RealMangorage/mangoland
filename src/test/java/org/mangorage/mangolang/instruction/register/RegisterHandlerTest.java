package org.mangorage.mangolang.instruction.register;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VMEnvironment;

import java.util.List;

public class RegisterHandlerTest {

    private final RegisterHandler registerHandler = new RegisterHandler();

    @Test
    public void bakeUsesLowercaseClassNameAsDefaultId() {
        List<BakedInstruction> bakedInstructions = registerHandler.bake(DefaultOnlyInstruction.class);

        Assertions.assertEquals(1, bakedInstructions.size());
        Assertions.assertEquals("defaultonlyinstruction", bakedInstructions.get(0).id());
        Assertions.assertInstanceOf(DefaultOnlyInstruction.class, bakedInstructions.get(0).instruction());
    }

    @Test
    public void bakeIncludesDefaultIdAndAnnotationAliases() {
        List<BakedInstruction> bakedInstructions = registerHandler.bake(AliasInstruction.class);

        Assertions.assertEquals(List.of(
                "aliasinstruction",
                "call_alias",
                "invoke_alias"
        ), bakedInstructions.stream().map(BakedInstruction::id).toList());

        Instruction instruction = bakedInstructions.get(0).instruction();
        for (BakedInstruction bakedInstruction : bakedInstructions) {
            Assertions.assertSame(instruction, bakedInstruction.instruction());
        }
    }

    @Test
    public void bakeDeduplicatesBlankAndRepeatedIds() {
        List<BakedInstruction> bakedInstructions = registerHandler.bake(DuplicateIdInstruction.class);

        Assertions.assertEquals(List.of(
                "duplicateidinstruction",
                "duplicate_alias"
        ), bakedInstructions.stream().map(BakedInstruction::id).toList());
    }

    @Test
    public void bakeRejectsClassesThatAreNotInstructions() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> registerHandler.bake(String.class)
        );

        Assertions.assertTrue(exception.getMessage().contains("does not implement Instruction"));
    }

    public static final class DefaultOnlyInstruction implements Instruction {
        @Override
        public void execute(VMEnvironment env) {
        }
    }

    @AutoRegisterInstruction(id = "call_alias")
    @AutoRegisterInstruction(id = "invoke_alias")
    public static final class AliasInstruction implements Instruction {
        @Override
        public void execute(VMEnvironment env) {
        }
    }

    @AutoRegisterInstruction
    @AutoRegisterInstruction(id = "duplicate_alias")
    @AutoRegisterInstruction(id = "duplicate_alias")
    public static final class DuplicateIdInstruction implements Instruction {
        @Override
        public void execute(VMEnvironment env) {
        }
    }
}

