public class Token {

  private String type;
  private String value;

  public Token(String Type, String Value) {
    this.type = Type;
    this.value = Value;
  }

  public void setType(String Type) {
    this.type = Type;
  }

  public String getType() {
    return this.type;
  }

  public void setToken(String Value) {
    this.value = Value;
  }

  public String getToken() {
    return this.value;
  }

  public String XmlFormat() {
    return "  <" + this.type + "> " + this.value + " </" + this.type + ">"; 
  }

}