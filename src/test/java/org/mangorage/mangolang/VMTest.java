package org.mangorage.mangolang;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mangorage.mangolang.compiler.Compiler;
import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.object.MangolangObjects;
import org.mangorage.mangolang.object.impl.BooleanMLObject;
import org.mangorage.mangolang.object.impl.IntegerMLObject;
import org.mangorage.mangolang.object.impl.StringMLObject;
import org.mangorage.mangolang.vm.VM;
import org.mangorage.mangolang.vm.VMEnvironment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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
    public void emittedObjectsRoundTripThroughVmDecoder() {
        assertRoundTrip(new IntegerMLObject(1337), IntegerMLObject.class, "1337");
        assertRoundTrip(new StringMLObject("hello world"), StringMLObject.class, "hello world");
        assertRoundTrip(BooleanMLObject.TRUE, BooleanMLObject.class, "true");
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
                        (byte) MangolangObjects.TAG_BOOLEAN,
                        (byte) 2,
                        (byte) 0,
                        (byte) 1,
                        (byte) 0
                }
        );

        RuntimeException booleanException = Assertions.assertThrows(RuntimeException.class, booleanEnv::readObject);
        Assertions.assertTrue(booleanException.getMessage().contains("Invalid payload size for object tag 3"));

        VMEnvironment integerEnv = new VMEnvironment(
                new VM(MangoLang.createEnv()),
                new byte[]{
                        (byte) MangolangObjects.OBJECT_PREFIX,
                        (byte) MangolangObjects.TAG_INTEGER,
                        (byte) 1,
                        (byte) 0,
                        (byte) 7
                }
        );

        RuntimeException integerException = Assertions.assertThrows(RuntimeException.class, integerEnv::readObject);
        Assertions.assertTrue(integerException.getMessage().contains("Invalid payload size for object tag 1"));
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

    private void assertRoundTrip(MangolangObject original, Class<? extends MangolangObject> expectedType, String expectedDisplay) {
        List<Byte> bytes = new ArrayList<>();
        MangolangObjects.emitObject(bytes, original);

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

