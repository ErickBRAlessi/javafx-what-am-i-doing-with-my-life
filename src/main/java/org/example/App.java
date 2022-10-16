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
    private static final int SCENE_SIZE_X = 1920;
    private static final int SCENE_SIZE_Y = 1080;
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
                            c.refresh(secondFromTheLastFrame);
                        });
            }
        };
        eventTimer.start();
        EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                CirclePhysics circle = new CirclePhysics(e.getSceneX(), e.getSceneY(), 10, Color.WHITE);
                circle.setGravityOn(true);
                root.getChildren().add(circle);
            }
        };

        EventHandler<KeyEvent> pumpEventClick = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                root.getChildren().stream()
                        .filter(f -> f instanceof CirclePhysics)
                        .map(c -> (CirclePhysics) c)
                        .forEach(c -> {
                            c.applyForceX(randomNegativeOrPositive() * 1000 * Math.random());
                            c.applyForceY(randomNegativeOrPositive() * 1000 * Math.random());
                        });
            }
        };
        scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, eventHandler);
        scene.addEventHandler(KeyEvent.KEY_TYPED, pumpEventClick);

    }

    private int randomNegativeOrPositive() {
        if (Math.floor(Math.random() * 100) % 2 == 0) {
            return 1;
        }
        return -1;
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
        private boolean gravity = false;
        private double bounce = 0d;
        private static final double GRAVITY_CONSTANT = 98.0d; // m/s²

        CirclePhysics(double x, double y, double size, Paint color) {
            super(x, y, size, color);
        }

        void applyForceY(double force) {
            accelerationY = accelerationY + (force / mass);
        }

        void applyForceX(double force) {
            accelerationX = accelerationX + (force / mass);
        }

        void refresh(double timePassed) {
            if (gravity) {
                velocityY = velocityY + (accelerationY + GRAVITY_CONSTANT) * timePassed;
            } else {
                velocityY = velocityY + accelerationY * timePassed;
            }
            velocityX = velocityX + accelerationX * timePassed;

            refreshPosition(timePassed);
            // logStatus();
        }

        private void refreshPosition(double timePassed) {
            double translatedX = calculateTranslateX(timePassed);
            double translatedY = calculateTranslateY(timePassed);

            if (itWillBeInSceneXLimits(translatedX)) {
                super.setCenterX(getCenterX() + translatedX);
            } else {
                this.velocityX = 0;
                this.accelerationX = 0;
            }

            if (itWillBeInSceneYLimits(translatedY)) {
                super.setCenterY(getCenterY() + translatedY);
            } else {
                this.velocityY = 0;
                this.accelerationY = 0;
            }

        }

        private boolean itWillBeInSceneYLimits(double translatedY) {
            if (getCenterY() - getRadius() + translatedY >= 0 && getCenterY() + getRadius() + translatedY <= SCENE_SIZE_Y) {
                return true;
            }
            return false;
        }

        private boolean itWillBeInSceneXLimits(double translatedX) {
            if (getCenterX() - getRadius() + translatedX >= 0 && getCenterX() + getRadius() + translatedX <= SCENE_SIZE_X) {
                return true;
            }
            return false;
        }

        private double calculateTranslateX(double timePassed) {
            return (velocityX * timePassed);
        }

        private double calculateTranslateY(double timePassed) {
            return (velocityY * timePassed);
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

        public void setGravityOn(boolean gravity) {
            this.gravity = gravity;
        }

        private void logStatus() {
            System.out.println("Position     X: " + this.getTranslateX() + " Position     Y: " + this.getTranslateY());
            System.out.println("Velocity     X: " + this.velocityX + " Velocity     Y: " + this.velocityY);
            System.out.println("Acceleration X: " + this.getTranslateX() + " Acceleration Y: " + this.getTranslateY());
        }
    }

}