package org.mangorage.mangland.java.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface IScriptProvider {
    byte[] compile(final String code);
    void execute(final byte[] instructions);

    default byte[] compileScript(final Path file) {
        try {
            return compile(
                    Files.readString(file)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    default void executeCompiledScript(final Path file) {
        try {
            execute(
                    Files.readAllBytes(file)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    default void executeUnCompiledScript(final Path file) {
        execute(compileScript(file));
    }

    default void executeScript(final Path file) {
        final String extension = Util.getExtension(file);

        if (extension.equals("ml")) {
            executeCompiledScript(file);
        } else {
            executeUnCompiledScript(file);
        }
    }
}
