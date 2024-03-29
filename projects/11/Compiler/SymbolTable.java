import java.util.Map;
import java.util.HashMap;

public class SymbolTable {

    private String currentType;
    private String currentKind;

    private Map<String, Symbol> classScope;
    private Map<String, Symbol> subroutineScope;
    private int staticIndex = 0;
    private int fieldIndex = 0;
    private int argumentIndex = 0;
    private int localIndex = 0;

    public SymbolTable() {
        classScope = new HashMap<String, Symbol>();
        subroutineScope = new HashMap<String, Symbol>();
        resetClassScope();
        resetSubroutineScope();
    }

    public void define(String name, String type, String kind) {
        System.out.println(String.format("name: %s, type: %s, kind: %s", name, type, kind));
        switch (kind) {
            case "static":
                classScope.put(name, new Symbol(type, "static", staticIndex++));
                break;
            case "field":
                classScope.put(name, new Symbol(type, "this", fieldIndex++));
                break;
            case "argument":
                subroutineScope.put(name, new Symbol(type, "argument", argumentIndex++));
                break;
            case "var":
                subroutineScope.put(name, new Symbol(type, "local", localIndex++));
                break;
            default:
                throw new NoSuchFieldError("no such kind");
        }
    }

    public void resetClassScope() {
        classScope.clear();
        staticIndex = 0;
        fieldIndex = 0;
    }

    public void resetSubroutineScope() {
        subroutineScope.clear();
        argumentIndex = 0;
        localIndex = 0;
    }

    public void setCurrentType(String type) {
        currentType = type;
    }

    public String getCurrentType() {
        return currentType;
    }

    public void setCurrentKind(String kind) {
        currentKind = kind;
    }

    public String getCurrentKind() {
        return currentKind;
    }

    public Symbol getSymbolByName(String name) {
        if (subroutineScope.containsKey(name)) {
            return subroutineScope.get(name);
        }
        if (classScope.containsKey(name)) {
            return classScope.get(name);
        }
        throw new NoSuchFieldError("no such name in scope");
    }

    // Development Tool for Developer
    public void showSymbolTable() {
        System.out.println("------SymbolTable-------");
        classScope.forEach((name, symbol) -> {
            System.out.println(name + " --- > " + symbol.getInfo());
        });
        subroutineScope.forEach((name, symbol) -> {
            System.out.println(name + " --- > " + symbol.getInfo());
        });
    }
}