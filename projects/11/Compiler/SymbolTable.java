import java.util.HashMap;

public class SymbolTable {

    private HashMap<String, Symbol> ClassScope;
    private HashMap<String, Symbol> SubroutineScope;
    private String STATIC = "static";
    private String FIELD = "field";
    private String ARGUMENT = "argument";
    private String LOCAL = "local";
    private int StaticIndex = 0;
    private int FieldIndex = 0;
    private int ArgumentIndex = 0;
    private int LocalIndex = 0;

    public SymbolTable() {

    }

    public void define(String name, String type, String kind) {
        switch (kind) {
            case "static":
                ClassScope.put(name, new Symbol(type, kind, StaticIndex++));
                break;
            case "field":
                ClassScope.put(name, new Symbol(type, kind, FieldIndex++));
                break;
            case "argument":
                SubroutineScope.put(name, new Symbol(type, kind, ArgumentIndex++));
                break;
            case "local":
                SubroutineScope.put(name, new Symbol(type, kind, LocalIndex++));
                break;
            default:
                break;
        }
    }
}