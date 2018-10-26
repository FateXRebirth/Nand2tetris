import java.io.*;
import java.util.Hashtable;

public class Tokenizer {

  static String token;
  static FileReader fileReader;
  static BufferedReader bufferReader;
  static FileWriter fileWriter;
  static BufferedWriter bufferWriter;
  static Hashtable<String, String[]> tokenTable;

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
  }

  public void Handle() {
    String[] input = DealIndent(DealComment(token)).split(" ");
    if(input.length == 0) return;
    Token[] tokens = GetTokens(input);
    try {
      bufferWriter.write("<tokens>");
      bufferWriter.newLine();
      for( Token token : tokens) {
        bufferWriter.write(token.XmlFormat());
        bufferWriter.newLine();
      }   
      bufferWriter.write("</tokens>");
      bufferWriter.newLine();
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }

  private Token[] GetTokens(String[] input) {
    Token[] tokens = new Token[input.length];
    for (int i = 0; i < input.length; i++) {
      tokens[i] = new Token(TokenTypes(input[i]), input[i]);
    }
    return tokens;
  }

  private String TokenTypes(String input) {
    return "token";
  }
  

  private String DealIndent(String token) {
    int index = 0;
    for(int i = 0; i < token.length(); i++) {
      if(token.charAt(i) != ' ') {
        index = i;
        break;
      }
    }
    return token.substring(index);
  }

  private String DealComment(String token) {
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