package src.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Log {
  private Date date;
  private String information;
  private String operation; //login, logout, send, delete, receive, error
  private Email email;

  public Log(Date date, String information, String operation, Email email) {
    this.date = date;
    this.information = information;
    this.operation = operation;
    this.email = email;
  }

  public Date getDate() {
    return date;
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
    return String.join(" - ", List.of(this.date.toString(), this.information));
  }
}