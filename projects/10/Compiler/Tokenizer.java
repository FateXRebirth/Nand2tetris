import java.io.*;
import java.util.regex.*;
import java.util.Hashtable;
import java.util.ArrayList;

public class Tokenizer {

  static String stream;
  static Parser parser;
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
    parser = new Parser(output);   
  }

  public void Open(File input) {
    try { 
      fileReader = new FileReader(input);
      bufferReader = new BufferedReader(fileReader);
    } catch (IOException e) {
      e.printStackTrace();
    }      
  }

  public boolean HasMoreTokens() {
    try {
      stream = bufferReader.readLine();
      if( stream == null) {
          bufferReader.close();
          fileReader.close();
          return false;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }    
    return true;
  }

  public void Handle() {
    String content = RemoveIndent(RemoveComment(stream));
    System.out.println(content);
  }

  public void Close() {
    parser.Close();
  }

  @Override
  public void finalize() {
    System.out.println("Is This Destructor?");
  }

  private void Initialize() {
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
  }

  private String GetTokenType(String value) {
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