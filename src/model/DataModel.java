package model;


import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class DataModel {
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public IntegerProperty refreshProperty = new SimpleIntegerProperty(0);

    public void incrementRefresh() {
        refreshProperty.set(refreshProperty.get() + 1);
    }

    //<editor-fold desc="Connection">
    private Connection connection;

    public void initConnection() {
        if(connection == null)
            connection = new Connection();
        try {
            connection.connect();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnectionInstance() {
        return connection;
    }
    //</editor-fold>

    //<editor-fold desc="LastEmailId">
    private final AtomicInteger nextEmailId = new AtomicInteger(1);

    public void setNextEmailId(int value) {
        nextEmailId.set(value+1);
    }

    public int getNextEmaild() {
        return nextEmailId.get();
    }

    public void incrementNextEmailId() {
        nextEmailId.incrementAndGet();
    }
    //</editor-fold>

    //<editor-fold desc="inbox and sent lists in runtime memory">
    private ArrayList<Email> inbox = new ArrayList<>();
    private ArrayList<Email> sent = new ArrayList<>();

    public void setInbox(ArrayList<Email> inbox) {
        this.inbox = new ArrayList<>(inbox);
    }

    public void addToInbox(Email email) {
        inbox.add(email);
        Collections.sort(inbox);
    }

    public void rmFromInbox(Email email) {
        inbox.remove(email);
    }

    public ArrayList<Email> Inbox() {
        return inbox;
    }

    public void setSent(ArrayList<Email> sent) {
        this.sent = new ArrayList<>(sent);
    }

    public void addToSent(Email email) {
        sent.add(email);
        Collections.sort(inbox);
    }

    public void rmFromSent(Email email) {
        sent.remove(email);
    }

    public ArrayList<Email> Sent() {
        return sent;
    }
    //</editor-fold>

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

    //<editor-fold desc="Validation">
    public Boolean checkMailAccount(String username) {
        return Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
                .matcher(username).find();
    }


    public boolean checkEmail() {
        Boolean res = checkMailAccount(getCurrentEmail().getSender()); //sender
        for(String item : getCurrentEmail().getRecipient())
            res &= checkMailAccount(item);
        return res;
    }
    //</editor-fold>
}
