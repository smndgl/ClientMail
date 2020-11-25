package model;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class DataModel {
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    //<editor-fold desc="Connection">
    private Connection connection;

    public void initConnection() {
        if(connection != null )
            throw new IllegalStateException("Connection can only be initialized once");
        connection = new Connection();
        try {
            connection.connect();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnectionInstance() {
        if(connection.isConnected())
            return connection;
        else
            return null;
    }
    //</editor-fold>

    //<editor-fold desc="LastEmailId">
    private final AtomicInteger lastEmailId = new AtomicInteger(1);

    public void setLastEmailId(int value) {
        lastEmailId.addAndGet(value);
    }

    public int getLastEmaild() {
        return lastEmailId.get();
    }

    public void incrementLastEmailId() {
        lastEmailId.incrementAndGet();
    }
    //</editor-fold>

    public Boolean checkEmail(String username) {
        return Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
                .matcher(username).find();
    }

    //<editor-fold desc="List filter for showing the choosen mailbox">
    private final StringProperty listFilter = new SimpleStringProperty();

    public StringProperty listFilterProperty() {
        return listFilter;
    }

    public String getListFilter() {
        return listFilterProperty().get();
    }

    public void setListFilter(String filter) {
        listFilterProperty().set(filter);
    }
    //</editor-fold>

    //<editor-fold desc="ListMail management conditioned by FilterList">
    /*
     *  Properties
     */
    private ObservableList<Email> mailList = FXCollections.observableArrayList(email ->
            new Observable[] {
                    new SimpleIntegerProperty(email.getId()),
                    new SimpleStringProperty(email.getSender()),
                    new SimpleListProperty<>(FXCollections.observableArrayList(email.getRecipient())),
                    new SimpleStringProperty(email.getSubject()),
                    new SimpleStringProperty(email.getText()),
                    new SimpleObjectProperty<>(email.getMailingDate())
    });

    public void setMailList(ArrayList<Email> emails) {
        this.mailList = FXCollections.observableList(emails);
    }

    public ObservableList<Email> getMailList() {
        return mailList;
    }

    public void addToMailList(Email email) {
        mailList.add(email);
    }

    public final ObjectProperty<Email> currentEmail = new SimpleObjectProperty<>();

    public ObjectProperty<Email> currentEmailProperty() {
        return currentEmail;
    }
    public Email getCurrentEmail() {
        return currentEmailProperty().get();
    }
    public void setCurrentEmail(Email email) {
        currentEmailProperty().set(email);
    }
    //</editor-fold>

    //<editor-fold desc="">

    //</editor-fold>
}
