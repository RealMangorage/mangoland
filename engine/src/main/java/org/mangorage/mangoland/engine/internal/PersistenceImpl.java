package org.mangorage.mangoland.engine.internal;

import org.mangorage.mangoland.engine.api.Persistence;
import org.mangorage.mangoland.engine.api.ByteArrayKey;
import org.mangorage.mangoland.engine.api.Variable;

import java.util.HashMap;
import java.util.Map;

public final class PersistenceImpl implements Persistence {
    private final Map<ByteArrayKey, Variable> variables = new HashMap<>();

    public PersistenceImpl() {}

    @Override
    public Variable getVariable(final byte[] id) {
        return variables.get(ByteArrayKey.of(id));
    }

    @Override
    public void setVariable(byte[] id, Variable value) {
        variables.put(
                ByteArrayKey.of(id),
                value
        );
    }
}
