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
        Map<String, Integer> codeToLine = new HashMap<String, Integer>();
        
        // get assemble code from .asm file
        List<String> content = Files.readAllLines(Paths.get("fill.asm"));

        // first-pass
        for(String line : content) {                     
            // handle comments and white-space
            if(!line.startsWith("//") && !line.isEmpty() && !line.endsWith(" ")) {
                if(line.startsWith("(")) {
                    int start = 0;
                    int end = line.indexOf(")");
                    String label = line.substring(start+1, end);
                    table.put(label, lineCounter);
                }
                else {
                    System.out.println(String.valueOf(lineCounter) + " " + line);
                    codeToLine.put(line, lineCounter);
                    lineCounter++;
                    contents.add(line);
                }
            }
        }

        // watch table
        for(Map.Entry<String, Integer> entry : table.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();
            System.out.println(key + " < --- > " + String.valueOf(value));
        }

        Charset utf8 = StandardCharsets.UTF_8;
        Files.write(Paths.get("output.text"), contents, utf8);
    }    
}

