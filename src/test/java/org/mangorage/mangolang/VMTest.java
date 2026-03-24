package org.mangorage.mangolang;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mangorage.mangolang.compiler.Compiler;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.instruction.InstructionSet;
import org.mangorage.mangolang.instruction.register.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.register.ParamType;
import org.mangorage.mangolang.instruction.register.Parameter;
import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.object.MangolangObjectCompiler;
import org.mangorage.mangolang.object.MangolangObjects;
import org.mangorage.mangolang.object.codec.impl.BooleanMLCodec;
import org.mangorage.mangolang.object.codec.impl.IntegerMLCodec;
import org.mangorage.mangolang.object.codec.impl.StringMLCodec;
import org.mangorage.mangolang.object.OperationType;
import org.mangorage.mangolang.object.impl.BooleanMLObject;
import org.mangorage.mangolang.object.impl.IntegerMLObject;
import org.mangorage.mangolang.object.impl.StringMLObject;
import org.mangorage.mangolang.vm.VM;
import org.mangorage.mangolang.vm.VMEnvironment;

import java.util.ArrayList;
import java.util.List;

public class VMTest {

    @Test
    public void objectEqualityUsesRealBooleans() {
        MangolangObject equalInts = new IntegerMLObject(12).equals(new IntegerMLObject(12));
        MangolangObject differentInts = new IntegerMLObject(12).equals(new IntegerMLObject(7));
        MangolangObject equalStrings = new StringMLObject("mango").equals(new StringMLObject("mango"));
        MangolangObject differentStrings = new StringMLObject("mango").equals(new StringMLObject("lang"));
        MangolangObject equalBooleans = BooleanMLObject.TRUE.equals(new BooleanMLObject(true));

        Assertions.assertSame(BooleanMLObject.TRUE, equalInts);
        Assertions.assertSame(BooleanMLObject.FALSE, differentInts);
        Assertions.assertSame(BooleanMLObject.TRUE, equalStrings);
        Assertions.assertSame(BooleanMLObject.FALSE, differentStrings);
        Assertions.assertSame(BooleanMLObject.TRUE, equalBooleans);
        Assertions.assertEquals("12", new IntegerMLObject(12).toString());
        Assertions.assertEquals("true", BooleanMLObject.TRUE.toString());
    }

    @Test
    public void unifiedObjectOperatorApiSupportsArithmeticAndComparison() {
        IntegerMLObject ten = new IntegerMLObject(10);

        Assertions.assertEquals("15", ten.operator(new IntegerMLObject(5), OperationType.ADD).toString());
        Assertions.assertEquals("5", ten.operator(new IntegerMLObject(5), OperationType.SUBTRACT).toString());
        Assertions.assertEquals("50", ten.operator(new IntegerMLObject(5), OperationType.MULTIPLY).toString());
        Assertions.assertEquals("2", ten.operator(new IntegerMLObject(5), OperationType.DIVIDE).toString());
        Assertions.assertSame(BooleanMLObject.TRUE, ten.operator(new IntegerMLObject(5), OperationType.GREATER_THAN));
        Assertions.assertEquals("10 apples", ten.operator(new StringMLObject(" apples"), OperationType.ADD).toString());
        Assertions.assertSame(BooleanMLObject.TRUE, new StringMLObject("mango").operator(new StringMLObject("mango"), OperationType.EQUALS));
    }

    @Test
    public void emittedObjectsRoundTripThroughVmDecoder() {
        assertRoundTrip(new IntegerMLObject(1337), IntegerMLObject.class, "1337");
        assertRoundTrip(new StringMLObject("hello world"), StringMLObject.class, "hello world");
        assertRoundTrip(BooleanMLObject.TRUE, BooleanMLObject.class, "true");
    }

    @Test
    public void codecTagsAreUniqueAndFitInObjectHeaders() {
        Assertions.assertEquals(3, java.util.Set.of(IntegerMLCodec.TAG, StringMLCodec.TAG, BooleanMLCodec.TAG).size());
        Assertions.assertTrue(IntegerMLCodec.TAG >= 0 && IntegerMLCodec.TAG <= 0xFF);
        Assertions.assertTrue(StringMLCodec.TAG >= 0 && StringMLCodec.TAG <= 0xFF);
        Assertions.assertTrue(BooleanMLCodec.TAG >= 0 && BooleanMLCodec.TAG <= 0xFF);
    }

