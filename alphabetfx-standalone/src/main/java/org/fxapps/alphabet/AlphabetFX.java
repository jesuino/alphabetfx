package org.fxapps.alphabet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
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
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.Pair;

import static java.util.stream.Collectors.groupingBy;

public class AlphabetFX extends Application {

    // System properties
    private static final String NO_REPEAT_PROP = "noRepeat";
    private static final String AUTO_PLAY_PROP = "autoPlay";
    private static final String COLLECTIONS_PROP = "collections";
    private static final String BG_COLOR_PROP = "bgColor";
    private static final String DECORATED_PROP = "decorated";

    // Base Directories
    private static final String NAMES_DIR = "/names/";
    private static final String ALPHABET_IMAGES_DIR = "/images/alphabet/";
    private static final String DETAILS_IMAGES_DIR = "/images/details/";
    private static final String ALPHABET_SOUND_DIR = "/sounds/alphabet/";

    private static final Character INITIAL_CHAR = 65; // A
    private static final Character END_CHAR = 90; // Z

    private static final int WIDTH = 700;
    private static final int HEIGHT = 400;

    private static final int LETTER_IMG_WIDTH = WIDTH - WIDTH / 5;
    private static final int LETTER_IMG_HEIGHT = HEIGHT - HEIGHT / 6;

    private static final int LETTER_IMG_MIN_WIDTH = LETTER_IMG_WIDTH / 5;
    private static final int LETTER_IMG_MIN_HEIGHT = LETTER_IMG_HEIGHT / 5;

    private static final int DETAILS_IMG_WIDTH = LETTER_IMG_HEIGHT - LETTER_IMG_HEIGHT / 4;
    private static final int DETAILS_IMG_HEIGHT = LETTER_IMG_HEIGHT - LETTER_IMG_HEIGHT / 4;

    private static final int letterImgPosX = WIDTH / 2 - LETTER_IMG_WIDTH / 2;
    private static final int letterImgPosY = HEIGHT / 2 - LETTER_IMG_HEIGHT / 2;

    private static final int detailsImgPosX = WIDTH / 2 - DETAILS_IMG_WIDTH / 2;
    private static final int detailsImgPosY = HEIGHT / 2 - DETAILS_IMG_HEIGHT / 2;

    private static final String IMG_EXT = ".png";
    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private ImageView letterImg;
    private ImageView detailsImg;
    private Label lblDetails;
    private VBox detailsImgParent;

    private Character cursor = INITIAL_CHAR;
    private Map<Character, List<Pair<String, String>>> detailsImages;
    private FadeTransition letterFade;
    private Timeline letterAnimation;
    private Pane root;
    private boolean autoPlay;
    private boolean noRepeat;

    List<Animation> allAnimations = new ArrayList<>();
    private Label lblEnd;
    private String namesFile = "br_presidents";
    private String bgColor;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {

        var decorated = Boolean.parseBoolean(System.getProperty(DECORATED_PROP, Boolean.TRUE.toString()));
        bgColor = System.getProperty(BG_COLOR_PROP, Color.PALETURQUOISE.toString());
        namesFile = System.getProperty(COLLECTIONS_PROP, "pokemons");
        autoPlay = Boolean.parseBoolean(System.getProperty(AUTO_PLAY_PROP, Boolean.TRUE.toString()));
        noRepeat = Boolean.parseBoolean(System.getProperty(NO_REPEAT_PROP, Boolean.TRUE.toString()));

        System.out.println("COLLECTIONS: " + namesFile);
        System.out.println("AUTO PLAY: " + autoPlay);
        System.out.println("NO REPEAT: " + noRepeat);
        System.out.println("DECORATED " + decorated);
        System.out.println("BG COLOR " + bgColor);

        if (!decorated) {
            stage.initStyle(StageStyle.UNDECORATED);
        }
        var scene = new Scene(buildApp(), WIDTH, HEIGHT);
        stage.setScene(scene);
        scene.setFill(Color.valueOf(bgColor));
        stage.show();
        //        stage.setFullScreen(true);
    }

