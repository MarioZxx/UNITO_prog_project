package src.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.LinkedList;
import java.util.List;

public class Account {

  private final String emailAddress;
  private final ListProperty<Email> inbox;
  private final ObservableList<Email> inboxContent;

  /**
   * Constructor of the class.
   *
   * @param emailAddress   indirizzo email
   *
   */

  public Account(String emailAddress) {
    this.emailAddress = emailAddress;
    this.inboxContent = FXCollections.observableList(new LinkedList<>());
    this.inbox = new SimpleListProperty<>();
    this.inbox.set(inboxContent);
  }

  /**
   * @return      email address of this account
   */
  public String getEmailAddress() {
    return new String(emailAddress);
  }

  /**
   * @return      list of email
   */
  public ListProperty<Email> inboxProperty() {
    return inbox;
  }

  /**
   * delete selected email
   */
  public void deleteEmail(Email email) {
    inboxContent.remove(email);
  }

  /**
   *Receive a List of Email, and save them in the inbox
   */
  public void saveNewEmails(List<Email> newEmails) {
    inboxContent.addAll(newEmails);
  }

}
