import java.lang.NoSuchFieldException; 
import java.util.Map;
import java.util.HashMap;

public class SymbolTable {
    
    private static int symbolCounter;
    private static Map<String, Integer> symbolTable;

    public SymbolTable() {
        symbolCounter = 16;
        symbolTable = new HashMap<String, Integer>();

        symbolTable.put("R0", 0);
        symbolTable.put("R1", 1);
        symbolTable.put("R2", 2);
        symbolTable.put("R3", 3);
        symbolTable.put("R4", 4);
        symbolTable.put("R5", 5);
        symbolTable.put("R6", 6);
        symbolTable.put("R7", 7);
        symbolTable.put("R8", 8);
        symbolTable.put("R9", 9);
        symbolTable.put("R10", 10);
        symbolTable.put("R11", 11);
        symbolTable.put("R12", 12);
        symbolTable.put("R13", 13);
        symbolTable.put("R14", 14);
        symbolTable.put("R15", 15);
        symbolTable.put("SCREEN", 16384);
        symbolTable.put("KBD", 24576);
        symbolTable.put("SP", 0);
        symbolTable.put("LCL", 1);
        symbolTable.put("ARG", 2);
        symbolTable.put("THIS", 3);
        symbolTable.put("THAT", 4);
        
    }

    public static void addEntry(String symbol, int address) throws NoSuchFieldException {
        if(contains(symbol)) throw new NoSuchFieldException("This symbol has already existed in SymbolTable");
        symbolTable.put(symbol, address);
    }

    public static boolean contains(String symbol) {
        return symbolTable.containsKey(symbol);
    }

    public static int GetAddress(String symbol) throws NoSuchFieldException {
        if(!contains(symbol)) throw new NoSuchFieldException("No such symbol key in SymbolTable");
        return symbolTable.get(symbol);
    }

}

