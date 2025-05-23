package org.mangorage.mangoland.engine.util;

import java.util.Arrays;
import java.util.List;

public final class ByteUtil {
    public static byte[][] extractBetween(final byte[] data, final byte[] startMarker, final byte[] endMarker) {
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
    private static int indexOf(final byte[] data, final byte[] target) {
        return indexOf(data, target, 0);
    }

    private static int indexOf(final byte[] data, final byte[] target, final int fromIndex) {
        outer:
        for (int i = fromIndex; i <= data.length - target.length; i++) {
            for (int j = 0; j < target.length; j++) {
                if (data[i + j] != target[j]) continue outer;
            }
            return i;
        }
        return -1;
    }

    // LE
    public static int bytesToInt(final byte[] bytes) {
        if (bytes == null || bytes.length != 4) {
            throw new IllegalArgumentException("Expected 4 bytes, got " + (bytes == null ? "null" : bytes.length));
        }

        return (bytes[3] & 0xFF) << 24 |
                (bytes[2] & 0xFF) << 16 |
                (bytes[1] & 0xFF) << 8  |
                (bytes[0] & 0xFF);
    }

    // LE
    public static byte[] intToBytes(final int value) {
        return new byte[] {
                (byte) value,
                (byte)(value >>> 8),
                (byte)(value >>> 16),
                (byte)(value >>> 24)
        };
    }

    public static byte[] merge(final byte[]... arrays) {
        if (arrays == null || arrays.length == 0)
            return new byte[0];

        if (arrays.length == 1)
            return arrays[0];

        int totalLength = 0;
        for (final byte[] array : arrays) {
            if (array != null) {
                totalLength += array.length;
            }
        }

        final byte[] result = new byte[totalLength];
        int pos = 0;
        for (final byte[] array : arrays) {
            if (array != null) {
                System.arraycopy(array, 0, result, pos, array.length);
                pos += array.length;
            }
        }

        return result;
    }
}
