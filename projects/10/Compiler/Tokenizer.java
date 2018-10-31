import java.io.*;
import java.util.regex.*;
import java.util.Hashtable;
import java.util.ArrayList;

public class Tokenizer {

  static String token;
  static FileReader fileReader;
  static BufferedReader bufferReader;
  static FileWriter fileWriter;
  static BufferedWriter bufferWriter;
  static Hashtable<String, String[]> tokenTable;
  static ArrayList<String> KEYWORDS;
  static ArrayList<String> SYMBOLS;
  static ArrayList<Token> tokens;

  static String KEYWORD = "KEYWORD";
  static String SYMBOL = "SYMBOL";
  static String STRING_CONST = "STRING_CONST";
  static String INT_CONST = "INT_CONST";
  static String IDENTIFIER = "IDENTIFIER";
  static String UNKNOWN = "UNKNOWN";

  public Tokenizer(File output) {
    try {
      fileWriter = new FileWriter(output);
      bufferWriter = new BufferedWriter(fileWriter);
      Initialize();
    } catch (IOException e) {
        e.printStackTrace();
    }
  }

  private void Initialize() {
    tokenTable = new Hashtable<String, String[]>();
    final String[] keywords = { "class", "constructor", "function", "method", "field", "static", "var", "int", "char", "boolean", "void", "true", "false", "null", "this", "let", "do", "if", "else", "while", "return" };
    tokenTable.put("keyword", keywords);
    final String[] symbols = { "{", "}", "(", ")", "[", "]", ".", ",", ";", "+", "-", "*", "/", "&", "|", "<", ">", "=", "~" };
    tokenTable.put("symbol", symbols);

    KEYWORDS = new ArrayList<String>();
    SYMBOLS = new ArrayList<String>();

    for(String keyword : keywords) {
      KEYWORDS.add(keyword);
    }
    for(String symbol : symbols) {
      SYMBOLS.add(symbol);
    }
  }

  public void Handle() {
    String[] input = RemoveIndent(RemoveComment(token)).split(" ");
    if(input.length == 0) return;
    Recursion(input);
    // Token[] tokens = GetTokens(input);
    try {
      // bufferWriter.write("<tokens>");
      // bufferWriter.newLine();
      for( Token token : tokens) {
        bufferWriter.write(token.XmlFormat());
        bufferWriter.newLine();
      }   
      // bufferWriter.write("</tokens>");
      // bufferWriter.newLine();
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }

  private void Recursion(String[] input) {
    tokens = new ArrayList<Token>();
    String Stage;
    String Type;
    for(String str : input) {
      Type = TokenTypes(str);
      if(Type == UNKNOWN) {
        tokens.add(new Token(Type, str));
        // for(int i = 0; i < str.length(); i++) {
        //   Stage = Character.toString(str.charAt(i));
        // }
      } else {
        tokens.add(new Token(Type, str));
      }
    }
  }

  private Token[] GetTokens(String[] input) {
    Token[] tokens = new Token[input.length];
    for (int i = 0; i < input.length; i++) {
      tokens[i] = new Token(TokenTypes(input[i]), input[i]);
    }
    return tokens;
  }

  private String TokenTypes(String token) {
    if(KEYWORDS.contains(token)) {
      return KEYWORD;
    } else if(SYMBOLS.contains(token)) {
      return SYMBOL;
    } else if(Pattern.matches("^[a-zA-Z]+$", token)) {
      return STRING_CONST;
    } else if(Pattern.matches("^[0-9]{1,5}$", token)) {
      if(Integer.valueOf(token) >= 0 && Integer.valueOf(token) <= 32767) {
        return INT_CONST;
      } else {
        return UNKNOWN;
      }
    } else {
      return UNKNOWN;
    }
  }
  
  private String RemoveIndent(String token) {
    int index = 0;
    for(int i = 0; i < token.length(); i++) {
      if(token.charAt(i) != ' ') {
        index = i;
        break;
      }
    }
    return token.substring(index);
  }

  private String RemoveComment(String token) {
    int index = token.indexOf("/");
    if(index == -1) return token;
    return token.substring(0, index);
  }

  public boolean HasMoreTokens() {
    try {
      token = bufferReader.readLine();
      if( token == null) {
          bufferReader.close();
          fileReader.close();
          bufferWriter.write("</tokens>");
          bufferWriter.newLine();
          return false;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }    
    return true;
  }

  public void Open(File input) {
    try { 
      fileReader = new FileReader(input);
      bufferReader = new BufferedReader(fileReader);
      bufferWriter.write("<tokens>");
      bufferWriter.newLine();
    } catch (IOException e) {
      e.printStackTrace();
    }      
  }

  public void Close() {
    try { 
      bufferWriter.close();
      fileWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }   
  }

  @Override
  public void finalize() {
    System.out.println("Is This Destructor?");
  }
  
}