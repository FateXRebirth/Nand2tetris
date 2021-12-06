public class Symbol {

    private String type;
    private String kind;
    private int index;

    public Symbol(String type, String kind, int index) {
        this.type = type;
        this.kind = kind;
        this.index = index;
    }

    public String getType() {
        return this.type;
    }

    public String getKind() {
        return this.kind;
    }

    public int getIndex() {
        return this.index;
    }

    public String getInfo() {
        return "Type: " + this.type + ", Kind: " + this.kind + ", Index: " + this.index;
    }
}