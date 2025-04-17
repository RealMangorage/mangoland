package org.mangorage.mangoland.engine.constants;

public final class CommonFlags {
    public static final int includeData             = 1;
    public static final int includeDataType         = 2;
    public static final int includeParameterMarkers = 3;

    public static final int includeAll = includeData
                    | includeDataType
                    | includeParameterMarkers;
}
