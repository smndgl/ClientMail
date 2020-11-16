package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MenuController {
    private final String PATH_TO_ACCOUNT_LIST = System.getProperty("user.dir")+"/files/accountlist.txt";
    private final String INBOX_FILTER = "-inboxlist";
    private final String SENT_FILTER = "-sentlist";
    private final String BIN_FILTER = "-binlist";
    /*
     *  COMPONENTS
     */
    @FXML
    private ComboBox<Account> cmbUsername;

    /*
     * menu
     */
    @FXML
    private MenuButton menuBtnMailBox;
    @FXML
    private MenuItem menuItemInbox;
    @FXML
    private MenuItem menuItemSent;
    @FXML
    private MenuItem menuItemBin;

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

        //<editor-fold desc="cmbUsernames">
        StringConverter<Account> converter = new StringConverter<Account>() {
            @Override
            public String toString(Account account) {
                return account.getAccountName();
            }
            @Override
            public Account fromString(String string) {
                return null;
            }
        };
        cmbUsername.setConverter(converter);

        model.loadAccountList(new File(PATH_TO_ACCOUNT_LIST));
        cmbUsername.setItems(model.getAccountlist());

        cmbUsername.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) ->
                model.setCurrentAccount(newSel));

        model.currentAccountProperty().addListener((obs, oldAccount, NewAccount) ->
                cmbUsername.getSelectionModel().select(NewAccount));

        cmbUsername.getSelectionModel().selectFirst();

        //listener per cambiare la view a seconda dell'elemento selezionato (BOOOOM)
        // binding dinamico lista email a seconda dell'utente selezionato nella combobox
        model.currentAccountProperty().addListener((obs, oldAc, newAc) -> {
            //richiamo mailLoader
            //TODO gestire cambio utente
        });


        //</editor-fold>


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

        // choosen mailbox set { inbox, sent, bin }
        model.listFilterProperty().addListener((obs, oldFilter, newFilter) -> {
                model.loadMailList();
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

        menuItemBin.setOnAction(actionEvent -> {
            menuBtnMailBox.setText("Bin");
            model.setListFilter(BIN_FILTER);
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
    }
}
