package src.model;

import java.io.Serial;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

public class Log implements Serializable {
  @Serial
  private static final long serialVersionUID = -1496634471427919911L;
  private Date date;
  private String user;
  private String information;
  private String operation; //login, regis, send, exit, delete
  private Email email;

  /**
  * @param date moment of the log
  * @param user user that sended the log
  * @param information info of the log
  * @param operation what kind of operation is made
  * @param email email that will be operated
  * */
  public Log(Date date, String user, String information, String operation, Email email) {
    this.date = date;
    this.user = user; //emailAddr of the account
    this.information = information;
    this.operation = operation;
    this.email = email;
  }

  public Date getDate() {
    return date;
  }

  public String getUser() {
    return user;
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
    return String.join("  -  ", List.of(df.format(this.date), this.user, this.information)) + "\n";
  }

}