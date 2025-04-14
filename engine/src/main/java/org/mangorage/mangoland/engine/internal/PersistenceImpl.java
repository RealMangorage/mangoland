package org.mangorage.mangoland.engine.internal;

import org.mangorage.mangoland.engine.api.Persistence;
import org.mangorage.mangoland.engine.api.ByteArrayKey;

import java.util.HashMap;
import java.util.Map;

public final class PersistenceImpl implements Persistence {
    private final Map<ByteArrayKey, byte[]> variables = new HashMap<>();

    public PersistenceImpl() {}

    public byte[] getVariable(byte[] id) {
        return variables.getOrDefault(ByteArrayKey.of(id), new byte[0]);
    }

    public void setVariable(byte[] id, byte[] value) {
        variables.put(
                ByteArrayKey.of(id),
                value
        );
    }
}
