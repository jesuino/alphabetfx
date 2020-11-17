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

/*
Download images from a CSV file with format {fileName},{fileURL}
Usage: jbang DownloadImages {CSV file} {image extension}

Result:

* Folder with download images: images
* text file with all names without extension

*/
public class DownloadImages {

    static final Path BASE_PATH = Paths.get("images");
    static final Path NAMES = Paths.get("names.txt");

    public static void main(String... args) throws Exception {
        if (args.length < 2) {
            System.out.println("Invalid number of parameters. Provide image URLs CSV and the image extension.");
            System.exit(0);
        }
        var inputCSV = Paths.get(args[0]);
        var extension = args[1];
        if (!Files.exists(inputCSV)) {
            System.out.println("Input CSV does not exist: " + inputCSV.toString());
            System.exit(0);
        }

        if (!Files.exists(BASE_PATH)) {
            Files.createDirectory(BASE_PATH);
        }
        var names = new ArrayList<String>();
        Files.lines(inputCSV).map(l -> l.split(",")).forEach(p -> {
            var name = p[0];
            var fileName = name + "." + extension;
            var bytes = getImage(p[1]);
            save(bytes, fileName);
            System.out.println("Saved: " + name);
            names.add(name);
        });
        saveNames(names);
    }

    static void saveNames(List<String> names) throws Exception {
        Files.deleteIfExists(NAMES);
        Files.createFile(NAMES);
        var namesWriter = new BufferedWriter(new FileWriter(NAMES.toFile()));
        for (var name : names) {
            namesWriter.append(name);
            namesWriter.newLine();
            ;
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
