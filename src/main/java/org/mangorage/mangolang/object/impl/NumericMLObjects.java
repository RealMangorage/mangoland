package org.mangorage.mangolang.object.impl;

import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.object.MangolangObjects;
import org.mangorage.mangolang.object.OperationType;

final class NumericMLObjects {
    private NumericMLObjects() {
    }

    static boolean isNumeric(MangolangObject value) {
        return value instanceof IntegerMLObject
                || value instanceof LongMLObject
                || value instanceof FloatMLObject
                || value instanceof DoubleMLObject;
    }

    static MangolangObject applyBinaryOperation(MangolangObject left, MangolangObject right, OperationType type) {
        if (!isNumeric(right)) {
            throw MangolangObjects.unsupportedOperation(left, right, type);
        }

        int rank = Math.max(rank(left), rank(right));
        return switch (type) {
            case ADD -> add(left, right, rank);
            case SUBTRACT -> subtract(left, right, rank);
            case MULTIPLY -> multiply(left, right, rank);
            case DIVIDE -> divide(left, right, rank);
            case EQUALS -> BooleanMLObject.of(equals(left, right, rank));
            case GREATER_THAN -> BooleanMLObject.of(greaterThan(left, right, rank));
        };
    }

    private static MangolangObject add(MangolangObject left, MangolangObject right, int rank) {
        return switch (rank) {
            case 0 -> new IntegerMLObject(asInt(left) + asInt(right));
            case 1 -> new LongMLObject(asLong(left) + asLong(right));
            case 2 -> new FloatMLObject(asFloat(left) + asFloat(right));
            case 3 -> new DoubleMLObject(asDouble(left) + asDouble(right));
            default -> throw new IllegalStateException("Unsupported numeric rank: " + rank);
        };
    }

    private static MangolangObject subtract(MangolangObject left, MangolangObject right, int rank) {
        return switch (rank) {
            case 0 -> new IntegerMLObject(asInt(left) - asInt(right));
            case 1 -> new LongMLObject(asLong(left) - asLong(right));
            case 2 -> new FloatMLObject(asFloat(left) - asFloat(right));
            case 3 -> new DoubleMLObject(asDouble(left) - asDouble(right));
            default -> throw new IllegalStateException("Unsupported numeric rank: " + rank);
        };
    }

    private static MangolangObject multiply(MangolangObject left, MangolangObject right, int rank) {
        return switch (rank) {
            case 0 -> new IntegerMLObject(asInt(left) * asInt(right));
            case 1 -> new LongMLObject(asLong(left) * asLong(right));
            case 2 -> new FloatMLObject(asFloat(left) * asFloat(right));
            case 3 -> new DoubleMLObject(asDouble(left) * asDouble(right));
            default -> throw new IllegalStateException("Unsupported numeric rank: " + rank);
        };
    }

    private static MangolangObject divide(MangolangObject left, MangolangObject right, int rank) {
        return switch (rank) {
            case 0 -> {
                int divisor = asInt(right);
                if (divisor == 0) {
                    throw new RuntimeException("Cannot divide by zero");
                }
                yield new IntegerMLObject(asInt(left) / divisor);
            }
            case 1 -> {
                long divisor = asLong(right);
                if (divisor == 0L) {
                    throw new RuntimeException("Cannot divide by zero");
                }
                yield new LongMLObject(asLong(left) / divisor);
            }
            case 2 -> {
                float divisor = asFloat(right);
                if (divisor == 0.0f) {
                    throw new RuntimeException("Cannot divide by zero");
                }
                yield new FloatMLObject(asFloat(left) / divisor);
            }
            case 3 -> {
                double divisor = asDouble(right);
                if (divisor == 0.0d) {
                    throw new RuntimeException("Cannot divide by zero");
                }
                yield new DoubleMLObject(asDouble(left) / divisor);
            }
            default -> throw new IllegalStateException("Unsupported numeric rank: " + rank);
        };
    }

    private static boolean equals(MangolangObject left, MangolangObject right, int rank) {
        return switch (rank) {
            case 0 -> asInt(left) == asInt(right);
            case 1 -> asLong(left) == asLong(right);
            case 2 -> Float.compare(asFloat(left), asFloat(right)) == 0;
            case 3 -> Double.compare(asDouble(left), asDouble(right)) == 0;
            default -> throw new IllegalStateException("Unsupported numeric rank: " + rank);
        };
    }

    private static boolean greaterThan(MangolangObject left, MangolangObject right, int rank) {
        return switch (rank) {
            case 0 -> asInt(left) > asInt(right);
            case 1 -> asLong(left) > asLong(right);
            case 2 -> asFloat(left) > asFloat(right);
            case 3 -> asDouble(left) > asDouble(right);
            default -> throw new IllegalStateException("Unsupported numeric rank: " + rank);
        };
    }

    private static int rank(MangolangObject value) {
        if (value instanceof IntegerMLObject) {
            return 0;
        }
        if (value instanceof LongMLObject) {
            return 1;
        }
        if (value instanceof FloatMLObject) {
            return 2;
        }
        if (value instanceof DoubleMLObject) {
            return 3;
        }
        throw new IllegalArgumentException("Unsupported numeric object: " + (value == null ? "null" : value.describe()));
    }

    static int asInt(MangolangObject value) {
        if (value instanceof IntegerMLObject integerObject) {
            return integerObject.getValue();
        }
        if (value instanceof LongMLObject longObject) {
            return (int) longObject.getValue();
        }
        if (value instanceof FloatMLObject floatObject) {
            return (int) floatObject.getValue();
        }
        if (value instanceof DoubleMLObject doubleObject) {
            return (int) doubleObject.getValue();
        }
        throw new IllegalArgumentException("Unsupported numeric object: " + (value == null ? "null" : value.describe()));
    }

    static long asLong(MangolangObject value) {
        if (value instanceof IntegerMLObject integerObject) {
            return integerObject.getValue();
        }
        if (value instanceof LongMLObject longObject) {
            return longObject.getValue();
        }
        if (value instanceof FloatMLObject floatObject) {
            return (long) floatObject.getValue();
        }
        if (value instanceof DoubleMLObject doubleObject) {
            return (long) doubleObject.getValue();
        }
        throw new IllegalArgumentException("Unsupported numeric object: " + (value == null ? "null" : value.describe()));
    }

    static float asFloat(MangolangObject value) {
        if (value instanceof IntegerMLObject integerObject) {
            return integerObject.getValue();
        }
        if (value instanceof LongMLObject longObject) {
            return longObject.getValue();
        }
        if (value instanceof FloatMLObject floatObject) {
            return floatObject.getValue();
        }
        if (value instanceof DoubleMLObject doubleObject) {
            return (float) doubleObject.getValue();
        }
        throw new IllegalArgumentException("Unsupported numeric object: " + (value == null ? "null" : value.describe()));
    }

    static double asDouble(MangolangObject value) {
        if (value instanceof IntegerMLObject integerObject) {
            return integerObject.getValue();
        }
        if (value instanceof LongMLObject longObject) {
            return longObject.getValue();
        }
        if (value instanceof FloatMLObject floatObject) {
            return floatObject.getValue();
        }
        if (value instanceof DoubleMLObject doubleObject) {
            return doubleObject.getValue();
        }
        throw new IllegalArgumentException("Unsupported numeric object: " + (value == null ? "null" : value.describe()));
    }
}


