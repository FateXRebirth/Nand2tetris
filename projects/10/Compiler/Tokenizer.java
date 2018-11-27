import java.io.*;
import java.util.regex.*;
import java.util.ArrayList;

public class Tokenizer {

    // Variable For Parsing
    static String stream;
    static String last;
    static String commentString;
    static boolean comment;
    static int pos;
    static ArrayList<String> TOKENS;

    // Parser
    static CompilationEngine engine;
    static FileReader fileReader;
    static BufferedReader bufferReader;

    // Variable For Token
    static ArrayList<String> KEYWORDS;
    static ArrayList<String> SYMBOLS;
    static String KEYWORD = "keyword";
    static String SYMBOL = "symbol";
    static String STRING_CONST = "stringConstant";
    static String INT_CONST = "integerConstant";
    static String IDENTIFIER = "identifier";

    public Tokenizer(File output) {
        engine = new CompilationEngine(output);
        final String[] keywords = { "class", "constructor", "function", "method", "field", "static", "var", "int", "char", "boolean", "void", "true", "false", "null", "this", "let", "do", "if", "else", "while", "return" };
        final String[] symbols = { "{", "}", "(", ")", "[", "]", ".", ",", ";", "+", "-", "*", "/", "&", "|", "<", ">", "=", "~" };
        KEYWORDS = new ArrayList<String>();
        SYMBOLS = new ArrayList<String>();
        for(String keyword : keywords) {
            KEYWORDS.add(keyword);
        }
        for(String symbol : symbols) {
            SYMBOLS.add(symbol);
        }
        TOKENS = new ArrayList<String>();
        stream = "";
        last = "";
        commentString = "";
        comment = false;
        pos = -1;
    }

    public void Open(File input) {
        try {
            fileReader = new FileReader(input);
            bufferReader = new BufferedReader(fileReader);
            int value = 0;
            // reads to the end of the stream
            while((value = bufferReader.read()) != -1) {
                // ignore space and \n
                if(value != 32) {
                    // converts int to character
                    char c = (char)value;
                    // prints character and its ascii code for debug
                    // System.out.println("char(" + c + ") -> int(" + value +")");
                    // save tokens
                    TOKENS.add(Character.toString(c));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Close() {
        engine.Close();
    }

    public boolean HasMoreTokens() {
        try {
            TOKENS.get(pos+1);
            pos = pos + 1;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void Advance() {
        last = TOKENS.get(pos);
        stream = stream + TOKENS.get(pos);
        if (GetToken(last).getType() == SYMBOL) {
            if(last.equals("/") || last.equals("*")) {
                commentString = commentString + last;
                if(commentString.equals("//") || commentString.equals("/*")) {
                    String c;
                    int index = 1;
                    comment = true;
                    while(comment) {
                        c = TOKENS.get(pos + index);
                        if(c.equals("/") || c.equals("\n")) {
                            pos = pos + index;
                            comment = false;
                        } else {
                            index = index + 1;
                        }
                    }
                }
            } else {
                if (stream.length() > 1) {
                    String subString = stream.substring(0, stream.length() - 1);
                    engine.Parser(GetToken(subString));
                }
                engine.Parser(GetToken(last));
                stream = "";
            }

        } else if(last.equals("\n")) {
            stream = "";
        } else {
            if(GetToken(stream).getType() == SYMBOL || GetToken(stream).getType() == KEYWORD) {
                engine.Parser(GetToken(stream));
                stream = "";
            }
        }
    }

    private Token GetToken(String value) {
        if(KEYWORDS.contains(value)) {
            return new Token(KEYWORD, value);
        } else if(SYMBOLS.contains(value)) {
            if(value.equals("<")) {
                return new Token(SYMBOL, "&lt;");
            } else if(value.equals(">")) {
                return new Token(SYMBOL, "&gt;");
            } else if(value.equals("\"")) {
                return new Token(SYMBOL, "&quot;");
            } else if(value.equals("&")) {
                return new Token(SYMBOL, "&amp;");
            } else {
                return new Token(SYMBOL, value);
            }
        } else if(Pattern.matches("^\".+\"$", value)) {
            return new Token(STRING_CONST, value.substring(1, value.length()-1));
        } else if(Pattern.matches("^[0-9]{1,5}$", value) && Integer.valueOf(value) >= 0 && Integer.valueOf(value) <= 32767) {
            return new Token(INT_CONST, value);
        } else {
            return new Token(IDENTIFIER, value);
        }
    }
}