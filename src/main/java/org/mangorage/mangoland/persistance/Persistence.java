package org.mangorage.mangoland.persistance;

import org.mangorage.mangoland.data.ByteArrayKey;

import java.util.HashMap;
import java.util.Map;

public final class Persistence {
    private final Map<ByteArrayKey, byte[]> variables = new HashMap<>();

    public Persistence() {
//        // Taken from a variable!
//        variables.put(
//                ByteArrayKey.of(new byte[]{0x31}), new byte[]{
//                        84, 97, 107, 101, 110, 32, 102, 114, 111, 109, 32,
//                        97, 32, 118, 97, 114, 105, 97, 98, 108, 101, 33
//                }
//        );
    }

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
