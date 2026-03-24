package org.mangorage.mangolang.instruction.register;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(AutoRegisterMultipleInstructions.class)
public @interface AutoRegisterInstruction {
    String id() default "";
    Parameter[] params() default {};
}
