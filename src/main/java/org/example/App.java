package org.example;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


/**
 * JavaFX App
 */
public class App extends Application {

    private static final int SCENE_SIZE_X = 1920;
    private static final int SCENE_SIZE_Y = 1080;
    //main timeline
    private Timeline timeline;
    private AnimationTimer timer;
    private Integer frame = 0;

    @Override
    public void start(Stage stage) {
        Group root = new Group();
        Scene scene = new Scene(root, SCENE_SIZE_X, SCENE_SIZE_Y, Color.BLACK);
        stage.setTitle("What Am I doing with my life?");
        stage.setScene(scene);
        stage.show();

        final Text text = new Text(frame.toString());
        text.setStroke(Color.WHITE);
        //You can add a specific action when each frame is started.
        timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                text.setText(frame.toString());
                frame++;
            }
        };

        EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                var circle = new Circle(e.getSceneX(), e.getSceneY(), 10, Color.WHITE);
                circle.setFill(Color.WHITE);
                root.getChildren().add(circle);
                //Creating the translation transformation
                fallAnimation(e.getSceneY(), circle);
            }

            private void fallAnimation(double high, Circle circle) {
                TranslateTransition translateTransition = new TranslateTransition();
                //Setting the X,Y,Z coordinates to apply the translation
                translateTransition.setDuration(Duration.millis(getFallMilliSecondsCalculation(high)));
                //Setting the node for the transition
                translateTransition.setNode(circle);
                //Setting the value of the transition along the y axis.
                translateTransition.setByY(SCENE_SIZE_Y - high);
                //Setting the cycle count for the transition
                translateTransition.setCycleCount(1);
                //Setting auto reverse value to false
                translateTransition.setAutoReverse(false);
                //Playing the animation
                translateTransition.play();
            }
        };

        EventHandler<KeyEvent> pumpEventClick = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                root.getChildren().stream().forEach(node -> {
                    double random = Math.random() * SCENE_SIZE_Y;
                    //Creating the translation transformation
                    TranslateTransition translateTransition = new TranslateTransition();
                    //Setting the X,Y,Z coordinates to apply the translation
                    translateTransition.setDuration(Duration.millis(getFallMilliSecondsCalculation(random)));
                    //Setting the node for the transition
                    translateTransition.setNode(node);
                    //Setting the value of the transition along the y axis.
                    translateTransition.setByY(-random);
                    //Setting the cycle count for the transition
                    translateTransition.setCycleCount(2);
                    //Setting auto reverse value to false
                    translateTransition.setAutoReverse(true);
                    //Playing the animation
                    translateTransition.play();
                    EventHandler onFinished = new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent t) {
                            node.setTranslateX(java.lang.Math.random() * 200 - 100);
                        }
                    };
                });

            }
        };
        scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, eventHandler);
        scene.addEventHandler(KeyEvent.KEY_TYPED, pumpEventClick);

    }


    public static void main(String[] args) {
        launch();
    }


    private int getFallMilliSecondsCalculation(double high) {
        return (int) Math.sqrt(2 * high * 100);
    }

    /*
      Timeline timeline = new Timeline();
        for (Node circle: root.getChildren()) {
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO, // set start position at 0
                            new KeyValue(circle.translateXProperty(), Math.random() * 1920),
                            new KeyValue(circle.translateYProperty(), Math.random() * 1080)
                    ),
                    new KeyFrame(new Duration(40000), // set end position at 40s
                            new KeyValue(circle.translateXProperty(), Math.random() * 1920),
                            new KeyValue(circle.translateYProperty(), Math.random() * 1080)
                    )
            );
        }
timeline.play();
     */
}