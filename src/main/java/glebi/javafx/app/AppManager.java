package glebi.javafx.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppManager extends Application {
    public void init(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Haulmont тестовое задание Bank App");
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("./fxml/main.fxml"));
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();
    }
}
