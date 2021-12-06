package model;

public class Account {

  private String id;
  private String mail;
  private String password;

  private Account() {}

  public Account(String id, String mail, String password) {
    this.id = id;
    this.mail = mail;
    this.password = password;
  }

  public String getId() {    return id;  }

  public String getMail() {    return mail;  }

  public String getPassword() {    return password;  }

  @Override
  public String toString() {
    return "Account{" +
    "id='" + id + '\'' +
    ", mail='" + mail + '\'' +
    ", password='" + password + '\'' +
    '}';
  }
}
