import java.util.List;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;

public class Assembler {
    
    public static void main(String[] args) {
        
        // check if user provides argument to handle
        if(args.length < 1) {
            throw new IllegalArgumentException("Missing argument! (e.g. java Assembler add.asm) ");
        }
        
        // get input filename
        String source = args[0];
        // set output filename
        String output = source.replace(".asm", ".hack");
        
        // code without white-space and comments (pure)
        ArrayList<String> contents = new ArrayList<String>();

        // line number corresponds to code
        int lineCounter = 0;

        Parser firstPassParser = new Parser(source);
        SymbolTable symbolTable = new SymbolTable();

        while(firstPassParser.hasMoreCommands()) {
            firstPassParser.advance();
            String type = firstPassParser.commandType();
            System.out.println(String.valueOf(lineCounter) + " " + type);
            if(type == "N_Command" || type == "L_Command") {
                if(type == "L_Command") {
                    firstPassParser.handle();
                    String label = firstPassParser.symbol();
                    symbolTable.addEntry("L_Command", label, lineCounter);
                }
            } else {
                lineCounter++;
            }
        }

        if(args.length > 1) symbolTable.showSymbolTable();

        // output to .hack file
        // Charset utf8 = StandardCharsets.UTF_8;
        // Files.write(Paths.get(output), contents, utf8);
    }    

    // complete the remaining zero
    static String makeComplete(int length) {
        int times = 15-length;
        String zeros = "";
        for(int i = 0 ; i < times ; i++) {
            zeros+= "0";
        }
        return "0" + zeros;
    }

    // translate A instruction
    /* result =  Integer.toBinaryString(table.get(label)); */
    /* return makeComplete(result.length()) + result; */
    /* result = Integer.toBinaryString(Integer.parseInt(label)); */
    /* return makeComplete(result.length()) + result; */
}

