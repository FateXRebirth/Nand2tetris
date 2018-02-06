import java.io.*;
import java.util.ArrayList;

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
        
        // Store generated code 
        ArrayList<String> contents = new ArrayList<String>();

        // line number corresponds to code
        int lineCounter = 0;

        // Parser for first-pass
        Parser firstPassParser = new Parser(source);
        // SymbolTable
        SymbolTable symbolTable = new SymbolTable();

        // first-pass
        while(firstPassParser.hasMoreCommands()) {
            firstPassParser.advance();
            String type = firstPassParser.commandType();
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

        // Parser for second-pass
        Parser secondPassParser = new Parser(source);
        // useful variable
        String result = "";
        String code = "";
        String symbol = "";
        String dest = "";
        String comp = "";
        String jump = "";
        String zeros;
        int times;

        // second-pass
        while(secondPassParser.hasMoreCommands()) {
            secondPassParser.advance();
            String type = secondPassParser.commandType();
            secondPassParser.handle();
            switch(type) {
                case "A_Command":
                    symbol = secondPassParser.symbol();
                    if(symbol.matches("[0-9]*")) {
                        result = Integer.toBinaryString(Integer.valueOf(symbol));
                    } else {
                        if(!symbolTable.contains(symbol)) symbolTable.addEntry("A_Command", symbol, 0);
                        result = Integer.toBinaryString(symbolTable.GetAddress(symbol));
                    }
                    // complete the remaining zeros
                    times = 15-result.length();
                    zeros = "";
                    for(int i = 0 ; i < times ; i++) {
                        zeros+= "0";
                    }
                    code = "0" + zeros + result;
                    break;
                case "C_Command":
                    comp = secondPassParser.comp();
                    dest = secondPassParser.dest();
                    jump = secondPassParser.jump();
                    code = "111" + Code.Comp(comp) + Code.Dest(dest) + Code.Jump(jump);
                    break;
                default:
                    break;
            }
            if(code != "") contents.add(code);
            // reset
            code = "";
            secondPassParser.reset();
        }

        // output to .hack file
        try {
            FileWriter fileWriter = new FileWriter(output);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for(String content : contents) {
                bufferedWriter.write(content);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }    
}

