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
        
        // check if user provides argument to handle
        if(args.length < 1) {
            throw new IllegalArgumentException("Missing argument! (e.g. java Assembler add.asm) ");
        }
        
        // get input filename
        String source = args[0];
        // set output filename
        String output = source.replace(".asm", ".hack");
        
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
        int lineCounter = -1;

        // get assemble code from .asm file
        List<String> content = Files.readAllLines(Paths.get(source));

        // first-pass
        for(String input : content) {
            // handle empty and comments
            if(makeSense(input)) {
                String line = makePure(input);
                // handle label
                if(line.indexOf("(") != -1) {
                    int start = 0;
                    int end = line.indexOf(")");
                    String label = line.substring(start+1, end);
                    if(!isExist(label)) table.put(label, lineCounter+1);
                } else {
                    lineCounter+=1;
                }
            }
        }

        // second-pass
        for(String input: content) {
            // handle empty and comments
            if(makeSense(input)) {
                String line = makePure(input);
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
        for(String input : content) {
            if(makeSense(input)) {
                String line = makePure(input);
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
        /* System.out.println("------TABLE-------"); */
        /* for(Map.Entry<String, Integer> entry : table.entrySet()) { */
        /*     String key = entry.getKey(); */
        /*     int value = entry.getValue(); */
        /*     System.out.println(key + " < --- > " + String.valueOf(value)); */
        /* } */
        
        // output to .hack file
        Charset utf8 = StandardCharsets.UTF_8;
        Files.write(Paths.get(output), contents, utf8);
    }    

    // check if this key is already exist in table 
    static boolean isExist(String key) {
        return table.containsKey(key);
    }

    // make string pure ( without space character and comments )
    static String makePure(String line) {
        String result = "";
        result = line.replace(" ", "");
        if(result.indexOf("/") != -1) {
            result = result.substring(0, result.indexOf("/"));
        }
        return result;
    }

    // check if this line does makesense
    static boolean makeSense(String line) {
        return !line.startsWith("//") && !line.isEmpty();
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

    // handle destination
    static String handleDest(String dest) {
        String result = "";
        switch(dest) {
             case "M":
                 result = "001";
                 break;
             case "D":
                 result = "010";
                 break;
             case "MD":
                 result = "011";
                 break;
             case "A":
                 result = "100";
                 break;
             case "AM":
                 result = "101";
                 break;
             case "AD":
                 result = "110";
                 break;
             case "AMD":
                 result = "111";
                 break;
             default:
                 result = "000";
         }
         return result;
    }

    // handle compute 
    static String handleComp(String comp) {
        String result = "";
        switch(comp) {
            case "0":
                result = "0101010";
                break;
            case "1":
                result = "0111111";
                break;
            case "-1":
                result = "0111010";
                break;
            case "D":
                result = "0001100";
                break;
            case "A":
                result = "0110000";
                break;
            case "M":
                result = "1110000";
                break;
            case "!D":
                result = "0001101";
                break;
            case "!A":
                result = "0110001";
                break;
            case "!M":
                result = "1110001";
                break;
            case "-D":
                result = "0001111";
                break;
            case "-A":
                result = "0110011";
                break;
            case "-M":
                result = "1110011";
                break;
            case "D+1":
            case "1+D":
                result = "0011111";
                break;
            case "A+1":
            case "1+A":
                result = "0110111";
                break;
            case "M+1":
            case "1+M":
                result = "1110111";
                break;
            case "D-1":
                result = "0001110";
                break;
            case "A-1":
                result = "0110010";
                break;
            case "M-1":
                result = "1110010";
                break;
            case "D+A":
            case "A+D":
                result = "0000010";
                break;
            case "D+M":
            case "M+D":
                result = "1000010";
                break;
            case "D-A":
                result = "0010011";
                break;
            case "D-M":
                result = "1010011";
                break;
            case "A-D":
                result = "0000111";
                break;
            case "M-D":
                result = "1000111";
                break;
            case "D&A":
            case "A&D":
                result = "0000000";
                break;
            case "D&M":
            case "M&D":
                result = "1000000";
                break;
            case "D|A":
            case "A|D":
                result = "0010101";
                break;
            case "D|M":
            case "M|D":
                result = "1010101";
                break;
            default:
                result = "";
        }
        return result;
    }

    // handle jump
    static String handleJump(String jump) {
        String result = "";
        switch(jump) {
            case "JGT":
                result = "001";
                break;
            case "JEQ":
                result = "010";
                break;
            case "JGE":
                result = "011";
                break;
            case "JLT":
                result = "100";
                break;
            case "JNE":
                result = "101";
                break;
            case "JLE":
                result = "110";
                break;
            case "JMP":
                result = "111";
                break;
            default:
                result = "000";
        }
        return result;
    }

    // translate C instruction
    static String translateC(String line) {
        String source = line.substring(0, line.length());
        String dest = "";
        String comp = "";
        String jump = "";
        int equalSignPosition = source.indexOf("=");
        int semiColonPosition = source.indexOf(";");
        if(equalSignPosition != -1) {
            // M = M + 1
            dest = handleDest( source.substring(0, equalSignPosition) );
            comp = handleComp( source.substring(equalSignPosition+1) );
            jump = "000";
        } else if(semiColonPosition != -1) {
            // 0 ; JMP
            comp = handleComp( source.substring(0, semiColonPosition) );
            dest = "000";
            jump = handleJump( source.substring(semiColonPosition+1) );
        } else {
            // A + 1
            // suppose to not have this case
        }
        return "111" + comp + dest + jump;
    }
}

