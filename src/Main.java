import controller.EditorController;
import controller.MenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.DataModel;
import model.Message;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

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

        // model initialize
        DataModel model = new DataModel();

        // TODO creare custom dialog
        String username;
        do {
            System.out.println("Choose username: ");
            Scanner scanner = new Scanner(System.in);
            username = scanner.nextLine();
        } while (!model.checkEmail(username));
        model.setUsername(username);
        model.initConnection();
        try {
            model.getConnectionInstance().login(model.getUsername());
            Message obj = model.getConnectionInstance().getMessage();
            if(obj.getContent() instanceof Boolean) {
                if((Boolean) obj.getContent()) {
                    primaryStage.setTitle(username);
                    model.setUsername(username);
                }
                else {
                    System.err.println("Error invalid username");
                    System.exit(0);
                }
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        // TODO evento chiusura finestra mandare messaggio al server che rimuove dalla memoria virtuale la mailbox di questo utente.

        listMailController.initModel(model);
        menuController.initModel(model);

        primaryStage.setTitle(username);

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
