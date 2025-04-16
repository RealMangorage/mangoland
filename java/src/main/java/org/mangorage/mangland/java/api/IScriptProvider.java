package org.mangorage.mangland.java.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface IScriptProvider {
    byte[] compile(String code);
    void execute(byte[] instructions);

    default byte[] compileScript(Path file) {
        try {
            return compile(
                    Files.readString(file)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    default void executeCompiledScript(Path file) {
        try {
            execute(
                    Files.readAllBytes(file)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    default void executeUnCompiledScript(Path file) {
        execute(compileScript(file));
    }

    default void executeScript(Path file) {
        String extension = Util.getExtension(file);

        if (extension.equals("ml")) {
            executeCompiledScript(file);
        } else {
            executeUnCompiledScript(file);
        }
    }
}
