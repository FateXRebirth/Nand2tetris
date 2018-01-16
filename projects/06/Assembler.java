import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
public class Assembler {
    
    // declaration about symbol table 
    static int symbolCounter = 16;
    static Map<String, Integer> table = new HashMap<String, Integer>();

    public static void main(String[] args) throws IOException {
        /* System.out.println(args[0]); */
        
        // pre-define symbol
        table.put("R0", 0);
        table.put("R1", 1);
        table.put("R2", 2);
        table.put("R3", 3);
        table.put("R4", 4);
        table.put("R5", 5);
        table.put("R6", 6);
        table.put("R7", 7);
        table.put("R8", 8);
        table.put("R9", 9);
        table.put("R10", 10);
        table.put("R11", 11);
        table.put("R12", 12);
        table.put("R13", 13);
        table.put("R14", 14);
        table.put("R15", 15);
        table.put("SCREEN", 16384);
        table.put("KBD", 24576);
        table.put("SP", 0);
        table.put("LCL", 1);
        table.put("ARG", 2);
        table.put("THIS", 3);
        table.put("THAT", 4);

        // code without white-space and comments (pure)
        ArrayList<String> contents = new ArrayList<String>();

        // line number corresponds to code
        int lineCounter = 0;

        // get assemble code from .asm file
        List<String> content = Files.readAllLines(Paths.get("fill.asm"));

        // first-pass
        for(String line : content) {
            // handle empty and comments
            if(makeSense(line)) {
                // handle label
                if(line.startsWith("(")) {
                    int start = 0;
                    int end = line.indexOf(")");
                    String label = line.substring(start+1, end);
                    if(!isExist(label)) table.put(label, lineCounter);
                }
                lineCounter+=1;
                System.out.println(String.valueOf(lineCounter) + " " + line);
            }
        }

        // second-pass
        for(String line : content) {
            // handle empty and comments
            if(makeSense(line)) {
                // handle variable
                if(line.indexOf("@")!= -1) {
                    int start = line.indexOf("@");
                    String label = line.substring(start+1);
                    if(!isExist(label) && !label.matches("[0-9]*")) table.put(label, symbolCounter++);
                }
            }
        }

        // result
        String result = "";

        //tanslate 
        for(String line : content) {
            if(makeSense(line)) {
                // handle A instruction
                if(line.indexOf("@") != -1) {
                    result = translateA(line);
                    contents.add(result);
                }
                // handle orther instruction
                else if(line.indexOf("(") != -1) {
                    // Do nothing
                }
                // handle C instruction
                else {
                    result = translateC(line);
                    contents.add(result);
                }
            }
        }

        // watch table
        System.out.println("------TABLE-------");
        for(Map.Entry<String, Integer> entry : table.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();
            System.out.println(key + " < --- > " + String.valueOf(value));
        }
        
        // output to .hack file
        Charset utf8 = StandardCharsets.UTF_8;
        Files.write(Paths.get("output.text"), contents, utf8);
    }    

    // check if this key is already exist in table 
    static boolean isExist(String key) {
        return table.containsKey(key);
    }

    // check if this line does makesense
    static boolean makeSense(String line) {
        return !line.startsWith("//") && !line.isEmpty() && !line.endsWith(" ");
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
    static String translateA(String line) { 
        int start = line.indexOf("@");
        String result = "";
        String label = line.substring(start+1);
        if(isExist(label)) {
            result =  Integer.toBinaryString(table.get(label));
            return makeComplete(result.length()) + result;
        } else {
            result = Integer.toBinaryString(Integer.parseInt(label));
            return makeComplete(result.length()) + result;
        }
    }

    // translate C instruction
    static String translateC(String line) {
        return line + " C ";
    }

}

