package org.mangorage.mangolang;


import java.io.*;

public final class Util {

    public static void saveProgram(String target, byte[] bytecode) {
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(target)))) {
            out.writeInt(bytecode.length); // store length first
            out.write(bytecode);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save program", e);
        }
    }

    public static byte[] loadProgram(String target) {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(target)))) {
            int length = in.readInt(); // read length first
            byte[] bytecode = new byte[length];

            in.readFully(bytecode);
            return bytecode;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load program", e);
        }
    }
}