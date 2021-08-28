public class Token {

    private String type;
    private String value;

    public Token(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        return "Token(" + this.type + "," + this.value + ")";
    }

    public String xmlFormat() {
        return "<" + this.type + "> " + this.value + " </" + this.type + ">";
    }
}