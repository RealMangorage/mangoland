package org.mangorage.mangolang.object;

import org.mangorage.mangolang.object.impl.StringMLObject;

// Marker class for objects that are in MangoLang
public interface MangolangObject {
    default MangolangObject equals(MangolangObject mangolangObject) {
        return operator(mangolangObject, OperationType.EQUALS);
    }

    MangolangObject operator(MangolangObject mangolangObject, OperationType type);

    MangolangObject asString();

    default String toDisplayString() {
        MangolangObject stringValue = asString();
        if (stringValue instanceof StringMLObject stringObject) {
            return stringObject.getValue();
        }

        return String.valueOf(stringValue);
    }

    default String describe() {
        return getClass().getSimpleName() + "(" + toDisplayString() + ")";
    }
}
