package org.mangorage.mangoland.engine.api;

import org.mangorage.mangoland.engine.api.variable.Variable;
import org.mangorage.mangoland.engine.internal.PersistenceImpl;

public sealed interface Persistence permits org.mangorage.mangoland.engine.internal.PersistenceImpl {

    static Persistence of() {
        return new PersistenceImpl();
    }

    Variable getVariable(byte[] id);
    void setVariable(byte[] id, Variable value);
}
