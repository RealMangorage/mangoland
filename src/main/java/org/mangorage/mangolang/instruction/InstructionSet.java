package org.mangorage.mangolang.instruction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class InstructionSet {
    private final Map<Integer, Instruction> opcodeMap = new HashMap<>();
    private final Map<String, Integer> nameMap = new HashMap<>();
    private final Map<Integer, String> opcodeToNameMap = new HashMap<>();

    private int nextOpcode = 1;

    public void register(List<Class<? extends Instruction>> classList) {
        for (Class<? extends Instruction> aClass : classList) {
            try {
                register(
                        aClass.getSimpleName().toLowerCase(),
                        aClass.newInstance()
                );
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int register(String name, Instruction inst) {
        int opcode = nextOpcode++;
        opcodeMap.put(opcode, inst);
        nameMap.put(name.toLowerCase(), opcode);
        opcodeToNameMap.put(opcode, name.toLowerCase());
        return opcode;
    }

    public Instruction get(int opcode) {
        return opcodeMap.get(opcode);
    }

    public Integer getOpcode(String name) {
        return nameMap.get(name.toLowerCase());
    }

    public String getName(int opcode) {
        return opcodeToNameMap.get(opcode);
    }

    public int requireOpcode(String name) {
        Integer op = nameMap.get(name.toLowerCase());
        if (op == null) {
            throw new RuntimeException("Instruction not registered: " + name);
        }
        return op;
    }
}