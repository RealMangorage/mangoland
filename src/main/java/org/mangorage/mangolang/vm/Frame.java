package org.mangorage.mangolang.vm;

import org.mangorage.mangolang.object.MangolangObject;

public class Frame {
    public static final int LOCAL_INDEX_BASE = 256;
    public static final int LOCAL_CAPACITY = 256;

    public int returnIp;
    public MangolangObject[] globals;
    public MangolangObject[] locals;

    public Frame(int returnIp, int globalSize) {
        this.returnIp = returnIp;
        this.globals = new MangolangObject[globalSize];
        this.locals = new MangolangObject[LOCAL_CAPACITY];
    }

    public Frame(int returnIp, MangolangObject[] globals) {
        this.returnIp = returnIp;
        this.globals = globals;
        this.locals = new MangolangObject[LOCAL_CAPACITY];
    }
}
