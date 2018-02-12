import java.io.*;

public class Parser {

	private static FileReader fileReader;
    private static BufferedReader bufferReader;
    private static String hasNext;
    private static String currentCommand;
	private static String type;
	private static String arg1;
	private static int arg2;

	public Parser(String input) {
        try {
            fileReader = new FileReader(input);
            bufferReader = new BufferedReader(fileReader);
        } catch (IOException e) {
			e.printStackTrace();
		}
    }

    public static boolean hasMoreCommands() {
        boolean has = true;
        try {
            hasNext = bufferReader.readLine();
            if( hasNext == null) {
                bufferReader.close();
                fileReader.close();
                has = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }    
        return has;
    }

    public static void advance() {
        currentCommand = hasNext;
    }

    public static String commandType() {
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

    public static String arg1() {
        return "";
    }

    public static int arg2() {
        return 0;
    }

    public static void handle() {
      
    }
}