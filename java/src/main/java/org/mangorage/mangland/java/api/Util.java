package org.mangorage.mangland.java.api;

import java.nio.file.Path;

public final class Util {
    public static String getExtension(final Path file) {
        String extension = "";

        final var fileName = file.getFileName().toString();
        final int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i+1);
        }

        return extension;
    }

    public static String stripExtension(final Path path) {
        if (path == null || path.getFileName() == null) {
            return null; // wow, look at you breaking everything with nulls again
        }

        final String filename = path.getFileName().toString();
        final int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) {
            return filename; // no extension? congrats on making yet another weird file
        }

        return filename.substring(0, lastDot);
    }
}
