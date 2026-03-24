package org.mangorage.mangolang.object;

import org.mangorage.mangolang.object.impl.BooleanMLObject;
import org.mangorage.mangolang.object.impl.DoubleMLObject;
import org.mangorage.mangolang.object.impl.FloatMLObject;
import org.mangorage.mangolang.object.impl.IntegerMLObject;
import org.mangorage.mangolang.object.impl.LongMLObject;
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

    default String typeName() {
        return "object";
    }

    default boolean isNumeric() {
        return false;
    }

    default int numericRank() {
        throw new IllegalStateException("Object is not numeric: " + describe());
    }

    default int asIntValue() {
        throw new IllegalStateException("Object is not numeric: " + describe());
    }

    default long asLongValue() {
        throw new IllegalStateException("Object is not numeric: " + describe());
    }

    default float asFloatValue() {
        throw new IllegalStateException("Object is not numeric: " + describe());
    }

    default double asDoubleValue() {
        throw new IllegalStateException("Object is not numeric: " + describe());
    }

    default MangolangObject applyNumericOperation(MangolangObject other, OperationType type) {
        if (!other.isNumeric()) {
            throw MangolangObjects.unsupportedOperation(this, other, type);
        }

        int rank = Math.max(numericRank(), other.numericRank());
        return switch (type) {
            case ADD -> switch (rank) {
                case 0 -> new IntegerMLObject(asIntValue() + other.asIntValue());
                case 1 -> new LongMLObject(asLongValue() + other.asLongValue());
                case 2 -> new FloatMLObject(asFloatValue() + other.asFloatValue());
                case 3 -> new DoubleMLObject(asDoubleValue() + other.asDoubleValue());
                default -> throw new IllegalStateException("Unsupported numeric rank: " + rank);
            };
            case SUBTRACT -> switch (rank) {
                case 0 -> new IntegerMLObject(asIntValue() - other.asIntValue());
                case 1 -> new LongMLObject(asLongValue() - other.asLongValue());
                case 2 -> new FloatMLObject(asFloatValue() - other.asFloatValue());
                case 3 -> new DoubleMLObject(asDoubleValue() - other.asDoubleValue());
                default -> throw new IllegalStateException("Unsupported numeric rank: " + rank);
            };
            case MULTIPLY -> switch (rank) {
                case 0 -> new IntegerMLObject(asIntValue() * other.asIntValue());
                case 1 -> new LongMLObject(asLongValue() * other.asLongValue());
                case 2 -> new FloatMLObject(asFloatValue() * other.asFloatValue());
                case 3 -> new DoubleMLObject(asDoubleValue() * other.asDoubleValue());
                default -> throw new IllegalStateException("Unsupported numeric rank: " + rank);
            };
            case DIVIDE -> switch (rank) {
                case 0 -> {
                    int divisor = other.asIntValue();
                    if (divisor == 0) {
                        throw new RuntimeException("Cannot divide by zero");
                    }
                    yield new IntegerMLObject(asIntValue() / divisor);
                }
                case 1 -> {
                    long divisor = other.asLongValue();
                    if (divisor == 0L) {
                        throw new RuntimeException("Cannot divide by zero");
                    }
                    yield new LongMLObject(asLongValue() / divisor);
                }
                case 2 -> {
                    float divisor = other.asFloatValue();
                    if (Float.compare(divisor, 0.0f) == 0) {
                        throw new RuntimeException("Cannot divide by zero");
                    }
                    yield new FloatMLObject(asFloatValue() / divisor);
                }
                case 3 -> {
                    double divisor = other.asDoubleValue();
                    if (Double.compare(divisor, 0.0d) == 0) {
                        throw new RuntimeException("Cannot divide by zero");
                    }
                    yield new DoubleMLObject(asDoubleValue() / divisor);
                }
                default -> throw new IllegalStateException("Unsupported numeric rank: " + rank);
            };
            case EQUALS -> BooleanMLObject.of(switch (rank) {
                case 0 -> asIntValue() == other.asIntValue();
                case 1 -> asLongValue() == other.asLongValue();
                case 2 -> Float.compare(asFloatValue(), other.asFloatValue()) == 0;
                case 3 -> Double.compare(asDoubleValue(), other.asDoubleValue()) == 0;
                default -> throw new IllegalStateException("Unsupported numeric rank: " + rank);
            });
            case GREATER_THAN -> BooleanMLObject.of(switch (rank) {
                case 0 -> asIntValue() > other.asIntValue();
                case 1 -> asLongValue() > other.asLongValue();
                case 2 -> asFloatValue() > other.asFloatValue();
                case 3 -> asDoubleValue() > other.asDoubleValue();
                default -> throw new IllegalStateException("Unsupported numeric rank: " + rank);
            });
        };
    }
}
