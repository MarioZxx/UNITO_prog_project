package src.model;

import org.w3c.dom.Document;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

public class Log implements Serializable {
  private Date date;
  private String account;
  private String information;
  private String operation; //login, logout, send, delete, receive, error
  private Email email;
  //private Account account

  /**
  * @param date moment of the log
  * @param account user that sended the log
  * @param information info of the log
  * @param operation what kind of operation is made
  * @param email
  * */
  public Log(Date date, String account,String information, String operation, Email email) {
    this.date = date;
    this.account = account; //emailAddr of the account
    this.information = information;
    this.operation = operation;
    this.email = email;
  }

  public Date getDate() {
    return date;
  }

  public String getAccount() {
    return account;
  }

  public String getInformation() {
    return information;
  }

  public String getOperation() {
    return operation;
  }

  public Email getEmail() {
    return email;
  }

  @Override
  public String toString() {
    DateFormat df = new SimpleDateFormat("[dd/MM/yyyy HH:mm:ss]");
    return String.join("  -  ", List.of(df.format(this.date), this.account, this.information)) + "\n";
  }

}