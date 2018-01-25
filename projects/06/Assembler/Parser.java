import java.io.IOException;
import java.io.File;
import java.util.Scanner;

public class Parser {

    private static String currentCommand;
    private static Scanner scanner;
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

    public static void advance() {
        scanner.nextLine();
    }

    public static void handle() {
        String result = "";
        result = currentCommand.replace(" ", "");
        if(result.indexOf("/") != -1) {
            result = result.substring(0, result.indexOf("/"));
        }

        switch(commandType()) {
            case "A_Command":
                symbol = "";
                break;
            case "C_Command":
                dest = "";
                comp = "";
                jump = "";
                break;
            case "L_Commnad":
                symbol = "";
                break;
            default:
                break;
        }
    }

    public static String commandType() {
        if (currentCommand.indexOf("@") !=- 1) {
            return "A_Command";
        }
        else if (currentCommand.indexOf("(") != -1) {
            return "L_Command";
        }
        else if (currentCommand.startsWith("//") || currentCommand.isEmpty()) {
            return "N_Command";        
        }
        else {
            return "C_Command";
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


