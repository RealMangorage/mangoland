package org.mangorage.mangoland.engine.api;

import org.mangorage.mangoland.engine.api.variable.Variable;
import org.mangorage.mangoland.engine.internal.PersistenceImpl;

/**
    A way to store variables, so future {@link org.mangorage.mangoland.engine.api.instruction.Instruction}'s
    can use them when being executed/ran
 */
public sealed interface Persistence permits org.mangorage.mangoland.engine.internal.PersistenceImpl {

    static Persistence of() {
        return new PersistenceImpl();
    }

    Variable getVariable(final byte[] id);
    void setVariable(final byte[] id, final Variable value);
}
