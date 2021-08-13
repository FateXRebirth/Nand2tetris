import java.util.Map;
import java.util.HashMap;

public class SymbolTable {

    private Map<String, Symbol> ClassScope;
    private Map<String, Symbol> SubroutineScope;
    private String STATIC = "static";
    private String FIELD = "field";
    private String ARGUMENT = "argument";
    private String LOCAL = "local";
    private int StaticIndex = 0;
    private int FieldIndex = 0;
    private int ArgumentIndex = 0;
    private int LocalIndex = 0;

    public SymbolTable() {
        ClassScope = new HashMap<String, Symbol>();
        SubroutineScope = new HashMap<String, Symbol>();
        resetClassScope();
        resetSubroutineScope();
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

    public void resetClassScope() {
        ClassScope.clear();
        StaticIndex = 0;
        FieldIndex = 0;
    }

    public void resetSubroutineScope() {
        SubroutineScope.clear();
        ArgumentIndex = 0;
        LocalIndex = 0;
    }

    // Development Tool for Developer
    public void showSymbolTable() {
        System.out.println("------TABLE-------");
        for (Map.Entry<String, Symbol> entry : ClassScope.entrySet()) {
            String name = entry.getKey();
            String info = entry.getValue().getInfo();
            System.out.println(name + " --- > " + String.valueOf(info));
        }
    }
}