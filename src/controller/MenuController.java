package controller;

import com.sun.javafx.beans.event.AbstractNotifyListener;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MenuController {
    private final String PATH_TO_ACCOUNT_LIST = System.getProperty("user.dir")+"/files/accountlist.txt";
    private final String INBOX_FILTER = "inbox";
    private final String SENT_FILTER = "sent";
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

        model.listFilterProperty().addListener((obs, oldFilter, newFilter) -> {
            if(newFilter.equals(INBOX_FILTER)) {
                menuBtnMailBox.setText(INBOX_FILTER);
                model.setMailList(model.Inbox());
            }
            else if(newFilter.equals(SENT_FILTER)) {
                menuBtnMailBox.setText(SENT_FILTER);
                model.setMailList(model.Sent());
            }
            else { // refreshh
                model.setListFilter(menuBtnMailBox.getText());
            }
            //setto ultimo id
            if(model.getMailList().size() > 0)
                model.setNextEmailId(model.getMailList().get(model.getMailList().size()-1).getId());
            else
                model.setNextEmailId(1);

            tableViewMails.setItems(model.getMailList());
        });

        menuItemInbox.setOnAction(actionEvent -> {
            model.setListFilter(INBOX_FILTER);
        });

        menuItemSent.setOnAction(actionEvent -> {
            model.setListFilter(SENT_FILTER);
        });

        // fill editor with choosen email, readonly mode
        tableViewMails.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            model.setCurrentEmail(newSel);
        });

        // re-select email when already highlighted
        /*
        tableViewMails.focusedProperty().addListener((obs, oldSel, newSel) -> {
            model.setCurrentEmail(tableViewMails.getSelectionModel().getSelectedItem());
        });
        */
        model.getMailList().addListener((ListChangeListener<Email>) change -> {
            tableViewMails.setItems(model.getMailList());
        });

        // inbox loaded as default mailbox when application starts
        menuItemInbox.fire();

        //</editor-fold>

        //<editor-fold>

        //</editor-fold>
    }
}
