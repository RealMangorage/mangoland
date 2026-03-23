package org.mangorage.mangolang.vm;

public class Frame {
    public int returnIp;
    // locals now store MangolangObject instances (allows integers, strings, custom objects, etc.)
    public org.mangorage.mangolang.object.MangolangObject[] locals;

    public Frame(int returnIp, int localSize) {
        this.returnIp = returnIp;
        this.locals = new org.mangorage.mangolang.object.MangolangObject[localSize];
    }
}
