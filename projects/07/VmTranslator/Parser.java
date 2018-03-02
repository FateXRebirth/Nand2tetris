import java.io.*;

public class Parser {

    private static FileReader fileReader;
    private static BufferedReader bufferReader;
    private static String hasNext;
    private static String currentCommand;
    private static String type;
    private static String arg1;
    private static int arg2;

    public Parser(File input) {
        try {
            fileReader = new FileReader(input);
            bufferReader = new BufferedReader(fileReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean HasMoreCommands() {
        try {
            hasNext = bufferReader.readLine();
            if( hasNext == null) {
                bufferReader.close();
                fileReader.close();
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }    
        return true;
    }

    public static void Advance() {
        currentCommand = hasNext;
    }

    public static String CommandType() {
        if (currentCommand.startsWith("//") || currentCommand.isEmpty()) {
            return "NULL";
        } else {
            if (currentCommand.indexOf("push") !=- 1) {
                return "C_PUSH";
            }
            else if (currentCommand.indexOf("pop") != -1) {
                return "C_POP";
            }
            else if (currentCommand.indexOf("label") != -1) {
                return "C_LABEL";
            }
            else if (currentCommand.indexOf("goto") != -1) {
                return "C_GOTO";
            }
            else if (currentCommand.indexOf("if") != -1) {
                return "C_IF";
            }
            else if (currentCommand.indexOf("function") != -1) {
                return "C_FUNCTION";
            }
            else if (currentCommand.indexOf("return") != -1) {
                return "C_RETURN";
            }
            else if (currentCommand.indexOf("call") != -1) {
                return "C_CALL";
            }
            else {
                return "C_ARITHMETIC";        
            }
        }
    
}
    public static String Type() {
        return type;
    }

    public static String Arg1() {
        return arg1;
    }

    public static int Arg2() {
        return arg2;
    }

    public static void Handle() {
      String[] lex = removeComment(currentCommand).split(" ");
      if(lex.length == 1) {
        type = lex[0];
      } else if (lex.length == 2) {
        type = lex[0];
        arg1 = lex[1];
      } else {
        type = lex[0];
        arg1 = lex[1];
        arg2 = Integer.valueOf(lex[2]);
      }
    }

    public static String removeComment(String input) {
        if(input.indexOf("/") != -1) {
            return input.substring(0, input.indexOf("/"));    
        }
        return input;
    }

    // Debugger 
    public static String GetCurrentCommand() {
        return currentCommand;
    }
}