    public Pane buildApp() {
        lblEnd = new Label("The End!");
        root = new Pane();
        detailsImages = bulkDetailsImages();

        letterImg = new ImageView();
        detailsImg = new ImageView();
        lblDetails = new Label();

        detailsImg.setEffect(new DropShadow());
        lblDetails.setEffect(new DropShadow(10, Color.BEIGE));
        lblDetails.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 40));

        lblEnd.setTranslateY(HEIGHT / 2 - 50);
        lblEnd.setTranslateX(100);
        lblEnd.setVisible(false);
        lblEnd.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 80));

        detailsImgParent = new VBox(detailsImg, lblDetails);
        detailsImgParent.setMinWidth(WIDTH);
        //        detailsImgParent.setLayoutX(detailsImgPosX);
        detailsImgParent.setLayoutY(detailsImgPosY);
        detailsImgParent.setAlignment(Pos.CENTER);

        root.getChildren().addAll(letterImg, detailsImgParent, lblEnd);

        var autoFadeTransition = new FadeTransition(Duration.millis(1000));
        autoFadeTransition.setFromValue(1);
        autoFadeTransition.setToValue(0);
        autoFadeTransition.setNode(root);
        autoFadeTransition.setCycleCount(1);
        autoFadeTransition.setOnFinished(e -> next());

        var autoAfterPokemon = new Timeline(new KeyFrame(Duration.seconds(2)));
        autoAfterPokemon.setOnFinished(e -> autoFadeTransition.playFromStart());

        var autoAfterLetter = new Timeline(new KeyFrame(Duration.seconds(1)));
        autoAfterLetter.setOnFinished(e -> letterAnimation.playFromStart());

        letterFade = new FadeTransition(Duration.millis(1000));
        letterFade.setFromValue(0);
        letterFade.setToValue(0.7);
        letterFade.setNode(letterImg);

        letterFade.setOnFinished(e -> {
            if (autoPlay) {
                autoAfterLetter.playFromStart();
            } else {
                letterImg.setDisable(false);
            }
        });

        letterAnimation = new Timeline(new KeyFrame(Duration.millis(0),
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

        letterAnimation.setOnFinished(ee -> {
            detailsImgParent.setDisable(false);
            if (autoPlay) {
                autoAfterPokemon.playFromStart();
            }
        });

        letterImg.setOnMouseClicked(e -> {
            letterImg.setDisable(true);
            letterAnimation.playFromStart();
        });

        if (!autoPlay) {
            detailsImgParent.setOnMouseClicked(e -> next());
        } else {
            root.setOnMouseClicked(e -> reset());
        }

        root.setTranslateX(0);
        root.setTranslateY(0);
        root.setMinSize(WIDTH, HEIGHT);
        root.setStyle("-fx-background-color: " + bgColor);

        loadRandomizedFor(cursor);

        allAnimations.addAll(List.of(letterAnimation, letterFade, autoAfterPokemon, autoFadeTransition, autoAfterLetter));

        return root;
    }

    private void reset() {
        lblEnd.setVisible(false);
        bulkDetailsImages();
        cursor = INITIAL_CHAR;
        loadRandomizedFor(cursor);
    }

    private void next() {
        advanceCursor();
        // advance to the next valid char
        int rounds = END_CHAR - INITIAL_CHAR;
        int i = 0;
        while (detailsImages.getOrDefault(cursor, Collections.emptyList()).isEmpty()) {
            advanceCursor();
            if (i == rounds) {
                stopAnimations();
                lblEnd.setVisible(true);
                return;
            }
            i++;
        }
        loadRandomizedFor(cursor);
    }

    private void advanceCursor() {
        if (cursor == END_CHAR) {
            cursor = INITIAL_CHAR;
        } else {
            cursor++;
        }
    }

    private void loadRandomizedFor(Character ch) {
        var candidates = detailsImages.get(ch);
        if (candidates.isEmpty()) {
            return;
        }
        int pos = RANDOM.nextInt(candidates.size());
        Pair<String, String> details = candidates.get(pos);
        if (noRepeat) {
            candidates.remove(pos);
        }
        startOver("" + ch, details);
    }

    private void startOver(String letter, Pair<String, String> details) {
        stopAnimations();
        // letter image setup
        var letterStream = AlphabetFX.class.getResourceAsStream(ALPHABET_IMAGES_DIR + letter + ".png");
        letterImg.setImage(new Image(letterStream));
        letterImg.setFitWidth(LETTER_IMG_WIDTH);
        letterImg.setFitHeight(LETTER_IMG_HEIGHT);
        letterImg.setSmooth(true);
        letterImg.setLayoutX(letterImgPosX);
        letterImg.setLayoutY(letterImgPosY);
        letterImg.setDisable(true);
        letterImg.setOpacity(0);

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

        root.setOpacity(1);
        letterFade.play();

        //        new MediaPlayer(new Media(soundPath)).play();
    }

    private void stopAnimations() {
        allAnimations.forEach(a -> a.stop());
    }

    private Map<Character, List<Pair<String, String>>> bulkDetailsImages() {
        return loadDetails().stream()
                            .map(name -> new Pair<>(name, DETAILS_IMAGES_DIR + namesFile + "/" + name + IMG_EXT))
                            .collect(groupingBy(p -> p.getKey().substring(0, 1).toCharArray()[0]));

    }

    private List<String> loadDetails() {
        var detailsIs = AlphabetFX.class.getResourceAsStream(NAMES_DIR + namesFile);
        var lines = new ArrayList<String>();
        var r = new BufferedReader(new InputStreamReader(detailsIs));
        try {
            for (String line; (line = r.readLine()) != null;) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Not able to read input stream lines");
        }
        return lines;
    }

}