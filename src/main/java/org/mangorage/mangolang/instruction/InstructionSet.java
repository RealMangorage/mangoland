package org.mangorage.mangolang.instruction;

import org.mangorage.mangolang.instruction.register.AutoRegisterInstruction;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class InstructionSet {
    private final Map<Integer, Instruction> opcodeMap = new HashMap<>();
    private final Map<String, Integer> nameMap = new HashMap<>();
    private final Map<Integer, String> opcodeToNameMap = new HashMap<>();

    private int nextOpcode = 1;

    public void register(List<Class<? extends Instruction>> classList) {
        for (Class<? extends Instruction> aClass : classList) {
            try {
                Instruction instruction = aClass.getDeclaredConstructor().newInstance();
                Set<String> names = new LinkedHashSet<>();
                names.add(normalizeName(aClass.getSimpleName()));

                for (AutoRegisterInstruction annotation : aClass.getAnnotationsByType(AutoRegisterInstruction.class)) {
                    String id = annotation.id();
                    if (id != null && !id.isBlank()) {
                        names.add(normalizeName(id));
                    }
                }

                Integer opcode = null;
                for (String name : names) {
                    if (opcode == null) {
                        opcode = register(name, instruction);
                    } else {
                        registerAlias(name, opcode);
                    }
                }
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int register(String name, Instruction inst) {
        String normalizedName = normalizeName(name);
        ensureNameAvailable(normalizedName);

        int opcode = nextOpcode++;
        opcodeMap.put(opcode, inst);
        nameMap.put(normalizedName, opcode);
        opcodeToNameMap.put(opcode, normalizedName);
        return opcode;
    }

    public void registerAlias(String name, int opcode) {
        String normalizedName = normalizeName(name);
        ensureNameAvailable(normalizedName);

        if (!opcodeMap.containsKey(opcode)) {
            throw new IllegalArgumentException("Instruction opcode not registered: " + opcode);
        }

        nameMap.put(normalizedName, opcode);
    }

    public Instruction get(int opcode) {
        return opcodeMap.get(opcode);
    }

    public Integer getOpcode(String name) {
        return nameMap.get(normalizeName(name));
    }

    public String getName(int opcode) {
        return opcodeToNameMap.get(opcode);
    }

    public int requireOpcode(String name) {
        Integer op = nameMap.get(normalizeName(name));
        if (op == null) {
            throw new RuntimeException("Instruction not registered: " + name);
        }
        return op;
    }

    private String normalizeName(String name) {
        return name.toLowerCase();
    }

    private void ensureNameAvailable(String normalizedName) {
        if (nameMap.containsKey(normalizedName)) {
            throw new IllegalStateException("Instruction already registered: " + normalizedName);
        }
    }

    public String getDebugInfo() {
        return "";
    }
}