package org.mangorage.mangolang.object;

import java.util.List;

// Marker class for objects that are in MangoLang
public interface MangolangObject {
    MangolangObject equals(MangolangObject mangolangObject);

    void emitBytes(List<Byte> out);
}
