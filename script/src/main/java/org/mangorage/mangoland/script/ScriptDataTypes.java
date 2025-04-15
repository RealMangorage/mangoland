package org.mangorage.mangoland.script;

import org.mangorage.mangoland.engine.api.datatype.DataType;
import org.mangorage.mangoland.script.datatypes.DataTypeType;
import org.mangorage.mangoland.script.datatypes.IntegerType;
import org.mangorage.mangoland.script.datatypes.StringType;
import org.mangorage.mangoland.script.datatypes.VariableType;

public final class ScriptDataTypes {
    public static final DataType<Void> VARIABLE = DataType.of(new byte[] { 0x2e, 0x2A, 0x77, 0x01 }, VariableType::new);
    public static final DataType<String> STRING_TYPE = DataType.of(new byte[] { 0x45, 0x60, 0x13, 0x7F }, StringType::new);
    public static final DataType<Integer> INTEGER_TYPE = DataType.of(new byte[] { 0x3B, 0x09, 0x5C, 0x22 }, IntegerType::new);
    public static final DataType<DataType<?>> DATA_TYPE = DataType.of(new byte[] { 0x3B, 0x09, 0x5C, 0x23 }, DataTypeType::new);
}
