package org.mangorage.mangland.java.internal;


import org.mangorage.mangland.java.api.IScriptProvider;
import org.mangorage.mangland.java.api.ScriptProvider;
import org.mangorage.mangland.java.api.Util;
import org.mangorage.scanner.api.Scanner;
import org.mangorage.scanner.api.ScannerBuilder;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
    java -jar mangoland-java.jar compile example.mangoland
    java -jar mangoland-java.jar execute example.ml / example.mangoland


    Its faster to run code thats already compiled.e

    Also it will inherently obfuscate it, by ignoring anything
    that isnt relevent to being compiled

    such as

    #print '1' -> // -> Isnt gonna be there. We only care about stuff inside quotes
 */
public final class MangolandJava {

    private static final Map<String, IScriptProvider> scriptProviders = load();

    private static Map<String, IScriptProvider> load() {
        final Scanner scanner = ScannerBuilder.of()
                .addClassloader(new URLClassLoader(fetchJars()))
                .addClasspath(Thread.currentThread().getContextClassLoader())
                .build();


        scanner.commitScan();

        final Map<String, IScriptProvider> providerMap = new HashMap<>();

        scanner.findClassesWithAnnotation(ScriptProvider.class)
                .forEach(clz -> {
                    final var annotation = clz.getAnnotation(ScriptProvider.class);
                    try {
                        providerMap.put(
                                annotation.id(),
                                (IScriptProvider) clz.getConstructor().newInstance()
                        );
                    } catch (ReflectiveOperationException e) {
                        throw new IllegalStateException(e);
                    }
                });

        return Map.copyOf(providerMap);
    }

    public static void main(final String[] args) throws IOException {

        System.out.println("Args:");
        System.out.println(
                Arrays.toString(args)
        );
        System.out.println("----------------------------------------");

        if (args.length == 1 && args[0].equals("listProviders")) {
            scriptProviders.forEach((k, v) -> {
                System.out.println("Provider with id '%s'".formatted(k));
                System.out.println(v);
            });
            System.exit(0);
            return;
        }

        if (args.length != 3) {
            System.out.println("""
                    CLI:
                    
                    java -jar mangoland-java.jar compile provider example.mangoland
                    java -jar mangoland-java.jar execute provider example.mangoland / example.ml
                    java -jar mangoland-java.jar listProviders
                    
                    .ml is a compiled form of .mangoland
                    """);
            System.exit(0);
            return;
        }

        final var provider = scriptProviders.get(args[0]);
        final String cmd = args[1];
        final var file = Path.of(args[2]);

        if (cmd.equals("execute")) {
            System.out.println("Running %s now".formatted(file));
            System.out.println("----------------------------------------");
            provider.executeScript(file);
            System.out.println("----------------------------------------");
            System.out.println("Finished executing....");
            System.exit(0);
            return;
        } else if (cmd.equals("compile")) {
            final var output = Util.stripExtension(file) + ".ml";

            System.out.println("Compiling %s now".formatted(file));
            System.out.println("----------------------------------------");
            Files.write(
                    Path.of(output),
                    provider.compileScript(file),
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE
            );
            System.out.println("----------------------------------------");
            System.out.println("Finished Compiling!");
            System.exit(0);
            return;
        }

        System.exit(0);
    }

    public static URL[] fetchJars() {
        final File providerDir = new File("providers");
        if (!providerDir.exists() || !providerDir.isDirectory()) return new URL[0];

        final File[] jarFiles = providerDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jarFiles == null) {
            return new URL[0];
        }

        final List<URL> urls = new ArrayList<>();
        for (final File jar : jarFiles) {
            try {
                urls.add(jar.toURI().toURL());
            } catch (MalformedURLException e) {
                throw new RuntimeException("Malformed URL while processing: " + jar.getName(), e);
            }
        }

        return urls.toArray(URL[]::new);
    }
}
