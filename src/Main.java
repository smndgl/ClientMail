import controller.EditorController;
import controller.MenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.DataModel;
import model.Message;
import task.Sync;

import java.io.IOException;
import java.util.Scanner;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        BorderPane root = new BorderPane();

        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("view/menu.fxml"));
        root.setLeft(menuLoader.load());
        MenuController menuController = menuLoader.getController();

        FXMLLoader listMailLoader = new FXMLLoader(getClass().getResource("view/editor.fxml"));
        root.setCenter(listMailLoader.load());
        EditorController listMailController = listMailLoader.getController();

        DataModel model = new DataModel();

        // TODO creare custom dialog
        String username;

        Boolean res = true;
        Sync sync = new Sync();
        do {
            do {
                System.out.println("Choose username: ");
                Scanner scanner = new Scanner(System.in);
                username = scanner.nextLine();
            } while (!model.checkMailAccount(username));

            model.setUsername(username);

            Scene scene = new Scene(root, 800, 600);
            primaryStage.setWidth(895);
            primaryStage.setHeight(565);
            primaryStage.setScene(scene);
            primaryStage.show();

            model.initConnection(); // TODO SI INLOOOOOOPPPPAAAA
            sync = new Sync(model);

            try {
                model.getConnectionInstance().login(model.getUsername());
                Message obj = model.getConnectionInstance().getMessage();
                res = (Boolean) obj.getContent();
                if (obj.getContent() instanceof Boolean) {
                    if (res) {
                        primaryStage.setTitle(username);
                        model.setUsername(username);

                        sync.fetchAll();
                        new Thread(sync).start();
                    }
                    else {
                        System.out.println("Error invalid username");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } // ask for inbox and sent
        } while (!res); //eheheh

        Sync finalSync = sync;
        primaryStage.setOnCloseRequest(windowEvent -> {
            finalSync.setInterrupted(true); //stop polling request for new emails
            try {
                if(model.getConnectionInstance().isConnected())
                    model.getConnectionInstance().logout(model.getUsername());
            } catch (IOException e) {
                System.err.println("Error on closing event: "+ e.getMessage());
            }
        });

        listMailController.initModel(model);
        menuController.initModel(model);

        primaryStage.setTitle(username);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
