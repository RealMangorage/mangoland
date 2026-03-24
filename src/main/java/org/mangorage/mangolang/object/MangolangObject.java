package org.mangorage.mangolang.object;

// Marker class for objects that are in MangoLang
public interface MangolangObject {
    default MangolangObject equals(MangolangObject mangolangObject) {
        return operator(mangolangObject, OperationType.EQUALS);
    }

    MangolangObject operator(MangolangObject mangolangObject, OperationType type);

    MangolangObject asString();
}
