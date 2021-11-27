import java.util.Map;
import java.util.HashMap;

public class SymbolTable {

    private Map<String, Symbol> classScope;
    private Map<String, Symbol> subroutineScope;
    private Map<String, Integer> indices;

    public SymbolTable() {
        classScope = new HashMap<String, Symbol>();
        subroutineScope = new HashMap<String, Symbol>();
        indices = new HashMap<String, Integer>();
        indices.put("static", 0);
        indices.put("field", 0);
        indices.put("argument", 0);
        indices.put("var", 0);
    }

    public void define(String name, String type, String kind) {
        int index = indices.get(kind);
        switch (kind) {
            case "static":
                classScope.put(name, new Symbol(type, "static", index));
                break;
            case "field":
                classScope.put(name, new Symbol(type, "this", index));
                break;
            case "function":
                classScope.put(name, new Symbol(type, "function", index));
                break;
            case "argument":
                subroutineScope.put(name, new Symbol(type, "argument", index));
                break;
            case "var":
                subroutineScope.put(name, new Symbol(type, "local", index));
                break;
            default:
                throw new NoSuchFieldError("no such kind");
        }
        indices.put(kind, index + 1);
    }

    public void startSubroutine() {
        subroutineScope.clear();
        indices.put("argument", 0);
        indices.put("var", 0);
    }

    public int indexOf(String kind) {
        return indices.get(kind);
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
}