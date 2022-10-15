package org.example;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;


/**
 * JavaFX App
 */
public class App extends Application {
    //physics calculated with 10px = 1m
    private static final int SCENE_SIZE_X = 1000;
    private static final int SCENE_SIZE_Y = 1000;
    //main timeline
    private Timeline timeline;
    private AnimationTimer eventTimer;
    private Integer frame = 0;
    private Double fps = 0d;
    private Double secondFromTheLastFrame;

    @Override
    public void start(Stage stage) {
        Group root = new Group();
        Scene scene = new Scene(root, SCENE_SIZE_X, SCENE_SIZE_Y, Color.BLACK);
        stage.setTitle("What Am I doing with my life?");
        stage.setScene(scene);
        stage.show();
        addFrameCounter(root);
        eventTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                root.getChildren().stream()
                        .filter(c -> c instanceof CirclePhysics)
                        .map(c -> (CirclePhysics) c)
                        .forEach(c -> {
                            c.refreshPosition(secondFromTheLastFrame);
                        });
            }
        };
        eventTimer.start();
        EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                CirclePhysics circle = new CirclePhysics(e.getSceneX(), e.getSceneY(), 10, Color.WHITE);
                root.getChildren().add(circle);
            }
        };

        EventHandler<KeyEvent> pumpEventClick = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {

            }
        };
        scene.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);
        scene.addEventHandler(KeyEvent.KEY_TYPED, pumpEventClick);

    }

    private void addFrameCounter(Group root) {
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        Text frameText = new Text(10, 20, frame.toString());
        frameText.setStroke(Color.WHITE);
        Text fpsText = new Text(10, 40, "0");
        fpsText.setStroke(Color.WHITE);
        //You can add a specific action when each frame is started.
        AnimationTimer frameTimer = new AnimationTimer() {
            long delta;
            long lastFrameTime;

            @Override
            public void handle(long now) {
                delta = now - lastFrameTime;
                lastFrameTime = now;
                refreshFrameStatus();
                setFrameStatusTexts();
            }

            public double getFrameRateHertz() {
                double frameRate = 1d / delta;
                return frameRate * 1e9;
            }

            private void refreshFrameStatus() {
                frame++;
                fps = getFrameRateHertz();
                secondFromTheLastFrame = 1d / fps;
            }

            private void setFrameStatusTexts() {
                frameText.setText(frame.toString());
                fpsText.setText(Double.toString(fps));
            }
        };
        frameTimer.start();
        root.getChildren().add(frameText);
        root.getChildren().add(fpsText);
    }


    public static void main(String[] args) {
        launch();
    }

    private class CirclePhysics extends Circle {
        private double accelerationX = 0d; // m/s²
        private double velocityX = 0d; // m/s
        private double accelerationY = 0d; // m/s²
        private double velocityY = 0d; // m/s
        private double mass = 1d; //kg

        CirclePhysics(double x, double y, double size, Paint color) {
            super(x, y, size, color);
        }

        void applyForceY(double force) {
            accelerationY = force / mass;
        }

        void applyForceX(double force) {
            accelerationX = force / mass;
        }

        void refreshPosition(double timePassed) {
            super.setTranslateX(getTranslateX() + calculateTranslateX(timePassed));
            super.setTranslateY(getTranslateY() + calculateTranslateY(timePassed));
        }

        private double calculateTranslateX(double timePassed) {
            return (velocityX * timePassed) + ((accelerationX * timePassed * timePassed) / 2);
        }

        private double calculateTranslateY(double timePassed) {
            return (velocityY * timePassed) + ((accelerationY * timePassed * timePassed) / 2);
        }

        public void setAccelerationX(double accelerationX) {
            this.accelerationX = accelerationX;
        }

        public void setVelocityX(double velocityX) {
            this.velocityX = velocityX;
        }

        public void setAccelerationY(double accelerationY) {
            this.accelerationY = accelerationY;
        }

        public void setVelocityY(double velocityY) {
            this.velocityY = velocityY;
        }

        public void setMass(double mass) {
            if (mass == 0d) {
                mass = 1d;
            }
            this.mass = mass;
        }
    }

}