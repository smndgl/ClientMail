package controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MenuController {
    private final String PATH_TO_ACCOUNT_LIST = System.getProperty("user.dir")+"/files/accountlist.txt";
    private final String INBOX_FILTER = "INBOX";
    private final String SENT_FILTER = "SENT";
    /*
     *  COMPONENTS
     */

    /*
     * menu
     */
    @FXML
    private MenuButton menuBtnMailBox;
    @FXML
    private MenuItem menuItemInbox;
    @FXML
    private MenuItem menuItemSent;

    /*
     * table view
     */
    @FXML
    private TableView<Email> tableViewMails;
    @FXML
    private TableColumn<Email, String> columnMailingDate;
    @FXML
    private TableColumn<String, String> columnSubject;
    @FXML
    private TableColumn<String, String> columnSender;


    private DataModel model;

    public void initModel(DataModel model) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model;

        //<editor-fold desc="tableViewMails">
        // sort on column header click disabled
        tableViewMails.setOnSort(Event::consume);

        tableViewMails.setPlaceholder(new Label("No email in this mailbox"));
        columnMailingDate.setResizable(false);
        columnSender.setResizable(false);
        columnSubject.setResizable(false);

        //column rendering
        columnMailingDate.setCellValueFactory(email -> {
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            return new SimpleStringProperty(df.format(email.getValue().getMailingDate()));
        });

        columnSubject.setCellValueFactory(new PropertyValueFactory<String, String>("subject"));

        columnSender.setCellValueFactory(new PropertyValueFactory<String, String>("sender"));

        // choosen mailbox set { inbox, sent }
        model.listFilterProperty().addListener((obs, oldFilter, newFilter) -> {
            Connection connection = model.getConnectionInstance();
            try {
                connection.fetchMailbox(model.getListFilter());
                Message m = connection.getMessage();
                if(m.getContent() instanceof String) {
                    Type type = new TypeToken<Collection<Email>>(){}.getType();
                    model.setMailList(new Gson().fromJson((String)m.getContent(), type));
                    //set atomic int
                    model.setLastEmailId(model.getMailList().get(model.getMailList().size()-1).getId());
                }
            }
            catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
            }
            catch (NullPointerException e) {
                System.out.println("connesione morta ihihihi");
                // TODO avvisare che la connesione è morta
            }
            tableViewMails.setItems(model.getMailList());
        });

        menuItemInbox.setOnAction(actionEvent -> {
            menuBtnMailBox.setText("Inbox");
            model.setListFilter(INBOX_FILTER);
        });

        menuItemSent.setOnAction(actionEvent -> {
            menuBtnMailBox.setText("Sent");
            model.setListFilter(SENT_FILTER);
        });

        // fill editor with choosen email, readonly mode
        tableViewMails.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            model.setCurrentEmail(newSel);
        });

        // re-select email when already highlighted
        tableViewMails.focusedProperty().addListener((obs, oldSel, newSel) -> {
            model.setCurrentEmail(tableViewMails.getSelectionModel().getSelectedItem());
        });

        // inbox loaded as default mailbox when application starts
        menuItemInbox.fire();

        //</editor-fold>

        //<editor-fold>

        //</editor-fold>
    }
}
