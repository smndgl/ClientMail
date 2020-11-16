package sample;

import com.sun.javafx.binding.StringFormatter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class EditorController {
    // componenti ui

    @FXML
    private Label lblSender;
    @FXML
    private TextField txtRecipients;
    @FXML
    private TextField txtSubject;
    @FXML
    private TextArea txtAreaMailText;
    @FXML
    private Label lblDebugId;

    @FXML
    private Button btnSend;

    @FXML
    private Button btnReply;
    @FXML
    private Button btnReplyAll;
    @FXML
    private Button btnForward;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnNew;

    public void initModel(DataModel model) {
        ChangeListener<Email> emailChangeListener = new ChangeListener<Email>() {
            @Override
            public void changed(ObservableValue<? extends Email> observableValue, Email oldEmail, Email newEmail) {
                if(newEmail != null) {
                    unBindEditor();
                    lblSender.textProperty().bind(newEmail.senderProperty());
                    txtSubject.textProperty().bind(newEmail.subjectProperty());
                    txtRecipients.textProperty().bind(new SimpleStringProperty(newEmail.getRecipient().toString().substring(1,newEmail.getRecipient().toString().length() - 1)));
                    txtAreaMailText.textProperty().bind(newEmail.textProperty());
                    lblDebugId.textProperty().bind(newEmail.idProperty().asString());
                }
            }
        };

        //<editor-fold desc="start settings">
            //appena avviato ho i controlli disabilitati, i bottoni anche perchè altrimenti non sanno che prendere
            //l'unico bottone che va è new
            //oppure delete
        controlsOff();
        buttonsOff();
        btnSend.setDisable(true);
        //</editor-fold>

        // <editor-fold desc="CurrentEmailChangeListener">
        model.currentEmailProperty().addListener(emailChangeListener);
        model.currentEmailProperty().addListener(observable -> {
            controlsOff();
            buttonsOn();
        });//set controls disabled
        //</editor-fold>

        //<editor-fold desc="Reply - ReplyAll - Forward">
        btnReply.setOnAction(actionEvent -> {
            model.setCurrentEmail(new Email(
                    model.getLastEmaild(),
                    model.getCurrentAccount().getAccountName(),
                    new ArrayList<String>(Arrays.asList(model.getCurrentEmail().getSender().split(" "))),
                    "RE: "+ model.getCurrentEmail().getSubject(),
                    "",
                    new Date()
            ));
            controlsOn();
            buttonsOff();
        });

        btnReplyAll.setOnAction(actionEvent -> {
            ArrayList<String> newRecipients = new ArrayList<>();
            newRecipients.add(model.getCurrentEmail().getSender());
            for (String item : model.getCurrentEmail().getRecipient()) {
                if(!(item.equals(model.getCurrentAccount().getAccountName())))
                    newRecipients.add(item);
            };
            model.setCurrentEmail(new Email(
                    model.getLastEmaild(),
                    model.getCurrentAccount().getAccountName(),
                    newRecipients,
                    "RE: "+ model.getCurrentEmail().getSubject(),
                    "",
                    new Date()
            ));
            controlsOn();
            buttonsOff();
        });

        btnForward.setOnAction(actionEvent -> {
            model.setCurrentEmail(new Email(
                    model.getLastEmaild(),
                    model.getCurrentAccount().getAccountName(),
                    new ArrayList<String>(),
                    "FW: "+ model.getCurrentEmail().getSubject(),
                    model.getCurrentEmail().getText(),
                    new Date()
            ));
            controlsOn();
            buttonsOff();
        });
        //</editor-fold>

        //<editor-fold desc="New - Delete - Send">
        btnNew.setOnAction(actionEvent -> {
            model.setCurrentEmail(new Email(
                    model.getLastEmaild(),
                    model.getCurrentAccount().getAccountName(),
                    new ArrayList<String>(),
                    "",
                    "",
                    new Date()
            ));
            controlsOn();
            buttonsOff();
        });

        //sure about it
        btnDelete.setOnAction(actionEvent -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("");
            alert.setContentText("Are you sure?");
            ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(okButton, noButton, cancelButton);
            alert.showAndWait().ifPresent(type -> {
                if (type == ButtonType.OK) {
                    // TODO
                    // richiamo il model per eliminare la mail --> sposto mail da inbox/sent a bin
                    // eventualmente setto la mailbox a bin ?
                }
            });
        });

        //</editor-fold>
    }

    public void controlsOff() {
        txtRecipients.setDisable(true);
        txtRecipients.setOpacity(1);
        txtAreaMailText.setDisable(true);
        txtAreaMailText.setOpacity(1);
        txtSubject.setDisable(true);
        txtSubject.setOpacity(1);
    }

    public void controlsOn() {
        txtRecipients.setDisable(false);
        txtAreaMailText.setDisable(false);
        txtSubject.setDisable(false);
    }

    public void buttonsOff() {
        btnReply.setDisable(true);
        btnReplyAll.setDisable(true);
        btnForward.setDisable(true);
        btnDelete.setDisable(true);
        btnSend.setDisable(false);
    }

    public void buttonsOn() {
        btnReply.setDisable(false);
        btnReplyAll.setDisable(false);
        btnForward.setDisable(false);
        btnDelete.setDisable(false);
        btnSend.setDisable(true);
    }

    public void unBindEditor() {
        lblSender.textProperty().unbind();

        txtSubject.textProperty().unbind();

        txtRecipients.textProperty().unbind();

        txtAreaMailText.textProperty().unbind();

        lblDebugId.textProperty().unbind();
    }
}
