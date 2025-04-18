package org.mangorage.mangoland.engine.internal;

import org.mangorage.mangoland.engine.api.Persistence;
import org.mangorage.mangoland.engine.api.ByteArrayKey;
import org.mangorage.mangoland.engine.api.variable.Variable;

import java.util.HashMap;
import java.util.Map;

public final class PersistenceImpl implements Persistence {
    private final Map<ByteArrayKey, Variable> variables = new HashMap<>();
    private final Map<ByteArrayKey, Object> variables_java = new HashMap<>();

    public PersistenceImpl() {}

    @Override
    public Variable getVariable(final byte[] id) {
        return variables.get(ByteArrayKey.of(id));
    }

    @Override
    public void setVariable(final byte[] id, final Variable value) {
        variables.put(
                ByteArrayKey.of(id),
                value
        );
    }

    @Override
    public Object getObject(final byte[] id) {
        return variables_java.get(ByteArrayKey.of(id));
    }

    @Override
    public void setObject(final byte[] id, final Object object) {
        variables_java.put(ByteArrayKey.of(id), object);
    }
}
