package org.mangorage.mangolang;


import java.io.*;

public final class Util {

    public static void saveProgram(String target, int[] bytecode) {
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(target)))) {
            out.writeInt(bytecode.length); // store length first
            for (int value : bytecode) {
                out.writeInt(value);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save program", e);
        }
    }

    public static int[] loadProgram(String target) {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(target)))) {
            int length = in.readInt(); // read length first
            int[] bytecode = new int[length];

            for (int i = 0; i < length; i++) {
                bytecode[i] = in.readInt();
            }

            return bytecode;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load program", e);
        }
    }
}