import java.io.IOException;
import java.io.File;
import java.util.Scanner;

public class Parser {
    
    private static Scanner scanner;
    private static String currentCommand;
    private static String symbol;
    private static String dest;
    private static String comp;
    private static String jump;

    public Parser(String input) {
        try {
            scanner = new Scanner(new File(input));
            System.out.println("Ready to parser...");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public static boolean hasMoreCommands() {
        return scanner.hasNext();
    }

    public static String purify(String input) {
        String source = "";
        source = input.replace(" ", "");
        if(source.indexOf("/") != -1) {
            source = source.substring(0, source.indexOf("/"));
        }
        return source;
    }

    public static void advance() {
        currentCommand = purify(scanner.nextLine());
    }

    public static void handle() {
        switch(commandType()) {
            case "A_Command":
                int index = currentCommand.indexOf("@");
                symbol = currentCommand.substring(index+1);
                break;
            case "C_Command":
                int equalSignPosition = currentCommand.indexOf("=");
                int semiColonPosition = currentCommand.indexOf(";");
                if(equalSignPosition != -1) {
                    dest = currentCommand.substring(0, equalSignPosition);
                    comp = currentCommand.substring(equalSignPosition+1);
                    jump = "000";
                } else if(semiColonPosition != -1) {
                    comp = currentCommand.substring(0, semiColonPosition);
                    jump = currentCommand.substring(semiColonPosition+1);
                    dest = "000";
                }
                break;
            case "L_Command":
                int start = 0;
                int end = currentCommand.indexOf(")");
                symbol = currentCommand.substring(start+1, end);
                break;
            default:
                break;
        }
        /* System.out.println("Symbol -> " + symbol +  ", Dest -> " + dest +  ", Comp -> " + comp + ", Jump -> " + jump);  */
    }

    public static String commandType() {
        if (currentCommand.startsWith("//") || currentCommand.isEmpty()) {
            return "N_Command";
        } else {
            if (currentCommand.indexOf("@") !=- 1) {
                return "A_Command";
            }
            else if (currentCommand.indexOf("(") != -1) {
                return "L_Command";
            }
            else {
                return "C_Command";        
            }
        }
    }

    public static String symbol() {
        return symbol;
    }

    public static String dest() {
        return dest;
    }

    public static String comp() {
        return comp;
    }

    public static String jump() {
        return jump;
    }
}


