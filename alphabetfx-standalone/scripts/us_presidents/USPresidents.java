///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 11

import static java.net.http.HttpClient.newHttpClient;
import static java.net.http.HttpResponse.BodyHandlers.ofByteArray;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class USPresidents {

    static final Path BASE_PATH = Paths.get("images");
    static final Path NAMES = Paths.get("presidents.txt");

    public static void main(String... args) throws Exception {
        if (!Files.exists(BASE_PATH)) {
            Files.createDirectory(BASE_PATH);
        }
        Files.deleteIfExists(NAMES);
        Files.createFile(NAMES);
        var i = new AtomicInteger();
        var names = new ArrayList<String>();
        Files.lines(Paths.get("presidents_urls.txt")).map(l -> l.split(",")).forEach(p -> {
            var name = p[1] + " (" + i.incrementAndGet() + "º)";
            var fileName = name + ".jpg";
            var bytes = getImage(p[0]);
            save(bytes, fileName);
            System.out.println(name);
            names.add(name);
        });
        saveNames(names);

    }

    static void saveNames(List<String> names) throws Exception {
        var namesWriter = new BufferedWriter(new FileWriter(NAMES.toFile()));
        for (var name : names) {
            namesWriter.append(name);
            namesWriter.newLine();;
        }
        namesWriter.close();
    }

    private static void save(byte[] bytes, String name) {
        try {
            var p = BASE_PATH.resolve(name);
            Files.deleteIfExists(p);
            Files.write(p, bytes, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static byte[] getImage(String url) {
        var uri = URI.create(url);
        var request = HttpRequest.newBuilder(uri).GET().build();
        try {
            return newHttpClient().send(request, ofByteArray()).body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