    @Test
    public void readObjectRejectsUnknownTags() {
        VMEnvironment env = new VMEnvironment(
                new VM(MangoLang.createEnv()),
                new byte[]{
                        (byte) MangolangObjects.OBJECT_PREFIX,
                        (byte) 99,
                        (byte) 0,
                        (byte) 0
                }
        );

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, env::readObject);
        Assertions.assertTrue(exception.getMessage().contains("Unknown object tag: 99"));
    }

    @Test
    public void readObjectRejectsInvalidPayloadLengths() {
        VMEnvironment booleanEnv = new VMEnvironment(
                new VM(MangoLang.createEnv()),
                new byte[]{
                        (byte) MangolangObjects.OBJECT_PREFIX,
                        (byte) BooleanMLCodec.TAG,
                        (byte) 2,
                        (byte) 0,
                        (byte) 1,
                        (byte) 0
                }
        );

        RuntimeException booleanException = Assertions.assertThrows(RuntimeException.class, booleanEnv::readObject);
        Assertions.assertTrue(booleanException.getMessage().contains("Invalid payload size for object tag " + BooleanMLCodec.TAG));

        VMEnvironment integerEnv = new VMEnvironment(
                new VM(MangoLang.createEnv()),
                new byte[]{
                        (byte) MangolangObjects.OBJECT_PREFIX,
                        (byte) IntegerMLCodec.TAG,
                        (byte) 1,
                        (byte) 0,
                        (byte) 7
                }
        );

        RuntimeException integerException = Assertions.assertThrows(RuntimeException.class, integerEnv::readObject);
        Assertions.assertTrue(integerException.getMessage().contains("Invalid payload size for object tag " + IntegerMLCodec.TAG));
    }

    @Test
    public void compiledProgramPrintsInlineStringsAndStackValues() {
        List<String> out = runProgram("""
                let x = 999
                print "Hello!"
                load x
                print
                push true
                print
                """);

        Assertions.assertEquals(List.of("Hello!", "999", "true"), out);
    }

    @Test
    public void typeInstructionCanStoreAndPrintVariableTypes() {
        List<String> out = runProgram("""
                let x = 999
                type x
                store typeresult
                print "Type: " .. typeresult
                """);

        Assertions.assertEquals(List.of("Type: integer"), out);
    }

    @Test
    public void typeInstructionReportsBuiltInObjectTypeNames() {
        List<String> out = runProgram("""
                let integerValue = 42
                let stringValue = "mango"
                let booleanValue = true

                type integerValue
                print
                type stringValue
                print
                type booleanValue
                print
                """);

        Assertions.assertEquals(List.of("integer", "string", "boolean"), out);
    }

    @Test
    public void whileLoopAndIfConditionsConsumeBooleanComparisonResults() {
        List<String> out = runProgram("""
                let x = 2
                while do
                    load x
                    print
                    if (x == 0) then
                        break
                    end
                    if (x != 1) then
                        print "tick"
                    end
                    load x
                    decrement
                    store x
                end
                """);

        Assertions.assertEquals(List.of("2", "tick", "1", "0"), out);
    }

    @Test
    public void inlineIfSupportsVariableToVariableEquality() {
        List<String> out = runProgram("""
                let x = 5
                let y = 5
                if (x == y) then
                    print "matched"
                end
                if (x != y) then
                    print "missed"
                end
                """);

        Assertions.assertEquals(List.of("matched"), out);
    }

    @Test
    public void whileSupportsInlineHeaderConditions() {
        List<String> out = runProgram("""
                let x = 10
                while x != 5 do
                    print "Doing! " .. x
                    x = x - 1
                end
                """);

        Assertions.assertEquals(List.of("Doing! 10", "Doing! 9", "Doing! 8", "Doing! 7", "Doing! 6"), out);
    }

    @Test
    public void labeledWhileHeadersCompileAndRun() {
        List<String> out = runProgram("""
                let x = 3
                mainloop: while x != 0 do
                    print "Loop " .. x
                    x = x - 1
                end
                """);

        Assertions.assertEquals(List.of("Loop 3", "Loop 2", "Loop 1"), out);
    }

    @Test
    public void labeledBreakCanExitOuterLoopFromInnerLoop() {
        List<String> out = runProgram("""
                let x = 10
                mainloop: while x != 5 do
                    print "Doing! " .. x
                    while do
                        print "Inner loop! " .. x
                        x = x - 1
                        if x == 7 then
                            break mainloop
                        end
                    end
                end

                print "Done! " .. x
                """);

        Assertions.assertEquals(List.of(
                "Doing! 10",
                "Inner loop! 10",
                "Inner loop! 9",
                "Inner loop! 8",
                "Done! 7"
        ), out);
    }

    @Test
    public void parameterizedFunctionsReceiveArgumentsAndShadowGlobals() {
        List<String> out = runProgram("""
                let x = 10

                function test(x, y, z)
                    print "X: " .. x
                    print "Y: " .. y
                    print "Z: " .. z
                end

                call test x 543 290
                load x
                print
                """);

        Assertions.assertEquals(List.of("X: 10", "Y: 543", "Z: 290", "10"), out);
    }

    @Test
    public void zeroArgumentFunctionsStillWorkWithParenthesesSyntax() {
        List<String> out = runProgram("""
                let x = 7

                function test()
                    load x
                    print
                end

                call test()
                """);

        Assertions.assertEquals(List.of("7"), out);
    }

    @Test
    public void callFailsWhenFunctionArityDoesNotMatch() {
        Compiler compiler = new Compiler(MangoLang.createEnv());

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> compiler.compile("""
                function test(x, y)
                    print x
                end

                call test 1
                """));

        Assertions.assertTrue(exception.getMessage().contains("expects 2 arguments but got 1"));
    }

    @Test
    public void letCanCaptureSingleFunctionReturnValue() {
        List<String> out = runProgram("""
                function check(x, y, z)
                    print "x: " .. x
                    print "y: " .. y
                    print "z: " .. z
                    if (3 == z) then
                        z = z + 1
                    end
                    return z
                end

                let result = call check(1, 2, 3)
                result = result + 10
                print "Result: " .. result
                """);

        Assertions.assertEquals(List.of("x: 1", "y: 2", "z: 3", "Result: 14"), out);
    }

    @Test
    public void functionsCanReturnLiteralValues() {
        List<String> out = runProgram("""
                function answer()
                    return 42
                end

                let result = call answer()
                print "Answer: " .. result
                """);

        Assertions.assertEquals(List.of("Answer: 42"), out);
    }

    @Test
    public void uncapturedReturnValueRemainsOnStack() {
        List<String> out = runProgram("""
                function answer()
                    return 99
                end

                call answer()
                print
                """);

        Assertions.assertEquals(List.of("99"), out);
    }

    @Test
    public void reassignmentCanUseInfixAdditionExpressions() {
        List<String> out = runProgram("""
                let total = 5
                total = total + 10
                print "Total: " .. total
                """);

        Assertions.assertEquals(List.of("Total: 15"), out);
    }

    @Test
    public void arithmeticExpressionsSupportSubtractMultiplyAndDivide() {
        List<String> out = runProgram("""
                let difference = 20 - 5
                let product = 6 * 7
                let quotient = 20 / 4
                print "Diff: " .. difference
                print "Product: " .. product
                print "Quotient: " .. quotient
                """);

        Assertions.assertEquals(List.of("Diff: 15", "Product: 42", "Quotient: 5"), out);
    }

    @Test
    public void repeatableAutoRegisterInstructionAliasesShareOneOpcode() {
        InstructionSet set = new InstructionSet();
        set.register(List.of(RepeatableAliasInstruction.class));

        int defaultOpcode = set.requireOpcode("repeatablealiasinstruction");
        int aliasOneOpcode = set.requireOpcode("call_alias");
        int aliasTwoOpcode = set.requireOpcode("invoke_alias");

        Assertions.assertEquals(defaultOpcode, aliasOneOpcode);
        Assertions.assertEquals(aliasOneOpcode, aliasTwoOpcode);
        Assertions.assertSame(set.get(aliasOneOpcode), set.get(aliasTwoOpcode));
        Assertions.assertInstanceOf(RepeatableAliasInstruction.class, set.get(aliasOneOpcode));
        Assertions.assertEquals("repeatablealiasinstruction", set.getName(aliasOneOpcode));
    }

    @Test
    public void repeatableAutoRegisterInstructionWithoutIdsFallsBackToSimpleName() {
        InstructionSet set = new InstructionSet();
        set.register(List.of(RepeatableBlankIdInstruction.class));

        int opcode = set.requireOpcode("repeatableblankidinstruction");

        Assertions.assertInstanceOf(RepeatableBlankIdInstruction.class, set.get(opcode));
    }

    @Test
    public void createEnvStillRegistersCompilerRequiredInstructionNames() {
        InstructionSet set = MangoLang.createEnv();

        for (String instructionName : List.of("call", "return", "jump", "print", "halt", "jump_if_false", "type")) {
            Assertions.assertNotNull(set.getOpcode(instructionName), instructionName + " should be registered");
        }
    }

    @AutoRegisterInstruction(
            id = "call_alias",
            params = {
                    @Parameter(type = ParamType.INT, value = "target")
            }
    )
    @AutoRegisterInstruction(
            id = "invoke_alias",
            params = {
                    @Parameter(type = ParamType.INT, value = "target"),
                    @Parameter(type = ParamType.INT, value = "argc")
            }
    )
    public static final class RepeatableAliasInstruction implements Instruction {
        @Override
        public void execute(VMEnvironment env) {
        }
    }

    @AutoRegisterInstruction(
            params = {
                    @Parameter(type = ParamType.INT, value = "target")
            }
    )
    @AutoRegisterInstruction(
            params = {
                    @Parameter(type = ParamType.INT, value = "argc")
            }
    )
    public static final class RepeatableBlankIdInstruction implements Instruction {
        @Override
        public void execute(VMEnvironment env) {
        }
    }

    private void assertRoundTrip(MangolangObject original, Class<? extends MangolangObject> expectedType, String expectedDisplay) {
        List<Byte> bytes = new ArrayList<>();
        MangolangObjectCompiler.emitObject(bytes, original);

        byte[] code = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            code[i] = bytes.get(i);
        }

        VMEnvironment env = new VMEnvironment(new VM(MangoLang.createEnv()), code);
        MangolangObject decoded = env.readObject();

        Assertions.assertInstanceOf(expectedType, decoded);
        Assertions.assertEquals(expectedDisplay, decoded.asString().toString());
    }

    private List<String> runProgram(String program) {
        Compiler compiler = new Compiler(MangoLang.createEnv());
        byte[] bytecode = compiler.compile(program);

        List<String> out = new ArrayList<>();
        VM vm = new VM(MangoLang.createEnv());
        vm.setTerminal(out::add);
        vm.run(bytecode);
        return out;
    }
}

