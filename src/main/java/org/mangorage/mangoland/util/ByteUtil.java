package org.mangorage.mangoland.util;

import java.nio.ByteBuffer;
import java.util.Arrays;

public final class ByteUtil {
    public static byte[][] extractBetween(byte[] data, byte[] startMarker, byte[] endMarker) {
        if (startMarker.length != 4 || endMarker.length != 4)
            throw new IllegalArgumentException("Your markers aren’t 4 bytes long, fix your damn input.");

        // First, count how many chunks we have
        int chunkCount = 0;
        int start = indexOf(data, startMarker);
        while (start != -1) {
            int end = indexOf(data, endMarker, start + 4);
            if (end == -1) break;

            // Found a chunk
            chunkCount++;
            start = indexOf(data, startMarker, end + 4); // Move to the next start
        }

        // If no chunks found, return an empty 2D array
        if (chunkCount == 0) return new byte[0][0];

        // Now allocate the byte[][] array with the correct size
        byte[][] chunks = new byte[chunkCount][];
        start = indexOf(data, startMarker);
        int index = 0;

        while (start != -1) {
            int end = indexOf(data, endMarker, start + 4);
            if (end == -1) break;

            // Extract the chunk
            byte[] chunk = Arrays.copyOfRange(data, start + 4, end);
            chunks[index++] = chunk;

            // Move start to search for the next pair
            start = indexOf(data, startMarker, end + 4); // Search for next start after current end
        }

        return chunks;
    }

    // Brutally ugly brute-force byte matcher
    private static int indexOf(byte[] data, byte[] target) {
        return indexOf(data, target, 0);
    }

    private static int indexOf(byte[] data, byte[] target, int fromIndex) {
        outer:
        for (int i = fromIndex; i <= data.length - target.length; i++) {
            for (int j = 0; j < target.length; j++) {
                if (data[i + j] != target[j]) continue outer;
            }
            return i;
        }
        return -1;
    }

    public static int bytesToInt(byte[] bytes) {
        if (bytes.length != 4) {
            throw new IllegalArgumentException("You need exactly 4 bytes.");
        }

        return (bytes[0] & 0xFF) << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }

    public static byte[] intToBytesLE(int value) {
        return ByteBuffer.allocate(4).order(java.nio.ByteOrder.LITTLE_ENDIAN).putInt(value).array();
    }

    public static byte[] merge(byte[] a, byte[] b) {
        if (a == null) a = new byte[0];
        if (b == null) b = new byte[0];

        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}
