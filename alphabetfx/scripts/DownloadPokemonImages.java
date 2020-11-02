///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 11

import static java.net.http.HttpClient.newHttpClient;
import static java.net.http.HttpResponse.BodyHandlers.ofByteArray;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.IntStream;

public class DownloadPokemonImages {

    private static final int MAX_POKEMONS = 893;

    public static void main(String... args) throws Exception {
        var names = Files.lines(Paths.get("names.txt")).toArray();
        var basePath = Paths.get("./images");
        if (!Files.exists(basePath)) {
            Files.createDirectory(basePath);
        }
        IntStream.rangeClosed(1, MAX_POKEMONS).forEach(i -> {
            var name = names.length >= i ? names[i - 1] : "" + i;
            var number = String.format("%03d", i);
            var url = "https://assets.pokemon.com/assets/cms2/img/pokedex/detail/" + number + ".png";
            System.out.println(url);
            var imgBytes = getImage(url);
            try {
                var p = basePath.resolve(name + ".png");
                Files.deleteIfExists(p);
                Files.write(p, imgBytes, StandardOpenOption.CREATE_NEW);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

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