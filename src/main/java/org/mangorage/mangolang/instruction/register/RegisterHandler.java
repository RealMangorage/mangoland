package org.mangorage.mangolang.instruction.register;

import org.mangorage.mangolang.instruction.Instruction;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class RegisterHandler {

    public List<BakedInstruction> bake(Class<?> clazz) {
        if (!Instruction.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Class does not implement Instruction: " + clazz.getName());
        }

        Class<? extends Instruction> instructionClass = clazz.asSubclass(Instruction.class);

        try {
            Instruction instruction = instructionClass.getDeclaredConstructor().newInstance();
            Set<String> ids = new LinkedHashSet<>();
            ids.add(normalizeId(instructionClass.getSimpleName()));

            for (AutoRegisterInstruction annotation : instructionClass.getAnnotationsByType(AutoRegisterInstruction.class)) {
                String id = annotation.id();
                if (id != null && !id.isBlank()) {
                    ids.add(normalizeId(id));
                }
            }

            List<BakedInstruction> bakedInstructions = new ArrayList<>(ids.size());
            for (String id : ids) {
                bakedInstructions.add(new BakedInstruction(id, instruction));
            }

            return bakedInstructions;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to bake instruction: " + clazz.getName(), e);
        }
    }

    private String normalizeId(String id) {
        return id.toLowerCase();
    }
}

