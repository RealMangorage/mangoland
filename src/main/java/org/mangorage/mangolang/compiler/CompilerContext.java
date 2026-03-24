package org.mangorage.mangolang.compiler;

import org.mangorage.mangolang.vm.Frame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public final class CompilerContext {
    public record FunctionInfo(String name, int address, List<String> parameters) {
        public int parameterCount() {
            return parameters.size();
        }
    }

    private static final class FunctionScope {
        private final String name;
        private final Map<String, Integer> variables = new HashMap<>();
        private int nextLocalIndex = 0;

        private FunctionScope(String name, List<String> parameters) {
            this.name = name;
            for (String parameter : parameters) {
                declareVariable(parameter);
            }
        }

        private int declareVariable(String name) {
            if (variables.containsKey(name)) {
                throw new RuntimeException("Variable already declared in function '" + this.name + "': " + name);
            }

            int index = Frame.LOCAL_INDEX_BASE + nextLocalIndex++;
            variables.put(name, index);
            return index;
        }

        private boolean hasVariable(String name) {
            return variables.containsKey(name);
        }

        private int getVariableIndex(String name) {
            Integer index = variables.get(name);
            if (index == null) {
                throw new RuntimeException("Unknown function variable '" + name + "' in function '" + this.name + "'");
            }
            return index;
        }
    }

    private final Map<String, Integer> globalVariables = new HashMap<>();
    private final Map<String, FunctionInfo> functions = new HashMap<>();
    private final Stack<FunctionScope> functionScopes = new Stack<>();

    private int nextGlobalVar = 0;
    private String pendingLoopLabel;

    public int declareVariable(String name) {
        if (!functionScopes.isEmpty()) {
            return functionScopes.peek().declareVariable(name);
        }

        if (globalVariables.containsKey(name))
            throw new RuntimeException("Variable already declared: " + name);

        int index = nextGlobalVar++;
        globalVariables.put(name, index);
        return index;
    }

    public int getVariableIndex(String name) {
        if (!functionScopes.isEmpty() && functionScopes.peek().hasVariable(name)) {
            return functionScopes.peek().getVariableIndex(name);
        }

        Integer globalIndex = globalVariables.get(name);
        if (globalIndex != null) {
            return globalIndex;
        }

        throw new RuntimeException("Unknown variable: " + name);
    }

    public boolean hasVariable(String name) {
        return (!functionScopes.isEmpty() && functionScopes.peek().hasVariable(name)) || globalVariables.containsKey(name);
    }

    public void registerFunction(String name, int address, List<String> parameters) {
        if (functions.containsKey(name)) {
            throw new RuntimeException("Function already declared: " + name);
        }

        functions.put(name, new FunctionInfo(name, address, List.copyOf(parameters)));
    }

    public int getFunction(String name) {
        return getFunctionInfo(name).address();
    }

    public FunctionInfo getFunctionInfo(String name) {
        FunctionInfo info = functions.get(name);
        if (info == null)
            throw new RuntimeException("Unknown function: " + name);
        return info;
    }

    public boolean hasFunction(String name) {
        return functions.containsKey(name);
    }

    public void beginFunction(String name, int address, List<String> parameters) {
        registerFunction(name, address, parameters);
        functionScopes.push(new FunctionScope(name, new ArrayList<>(parameters)));
    }

    public void endFunction() {
        if (functionScopes.isEmpty()) {
            throw new RuntimeException("Cannot end function scope when no function is active");
        }

        functionScopes.pop();
    }

    public void setPendingLoopLabel(String pendingLoopLabel) {
        this.pendingLoopLabel = pendingLoopLabel;
    }

    public String consumePendingLoopLabel() {
        String label = pendingLoopLabel;
        pendingLoopLabel = null;
        return label;
    }
}