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
                "call_alias",
                "invoke_alias"
        ), bakedInstructions.stream().map(BakedInstruction::id).toList());

        Instruction instruction = bakedInstructions.get(0).instruction();
        for (BakedInstruction bakedInstruction : bakedInstructions) {
            Assertions.assertSame(instruction, bakedInstruction.instruction());
        }
    }

    @Test
    public void bakeUsesClassNameAsFallbackIdForBlankAnnotationIds() {
        List<BakedInstruction> bakedInstructions = registerHandler.bake(BlankIdInstruction.class);

        Assertions.assertEquals(List.of(
                "blankidinstruction"
        ), bakedInstructions.stream().map(BakedInstruction::id).toList());
    }

    @Test
    public void bakeParsesParamsIntoConstructorArguments() {
        List<BakedInstruction> bakedInstructions = registerHandler.bake(ParameterizedInstruction.class);

        Assertions.assertEquals(List.of(
                "param_true",
                "param_false"
        ), bakedInstructions.stream().map(BakedInstruction::id).toList());

        ParameterizedInstruction first = (ParameterizedInstruction) bakedInstructions.get(0).instruction();
        ParameterizedInstruction second = (ParameterizedInstruction) bakedInstructions.get(1).instruction();

        Assertions.assertTrue(first.flag());
        Assertions.assertEquals(42, first.number());
        Assertions.assertFalse(second.flag());
        Assertions.assertEquals(7, second.number());
        Assertions.assertNotSame(first, second);
    }

    @Test
    public void bakeReusesInstructionInstancesForEquivalentParams() {
        List<BakedInstruction> bakedInstructions = registerHandler.bake(SameParamsAliasInstruction.class);

        Assertions.assertEquals(List.of(
                "first_alias",
                "second_alias"
        ), bakedInstructions.stream().map(BakedInstruction::id).toList());

        Assertions.assertSame(bakedInstructions.get(0).instruction(), bakedInstructions.get(1).instruction());
        Assertions.assertEquals(9, ((SameParamsAliasInstruction) bakedInstructions.get(0).instruction()).number());
    }

    @Test
    public void bakeCanReturnMultipleEntriesWithTheSameFallbackIdWhenParamsDiffer() {
        List<BakedInstruction> bakedInstructions = registerHandler.bake(OverloadedDefaultIdInstruction.class);

        Assertions.assertEquals(List.of(
                "overloadeddefaultidinstruction",
                "overloadeddefaultidinstruction"
        ), bakedInstructions.stream().map(BakedInstruction::id).toList());

        OverloadedDefaultIdInstruction first = (OverloadedDefaultIdInstruction) bakedInstructions.get(0).instruction();
        OverloadedDefaultIdInstruction second = (OverloadedDefaultIdInstruction) bakedInstructions.get(1).instruction();

        Assertions.assertEquals(1, first.number());
        Assertions.assertEquals(2, second.number());
        Assertions.assertNotSame(first, second);
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
    public static final class BlankIdInstruction implements Instruction {
        @Override
        public void execute(VMEnvironment env) {
        }
    }

    @AutoRegisterInstruction(
            id = "param_true",
            params = {
                    @Parameter(type = ParamType.BOOLEAN, value = "true"),
                    @Parameter(type = ParamType.INT, value = "42")
            }
    )
    @AutoRegisterInstruction(
            id = "param_false",
            params = {
                    @Parameter(type = ParamType.BOOLEAN, value = "false"),
                    @Parameter(type = ParamType.INT, value = "7")
            }
    )
    public static final class ParameterizedInstruction implements Instruction {
        private final boolean flag;
        private final int number;

        public ParameterizedInstruction(boolean flag, int number) {
            this.flag = flag;
            this.number = number;
        }

        public boolean flag() {
            return flag;
        }

        public int number() {
            return number;
        }

        @Override
        public void execute(VMEnvironment env) {
        }
    }

    @AutoRegisterInstruction(
            id = "first_alias",
            params = {
                    @Parameter(type = ParamType.INT, value = "9")
            }
    )
    @AutoRegisterInstruction(
            id = "second_alias",
            params = {
                    @Parameter(type = ParamType.INT, value = "9")
            }
    )
    public static final class SameParamsAliasInstruction implements Instruction {
        private final int number;

        public SameParamsAliasInstruction(int number) {
            this.number = number;
        }

        public int number() {
            return number;
        }

        @Override
        public void execute(VMEnvironment env) {
        }
    }

    @AutoRegisterInstruction(params = {@Parameter(type = ParamType.INT, value = "1")})
    @AutoRegisterInstruction(params = {@Parameter(type = ParamType.INT, value = "2")})
    public static final class OverloadedDefaultIdInstruction implements Instruction {
        private final int number;

        public OverloadedDefaultIdInstruction(int number) {
            this.number = number;
        }

        public int number() {
            return number;
        }

        @Override
        public void execute(VMEnvironment env) {
        }
    }
}

