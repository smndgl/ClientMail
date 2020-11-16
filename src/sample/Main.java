package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        BorderPane root = new BorderPane();

        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("menu.fxml"));
        root.setLeft(menuLoader.load());
        MenuController menuController = menuLoader.getController();

        FXMLLoader listMailLoader = new FXMLLoader(getClass().getResource("editor.fxml"));
        root.setCenter(listMailLoader.load());
        EditorController listMailController = listMailLoader.getController();

        // model initialize
        DataModel model = new DataModel();
        listMailController.initModel(model);
        menuController.initModel(model);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setWidth(895);
        primaryStage.setHeight(565);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
