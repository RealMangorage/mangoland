package org.mangorage.mangolang.compiler;

import java.util.HashMap;
import java.util.Map;

public final class CompilerContext {
    private final Map<String, Integer> variables = new HashMap<>();
    private final Map<String, Integer> functions = new HashMap<>();

    private int nextVar = 0;

    public int declareVariable(String name) {
        if (variables.containsKey(name))
            throw new RuntimeException("Variable already declared: " + name);

        int index = nextVar++;
        variables.put(name, index);
        return index;
    }

    public int getVariableIndex(String name) {
        Integer index = variables.get(name);
        if (index == null)
            throw new RuntimeException("Unknown variable: " + name);
        return index;
    }

    public boolean hasVariable(String name) {
        return variables.containsKey(name);
    }

    public void registerFunction(String name, int address) {
        functions.put(name, address);
    }

    public int getFunction(String name) {
        Integer addr = functions.get(name);
        if (addr == null)
            throw new RuntimeException("Unknown function: " + name);
        return addr;
    }
}