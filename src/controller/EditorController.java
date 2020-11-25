package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.DataModel;
import model.Email;

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

    private DataModel model;

    public void initModel(DataModel model) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model;
        //<editor-fold desc="start settings">
            //appena avviato ho i controlli disabilitati, i bottoni anche perchè altrimenti non sanno che prendere
            //l'unico bottone che va è new
            //oppure delete
        controlsOff();
        buttonsOff();
        btnSend.setDisable(true);
        //</editor-fold>

        // <editor-fold desc="CurrentEmailChangeListener">
        model.currentEmailProperty().addListener((obs, oldEmail, newEmail) -> {
            if(newEmail == null) {
                unBindEditor();
            }
            else {
                lblSender.setText(newEmail.getSender());
                txtSubject.setText(newEmail.getSubject());
                txtRecipients.setText(newEmail.getRecipientAsString());
                txtAreaMailText.setText(newEmail.getText());
                lblDebugId.setText(Integer.toString(newEmail.getId()));
            }
        });
        model.currentEmailProperty().addListener(observable -> {
            controlsOff();
            buttonsOn();
        });//set controls disabled
        //</editor-fold>

        //<editor-fold desc="Reply - ReplyAll - Forward">
        btnReply.setOnAction(actionEvent -> {
            model.setCurrentEmail(new Email(
                    model.getLastEmaild(),
                    model.getUsername(),
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
                if(!(item.equals(model.getUsername())))
                    newRecipients.add(item);
            };
            model.setCurrentEmail(new Email(
                    model.getLastEmaild(),
                    model.getUsername(),
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
                    model.getUsername(),
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
                    model.getUsername(),
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
            // TODO mandare al server messaggio tipo delete
        });

        btnSend.setOnAction(actionEvent -> {
            /* TODO
                * controllo campi
                * inserisco id per sentlist del mittente
                * creo messaggio e mando al socket
            */
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
