package org.mangorage.mangolang.vm;

public class Frame {
    public int returnIp;
    public int[] locals;

    public Frame(int returnIp, int localSize) {
        this.returnIp = returnIp;
        this.locals = new int[localSize];
    }
}
