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
        return this.type;
    }

    public int getIndex() {
        return this.index;
    }
}