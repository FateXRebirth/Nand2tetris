import java.io.*;

public class Tokenizer {

  static String token;
  static FileReader fileReader;
  static BufferedReader bufferReader;
  static FileWriter fileWriter;
  static BufferedWriter bufferWriter;

  public Tokenizer(File output) {
    try {
      fileWriter = new FileWriter(output);
      bufferWriter = new BufferedWriter(fileWriter);
    } catch (IOException e) {
        e.printStackTrace();
    }
  }

  public void Handle() {
    try {
      bufferWriter.write(token);
      bufferWriter.newLine();
    } catch (IOException e) {
      e.printStackTrace();
    } 
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