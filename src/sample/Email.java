package sample;


import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Email implements Serializable, Comparable<Email> {
    final private IntegerProperty id = new SimpleIntegerProperty(); //controlli da fare su id per unicità
    final private StringProperty sender = new SimpleStringProperty();
    public ListProperty<String> recipient = new SimpleListProperty<>();
    final private StringProperty subject = new SimpleStringProperty();
    final private StringProperty text = new SimpleStringProperty();
    final private ObjectProperty<Date> mailingDate = new SimpleObjectProperty<>();

    //<editor-fold desc="id">
    public IntegerProperty idProperty() {
        return id;
    }

    public int getId() {
        return idProperty().get();
    }

    public void setId(int id) {
        idProperty().set(id);
    }
    //</editor-fold>

    //<editor-fold desc="sender">
    public StringProperty senderProperty() {
        return sender;
    }

    public String getSender() {
        return senderProperty().get();
    }

    public void setSender(String senderAccount) {
        senderProperty().set(senderAccount);
    }
    //</editor-fold>

    //<editor-fold desc="recipient">
    public ListProperty<String> recipientProperty() {
        return recipient;
    }

    public ObservableList<String> getRecipient() {
        return recipientProperty().get();
    }

    public void setRecipient(ObservableList<String> recipient) {
        recipientProperty().setAll(recipient);
    }
    //</editor-fold>

    //<editor-fold desc="subject">
    public StringProperty subjectProperty() {
        return subject;
    }

    public String getSubject() {
        return subjectProperty().get();
    }

    public void setSubject(String subject) {
        subjectProperty().set(subject);
    }
    //</editor-fold>

    //<editor-fold desc="text">
    public StringProperty textProperty() {
        return text;
    }

    public String getText() {
        return textProperty().get();
    }

    public void setText(String text) {
        textProperty().set(text);
    }
    //</editor-fold>

    //<editor-fold desc="malingDate">
    public ObjectProperty<Date> mailingDateProperty() {
        return mailingDate;
    }

    public Date getMailingDate() {
        return mailingDateProperty().get();
    }

    public void setMailingDate(Date mailingDate) {
        mailingDateProperty().set(mailingDate);
    }
    //</editor-fold>

    public Email(int id, String sender, List<String> recipient, String subject, String text, Date mailingDate) {
        setId(id);
        setSender(sender);
        this.recipient = new SimpleListProperty<>(FXCollections.observableArrayList(recipient));
        setSubject(subject);
        setText(text);
        setMailingDate(mailingDate);
    }

    @Override
    public int compareTo(Email o) {
        return o.mailingDate.getValue().compareTo(mailingDate.getValue());
    }
}
