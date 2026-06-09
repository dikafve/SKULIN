package main;

import main.database.DatabaseHelper;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        DatabaseHelper.initDatabase();

        Label label = new Label("Skulin - Smart Kuliah Utility for Learning & Integrated Scheduling");
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Skulin App");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}