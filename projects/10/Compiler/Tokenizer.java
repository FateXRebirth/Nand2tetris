import java.io.*;
import java.util.regex.*;
import java.util.ArrayList;

public class Tokenizer {

  static String stream;
  static int pos;
  static ArrayList<String> TOKENS;

  static CompilationEngine engine;
  static FileReader fileReader;
  static BufferedReader bufferReader;

  static ArrayList<String> KEYWORDS;
  static ArrayList<String> SYMBOLS;

  static String KEYWORD = "keyword";
  static String SYMBOL = "symbol";
  static String STRING_CONST = "stringConstant";
  static String INT_CONST = "integerConstant";
  static String IDENTIFIER = "identifier";
  static String SPACE = "Space";
  static String UNKNOWN = "Unknown";

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
    pos = 0;
    TOKENS = new ArrayList<String>();
  }

  public void Open(File input) {
    try { 
      fileReader = new FileReader(input);
      bufferReader = new BufferedReader(fileReader);
      int value = 0;
      // reads to the end of the stream 
      while((value = bufferReader.read()) != -1) {
        
        // ignore space and \n
        if(value != 10 && value != 32) {
          // converts int to character
          char c = (char)value;
          // prints character and its ascii code for debug
          // System.out.println("char(" + c + ") -> int(" + value +")");
          // save tokens
          TOKENS.add(Character.toString(c));
        }
      }
      // for(String x : TOKENS) {
      //   System.out.println(x);
      // }
    } catch (IOException e) {
      e.printStackTrace();
    }      
  }
  
  public void Close() {
    engine.Close();
  }

  public boolean HasMoreTokens() {
   try {
     TOKENS.get(pos);
     return true;
   } catch (Exception e) {
     return false;
   }
  }

  public void Advance() {
    engine.Write(TOKENS.get(pos++));
  }

  private String TokenType(String value) {
    if(KEYWORDS.contains(value)) {
      return KEYWORD;
    } else if(SYMBOLS.contains(value)) {
      return SYMBOL;
    } else if(Pattern.matches("^\"[a-zA-Z]+\"$", value)) {
      return STRING_CONST;
    } else if(Pattern.matches("^[0-9]{1,5}$", value) && Integer.valueOf(value) >= 0 && Integer.valueOf(value) <= 32767) {
      return INT_CONST;
    } else {
      return UNKNOWN;
    }
  }

  private String RemoveIndent(String stream) {
    int index = 0;
    for(int i = 0; i < stream.length(); i++) {
      if(stream.charAt(i) != ' ') {
        index = i;
        break;
      }
    }
    return stream.substring(index);
  }

  private String RemoveComment(String stream) {
    int index = stream.indexOf("/");
    if(index == -1) return stream;
    return stream.substring(0, index);
  }
  
}