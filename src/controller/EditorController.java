package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Connection;
import model.DataModel;
import model.Email;

import java.io.IOException;
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

    @FXML
    private Label lblError;

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
            if(model.getListFilter().equals("inbox")) {
                model.setCurrentEmail(new Email(
                        model.getNextEmaild(),
                        model.getUsername(),
                        new ArrayList<String>(Arrays.asList(model.getCurrentEmail().getSender().split(" "))),
                        "RE: " + model.getCurrentEmail().getSubject(),
                        "",
                        new Date()
                ));
                controlsOn();
                buttonsOff();
            }
            else {
                lblError.setText("non puoi risponderti da solo genio");
            }
        });

        btnReplyAll.setOnAction(actionEvent -> {
            if(model.getListFilter().equals("inbox")) {
                ArrayList<String> newRecipients = new ArrayList<>();
                newRecipients.add(model.getCurrentEmail().getSender());
                for (String item : model.getCurrentEmail().getRecipient()) {
                    if (!(item.equals(model.getUsername())))
                        newRecipients.add(item);
                }
                ;
                model.setCurrentEmail(new Email(
                        model.getNextEmaild(),
                        model.getUsername(),
                        newRecipients,
                        "RE: " + model.getCurrentEmail().getSubject(),
                        "",
                        new Date()
                ));
                controlsOn();
                buttonsOff();
            }
            else {
                lblError.setText("non puoi risponderti da solo genio");
            }
        });

        btnForward.setOnAction(actionEvent -> {
            lblError.setText("");
            model.setCurrentEmail(new Email(
                    model.getNextEmaild(),
                    model.getUsername(),
                    new ArrayList<String>(),
                    "FW: "+ model.getCurrentEmail().getSubject(),
                    model.getCurrentEmail().getText() + "\nThis message was originally sent from"+model.getCurrentEmail().getSender(),
                    new Date()
            ));
            controlsOn();
            buttonsOff();
        });
        //</editor-fold>

        //<editor-fold desc="New - Delete - Send">
        btnNew.setOnAction(actionEvent -> {
            lblError.setText("");
            model.setCurrentEmail(new Email(
                    model.getNextEmaild(),
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
            lblError.setText("");
            Email email = model.getCurrentEmail();
            String mailbox = "";
            if(model.getCurrentEmail().getSender().equals(model.getUsername())) { // sender == username logged
                model.rmFromSent(email);
                mailbox = "sent";
            }
            else { // inbox
                model.rmFromInbox(email);
                mailbox = "inbox";
            }
            model.getMailList().remove(email);
            // now server stuffs
            Connection connection = model.getConnectionInstance();
            if(connection.isConnected()) {

                try {
                    connection.delete(email, mailbox);
                }
                catch (IOException e) {
                    System.err.println("Error on delete: "+e.getMessage());
                }
                btnNew.fire(); //clear fields
                model.setListFilter(mailbox);
            }
            else {
                lblError.setText("ERROR! Connection w/ server is down. Retry later");
            }
        });

        btnSend.setOnAction(actionEvent -> {
            if(model.checkEmail()) {
                model.getCurrentEmail().setRecipient(new ArrayList<String>(Arrays.asList(txtRecipients.getText().trim().split(","))));
                model.getCurrentEmail().setSubject(txtSubject.getText());
                model.getCurrentEmail().setText(txtAreaMailText.getText());
                model.getCurrentEmail().setMailingDate(new Date());
                Connection connection = model.getConnectionInstance();
                if(connection.isConnected()) {
                    try {
                        connection.send(model.getCurrentEmail());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    model.incrementNextEmailId(); //impoooo
                    model.addToSent(model.getCurrentEmail());
                    model.setListFilter("sent");
                    btnNew.fire();
                }
                else {
                    lblError.setText("ERROR! Connection w/ server is down. Retry later");
                }
            }
            else {
                lblError.setText("ERROR! cannot validate emails");
            }
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
