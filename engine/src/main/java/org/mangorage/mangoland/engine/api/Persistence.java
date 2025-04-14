package org.mangorage.mangoland.engine.api;

import org.mangorage.mangoland.engine.internal.PersistenceImpl;

public sealed interface Persistence permits org.mangorage.mangoland.engine.internal.PersistenceImpl {

    static Persistence of() {
        return new PersistenceImpl();
    }

    byte[] getVariable(byte[] id);
    void setVariable(byte[] id, byte[] value);
}
