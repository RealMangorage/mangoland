package org.mangorage.mangland.java.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
    Put this on a class that will be able
    to run/compile scripts
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ScriptProvider {
    String id();
    int version();
}
