public class Code {

    public static String Dest(String dest) {
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
    
    public static String Comp(String comp) {
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

    public static String Jump(String jump) {
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
}
