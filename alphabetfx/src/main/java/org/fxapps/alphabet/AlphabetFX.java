package org.fxapps.alphabet;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

public class AlphabetFX extends Application {

    private static final String ALPHABET_IMAGES_DIR = "/images/alphabet/";
    private static final String DETAILS_IMAGES_DIR = "/images/details";

    private static final String ALPHABET_SOUND_DIR = "/sounds/alphabet/";

    private static final Character INITIAL_CHAR = 65; // A
    private static final Character END_CHAR = 90; // Z

    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;

    private static final int LETTER_IMG_WIDTH = WIDTH - WIDTH / 5;
    private static final int LETTER_IMG_HEIGHT = HEIGHT - HEIGHT / 6;

    private static final int LETTER_IMG_MIN_WIDTH = LETTER_IMG_WIDTH / 5;
    private static final int LETTER_IMG_MIN_HEIGHT = LETTER_IMG_HEIGHT / 5;

    private static final int DETAILS_IMG_WIDTH = LETTER_IMG_HEIGHT - LETTER_IMG_HEIGHT / 4;
    private static final int DETAILS_IMG_HEIGHT = LETTER_IMG_HEIGHT - LETTER_IMG_HEIGHT / 4;

    private static final int letterImgPosX = -LETTER_IMG_MIN_WIDTH;
    private static final int letterImgPosY = HEIGHT / 2 - LETTER_IMG_HEIGHT / 2 + 50;

    private static final int detailsImgPosX = 0;
    private static final int detailsImgPosY = HEIGHT / 2 - DETAILS_IMG_HEIGHT / 2 + 50;

    private static final String IMG_EXT = ".png";

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private ImageView letterImg;
    private ImageView detailsImg;
    private Label lblDetails;
    private VBox detailsImgParent;

    private Character cursor = INITIAL_CHAR;
    private Map<Character, List<Pair<String, String>>> detailsImages;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        var scene = new Scene(buildApp(), 335, 600);
        stage.setScene(scene);
        stage.show();
    }

    public Pane buildApp() {
        detailsImages = bulkDetailsImages();

        letterImg = new ImageView();
        detailsImg = new ImageView();
        lblDetails = new Label();
        detailsImgParent = new VBox(10, detailsImg, lblDetails);

        detailsImg.setEffect(new DropShadow());
        lblDetails.setEffect(new DropShadow(10, Color.BEIGE));

        lblDetails.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 40));

        detailsImgParent.setLayoutX(detailsImgPosX);
        detailsImgParent.setLayoutY(detailsImgPosY);
        detailsImgParent.setAlignment(Pos.CENTER);

        letterImg.setOnMouseClicked(e -> {
            letterImg.setDisable(true);
            var letterAnimation = new Timeline(new KeyFrame(Duration.millis(0),
                                                            new KeyValue(letterImg.layoutXProperty(), letterImgPosX),
                                                            new KeyValue(letterImg.layoutYProperty(), letterImgPosY),
                                                            new KeyValue(letterImg.fitWidthProperty(), LETTER_IMG_WIDTH),
                                                            new KeyValue(letterImg.fitHeightProperty(), LETTER_IMG_HEIGHT)),
                                               new KeyFrame(Duration.millis(1000),
                                                            new KeyValue(letterImg.layoutXProperty(), 0),
                                                            new KeyValue(letterImg.layoutYProperty(), 0),
                                                            new KeyValue(letterImg.fitWidthProperty(), LETTER_IMG_MIN_WIDTH),
                                                            new KeyValue(letterImg.fitHeightProperty(), LETTER_IMG_MIN_HEIGHT)),
                                               new KeyFrame(Duration.millis(800),
                                                            new KeyValue(detailsImgParent.opacityProperty(), 0)),
                                               new KeyFrame(Duration.millis(2000),
                                                            new KeyValue(detailsImgParent.opacityProperty(), 1.0)));
            letterAnimation.playFromStart();
            letterAnimation.setOnFinished(ee -> detailsImgParent.setDisable(false));
        });

        detailsImgParent.setOnMouseClicked(e -> next());

        loadRandomizedFor(cursor);

        var root = new Pane(letterImg, detailsImgParent);
        root.setTranslateX(0);
        root.setTranslateY(0);
        root.setMinSize(WIDTH, HEIGHT);
        root.setStyle("-fx-background-color: paleturquoise");

        return root;
    }

    private void next() {
        if (cursor == END_CHAR) {
            cursor = INITIAL_CHAR;
        } else {
            cursor++;
        }
        loadRandomizedFor(cursor);
    }

    private void loadRandomizedFor(Character ch) {
        var candidates = detailsImages.get(ch);
        int pos = RANDOM.nextInt(candidates.size());
        startOver("" + ch, candidates.get(pos));
    }

    private void startOver(String letter, Pair<String, String> details) {
        // letter image setup
        var letterStream = AlphabetFX.class.getResourceAsStream(ALPHABET_IMAGES_DIR + "/" + letter + ".png");
        letterImg.setImage(new Image(letterStream));
        letterImg.setFitWidth(LETTER_IMG_WIDTH);
        letterImg.setFitHeight(LETTER_IMG_HEIGHT);
        letterImg.setSmooth(true);
        letterImg.setLayoutX(letterImgPosX);
        letterImg.setLayoutY(letterImgPosY);
        letterImg.setDisable(false);
        letterImg.setOpacity(0.7);

        // details img setup
        detailsImg.setImage(new Image(AlphabetFX.class.getResourceAsStream(details.getValue())));
        detailsImg.setFitWidth(DETAILS_IMG_WIDTH);
        detailsImg.setFitHeight(DETAILS_IMG_HEIGHT);
        detailsImg.setPreserveRatio(true);
        detailsImg.setSmooth(true);

        // details Img parent
        detailsImgParent.setOpacity(0);
        detailsImgParent.setDisable(true);
        lblDetails.setText(details.getKey());

        //        new MediaPlayer(new Media(soundPath)).play();
    }

    private Map<Character, List<Pair<String, String>>> bulkDetailsImages() {
        try {
            System.out.println("DETAILS: " + AlphabetFX.class.getResource("/details"));
            var detailImages = AlphabetFX.class.getResource(DETAILS_IMAGES_DIR);
            return Arrays.stream(new File(detailImages.getFile()).listFiles())
                   .map(f -> {
                       var name = f.getName().replaceAll(IMG_EXT, "");
                       var path = DETAILS_IMAGES_DIR + "/" + f.getName(); 
                       return new Pair<>(name, path);
                   }).collect(Collectors.groupingBy(p -> p.getKey().toUpperCase().toCharArray()[0]));
//            var resource = AlphabetApp.class.getResource("/details");
//            return Files.lines(Paths.get(resource.toURI()))
//                        .map(name -> {
//                            String path = DETAILS_IMAGES_DIR + name + IMG_EXT;
//                            return new Pair<>(name, path);
//                        }).collect(Collectors.groupingBy(p -> p.getKey().substring(0, 1).toCharArray()[0]));
        } catch (Exception e) {
            throw new RuntimeException("[DEBUG] Error reading FILE", e);
        }
    }

}