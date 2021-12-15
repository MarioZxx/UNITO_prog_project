package src.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.LinkedList;
import java.util.List;

public class Account {

  private final ListProperty<Email> inbox;
  private final ObservableList<Email> inboxContent;
  private final String emailAddress;

  /**
   * Constructor of the class.
   *
   * @param emailAddress   indirizzo email
   *
   */

  public Account(String emailAddress) {
    this.inboxContent = FXCollections.observableList(new LinkedList<>());
    this.inbox = new SimpleListProperty<>();
    this.inbox.set(inboxContent);
    this.emailAddress = emailAddress;
  }

  /**
   * @return      list of email
   */
  public ListProperty<Email> inboxProperty() {
    return inbox;
  }

  /**
   * @return      email address of this account
   */
  public String getEmailAddress() {
    return emailAddress;
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
