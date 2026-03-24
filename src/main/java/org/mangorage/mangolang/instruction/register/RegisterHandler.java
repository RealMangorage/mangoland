package org.mangorage.mangolang.instruction.register;

import org.mangorage.mangolang.instruction.Instruction;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class RegisterHandler {

    public List<BakedInstruction> bake(Class<?> clazz) {
        if (!Instruction.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Class does not implement Instruction: " + clazz.getName());
        }

        Class<? extends Instruction> instructionClass = clazz.asSubclass(Instruction.class);

        AutoRegisterInstruction[] annotations = instructionClass.getAnnotationsByType(AutoRegisterInstruction.class);

        try {
            List<BakedInstruction> bakedInstructions = new ArrayList<>();
            Map<InstantiationKey, Instruction> instructionCache = new LinkedHashMap<>();

            if (annotations.length == 0) {
                bakedInstructions.add(new BakedInstruction(
                        normalizeId(instructionClass.getSimpleName()),
                        instantiate(instructionClass, instructionCache, new Object[0])
                ));
                return bakedInstructions;
            }

            for (AutoRegisterInstruction annotation : annotations) {
                String id = annotation.id();
                String bakedId = (id == null || id.isBlank())
                        ? normalizeId(instructionClass.getSimpleName())
                        : normalizeId(id);

                Instruction instruction = instantiateForAnnotation(instructionClass, instructionCache, annotation.params());
                BakedInstruction bakedInstruction = new BakedInstruction(bakedId, instruction);

                if (!containsEquivalentEntry(bakedInstructions, bakedInstruction)) {
                    bakedInstructions.add(bakedInstruction);
                }
            }

            return bakedInstructions;
        } catch (ReflectiveOperationException | IllegalArgumentException e) {
            throw new RuntimeException("Failed to bake instruction: " + clazz.getName(), e);
        }
    }

    private String normalizeId(String id) {
        return id.toLowerCase(Locale.ROOT);
    }

    private Object[] parseParams(Parameter[] params) {
        Object[] args = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            args[i] = params[i].type().parse(params[i].value());
        }
        return args;
    }

    private Instruction instantiateForAnnotation(
            Class<? extends Instruction> instructionClass,
            Map<InstantiationKey, Instruction> instructionCache,
            Parameter[] params
    ) throws ReflectiveOperationException {
        if (params.length == 0) {
            return instantiate(instructionClass, instructionCache, new Object[0]);
        }

        IllegalArgumentException failure;
        try {
            return instantiate(instructionClass, instructionCache, parseParams(params));
        } catch (IllegalArgumentException exception) {
            failure = exception;
        }

        if (hasNoArgConstructor(instructionClass)) {
            return instantiate(instructionClass, instructionCache, new Object[0]);
        }

        throw failure;
    }

    private Instruction instantiate(
            Class<? extends Instruction> instructionClass,
            Map<InstantiationKey, Instruction> instructionCache,
            Object[] args
    ) throws ReflectiveOperationException {
        Constructor<? extends Instruction> constructor = findConstructor(instructionClass, args);
        InstantiationKey key = new InstantiationKey(constructor, List.of(args.clone()));

        Instruction cachedInstruction = instructionCache.get(key);
        if (cachedInstruction != null) {
            return cachedInstruction;
        }

        constructor.setAccessible(true);
        Instruction instruction = constructor.newInstance(args);
        instructionCache.put(key, instruction);
        return instruction;
    }

    private Constructor<? extends Instruction> findConstructor(Class<? extends Instruction> instructionClass, Object[] args) {
        List<Constructor<? extends Instruction>> matches = new ArrayList<>();

        for (Constructor<?> constructor : instructionClass.getDeclaredConstructors()) {
            if (isCompatible(constructor.getParameterTypes(), args)) {
                @SuppressWarnings("unchecked")
                Constructor<? extends Instruction> typedConstructor = (Constructor<? extends Instruction>) constructor;
                matches.add(typedConstructor);
            }
        }

        if (matches.isEmpty()) {
            throw new IllegalArgumentException(
                    "No matching constructor for " + instructionClass.getName() + " with args " + Arrays.toString(args)
            );
        }

        if (matches.size() > 1) {
            throw new IllegalArgumentException(
                    "Multiple matching constructors for " + instructionClass.getName() + " with args " + Arrays.toString(args)
            );
        }

        return matches.get(0);
    }

    private boolean isCompatible(Class<?>[] parameterTypes, Object[] args) {
        if (parameterTypes.length != args.length) {
            return false;
        }

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = wrap(parameterTypes[i]);
            Object arg = args[i];

            if (arg == null) {
                if (parameterTypes[i].isPrimitive()) {
                    return false;
                }
                continue;
            }

            if (!parameterType.isInstance(arg)) {
                return false;
            }
        }

        return true;
    }

    private boolean hasNoArgConstructor(Class<? extends Instruction> instructionClass) {
        for (Constructor<?> constructor : instructionClass.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                return true;
            }
        }

        return false;
    }

    private Class<?> wrap(Class<?> type) {
        if (!type.isPrimitive()) {
            return type;
        }

        if (type == boolean.class) return Boolean.class;
        if (type == int.class) return Integer.class;
        if (type == long.class) return Long.class;
        if (type == double.class) return Double.class;
        if (type == float.class) return Float.class;
        if (type == char.class) return Character.class;
        if (type == byte.class) return Byte.class;
        if (type == short.class) return Short.class;

        return type;
    }

    private boolean containsEquivalentEntry(List<BakedInstruction> bakedInstructions, BakedInstruction candidate) {
        for (BakedInstruction bakedInstruction : bakedInstructions) {
            if (bakedInstruction.id().equals(candidate.id()) && bakedInstruction.instruction() == candidate.instruction()) {
                return true;
            }
        }

        return false;
    }

    private record InstantiationKey(Constructor<? extends Instruction> constructor, List<Object> args) {
    }
}

