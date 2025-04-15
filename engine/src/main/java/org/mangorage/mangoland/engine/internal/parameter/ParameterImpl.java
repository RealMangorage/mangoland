package org.mangorage.mangoland.engine.internal.parameter;

import org.mangorage.mangoland.engine.api.datatype.DataType;
import org.mangorage.mangoland.engine.api.parameter.Parameter;
import org.mangorage.mangoland.engine.api.variable.Variable;
import org.mangorage.mangoland.engine.constants.InstructionConstants;
import org.mangorage.mangoland.engine.util.ByteUtil;

public final class ParameterImpl implements Parameter {

    private final Variable variable;

    public ParameterImpl(final Variable variable) {
        this.variable = variable;
    }

    public boolean is(final DataType<?> dataType) {
        return this.variable.getDataType().equals(dataType);
    }

    @SuppressWarnings("unchecked")
    public <T> T asObject() {
        return (T) variable.asObject();
    }

    public <T> T asObject(final Class<T> tClass) {
        return asObject();
    }

    public DataType<?> getDataType() {
        return variable.getDataType();
    }

    @Override
    public Variable getVariable() {
        return variable;
    }

    @Override
    public byte[] getFullData() {
        return ByteUtil.merge(
                InstructionConstants.PARAMETER_START.get(),
                variable.getFullData(),
                InstructionConstants.PARAMETER_END.get()
        );
    }


}
