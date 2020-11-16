package sample;


import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.beans.XMLEncoder;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DataModel {
    /*
     * - lista utenti
     * - lista messaggi in base all'utente
     * - lista messaggi mandati in base all'utente
     * - lista messaggi eliminati in base all'utente
     *
     */
    private final AtomicInteger lastEmailId = new AtomicInteger(0);

    public int getLastEmaild() {
        return lastEmailId.get();
    }

    public void incrementLastEmailId() {
        lastEmailId.incrementAndGet();
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


    //<editor-fold desc="cmbUsername for switching between accounts">
    /*
     * Properties
     */
    private final ObservableList<Account> accountlist = FXCollections.observableArrayList(account ->
            new Observable[] { account.accountNameProperty()});

    public final ObjectProperty<Account> currentAccount = new SimpleObjectProperty<>();

    public ObservableList<Account> getAccountlist() {
        return accountlist;
    }

    public ObjectProperty<Account> currentAccountProperty() {
        return currentAccount;
    }

    public Account getCurrentAccount() {
        return currentAccountProperty().get();
    }

    public void setCurrentAccount(Account account) {
        System.out.println("SELECTEED: "+account.getAccountName());
        currentAccountProperty().set(account);
    }

    /*
     *  Loading account list from file
     */
    public void loadAccountList(File f) {
        ArrayList<Account> accounts = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                accounts.add(new Account(line));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        accountlist.setAll(accounts);
    }

    //</editor-fold>


    //<editor-fold desc="ListMail management conditioned by FilterList">
    /*
     *  Properties
     */
    private final ObservableList<Email> mailList = FXCollections.observableArrayList(email ->
            new Observable[] {
                    email.idProperty(),
                    email.senderProperty(),
                    email.recipientProperty(),
                    email.subjectProperty(),
                    email.textProperty(),
                    email.mailingDateProperty()}
    );

    public ObservableList<Email> getMailList() {
        return mailList;
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
    /*
     *  Loading mail list from file
     */
    public void loadMailList() {
        ArrayList<Email> emails = new ArrayList<>();
        File f = new File(System.getProperty("user.dir")+
                "/files/"+getCurrentAccount().getAccountName()+ // account name
                listFilterProperty().getValue()+                // choosen mailbox
                ".txt");
        String line;
        try {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            BufferedReader br = new BufferedReader(new FileReader(f));
            while ((line = br.readLine()) != null) {
                String[] s = line.split("###");
                ArrayList<String> recipients = new ArrayList<String>(Arrays.asList(s[2].split(",")));
                emails.add(new Email(
                        Integer.parseInt(s[0]),
                        s[1],
                        recipients,
                        s[3],
                        s[4],
                        format.parse(s[5])
                ));
            }
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        if(emails.size() > 0)
            lastEmailId.getAndAdd(emails.get(emails.size()-1).getId()+1); //last id stored in lastEmailId
        else
            lastEmailId.getAndAdd(0);

        Collections.sort(emails);

        mailList.setAll(emails);
    }

    //</editor-fold>


    //<editor-fold desc="">

    //</editor-fold>
}
