AlphabetFX
--

A sort of animation using JavaFX to animated images that starts with a letter.

Currently supported:

* pokemons
* us_presidents
* br_presidents

## Running

It requires maven and JDK 11+. To run go to `alphabetfx-standalone` and run:

```
mvn javafx:run
```
## Customizing

The following system properties should allow users to customize the app:
    private static final String NO_REPEAT_PROP = "noRepeat";
    private static final String AUTO_PLAY_PROP = "autoPlay";
    private static final String BG_COLOR_PROP = "bgColor";
    private static final String DECORATED_PROP = "decorated";
* `collections`: the images that will be displayed. Supported values are: `pokemons`,`br_presidents`, `us_presidents` - default is pokemons;
* `autoPlay`: when true the app will play automatically - when false users need to click to advanced - default is true;
* `decorated`: when false the stage will not show the bar - default is false;
* `bgColor`: The app background color using Web format;
* `noRepeat`: When true the images won't be repeated and eventually the system will end showing images;

## Extending

To extend to support other images:

1) Create a folder X (replace X with the collections name) in `alphabetfx-standalone/src/main/resources/images/details` and place all the images there;
2) Create a file with the same X name you used for the folder in `alphabetfx-standalone/src/main/resources/names/` - this file should contain all images names separated by line. This file must have the extension `.dat`;
3) Now you can run the program by setting the system property collections with the value you gave to the folder (X).

## Building and Packaging

### Standalone

Go to `alphabetfx-standalone` and run  `mvn clean install`. The JAR will be in `alphabetfx-standalone/target` folder and you can run it normally using `java -jar`. Here's an example:
```
java -jar -DbgColor=#AAA -Dcollections=br_presidents -DautoPlay=false target/alphabetfx-standalone-1.0.jar 
```

### Mobile and native app

TODO
