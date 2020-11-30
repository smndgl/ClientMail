package task;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import model.Connection;
import model.DataModel;
import model.Email;
import model.Message;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;

public class Sync implements Runnable{
    private Connection connection;
    private final DataModel model;
    private Boolean interrupted = false;
    private Boolean reconnect = false;

    public Sync() {
        this.model = null;
        this.connection = null;
    }

    public Sync(DataModel model) {
        this.model = model;
        this.connection = model.getConnectionInstance();
    }

    public void setInterrupted(Boolean interrupted) {
        this.interrupted = interrupted;
    }

    public void fetchAll() {
        this.model.setInbox(fetchMailboxes("INBOX"));
        System.out.println("Inbox loaded");
        this.model.setSent(fetchMailboxes("SENT"));
        System.out.println("Sent loaded");
    }

    private ArrayList<Email> fetchMailboxes(String FILTER) {
        try {
            connection.fetchMailbox(FILTER);
            Message m = connection.getMessage();
            if(m.getContent() instanceof String) {
                Type type = new TypeToken<Collection<Email>>(){}.getType();
                return new Gson().fromJson((String)m.getContent(), type);
                //set atomic int in menucontroller
                //model.setLastEmailId(model.getMailList().get(model.getMailList().size()-1).getId());
            }
        }
        catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e) {
            System.out.println("connesione morta ihihihi");
            // TODO avvisare che la connesione è morta
        }
        return null; //should never been reached
    }

    @Override
    public void run() {
        while(!interrupted) {
            try {
                if(reconnect) {
                    Thread.sleep(4000);
                    System.out.println("try to reconnect");
                    model.getConnectionInstance().connect();
                    if(model.getConnectionInstance().isConnected()) { // prolemi di istanze
                        this.reconnect = false;
                        this.connection = model.getConnectionInstance();
                        connection.reconnect(model.getUsername()); //sequenza d'avvio
                        this.fetchAll();
                    }
                }
                else {
                    Message m = connection.getMessage();
                    Email email = (Email) m.getContent();
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(model.getUsername() + " - new email");
                        alert.setHeaderText(null);
                        alert.setContentText("You received a new mail");
                        alert.showAndWait();
                    });
                    if (email != null) {
                        model.addToInbox(email);
                        model.setListFilter("");
                    }
                }
            }
            catch (ClassCastException | InterruptedException e) {
                System.err.println(Thread.currentThread().getName()+" Error: "+ e.getMessage());
            }
            catch(NullPointerException e) {
                System.out.println("Socket chiusa");
                this.reconnect = true;
            } catch (IOException e) {
                System.out.println("CANNOT CONNECT : "+ e.getMessage());
            }
        }
    }
}
