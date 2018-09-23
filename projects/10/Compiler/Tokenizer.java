import java.io.*;

public class Tokenizer {

  static String hasNext;
  static FileReader fileReader;
  static BufferedReader bufferReader;
  static FileWriter fileWriter;
  static BufferedWriter bufferWriter;

  public Tokenizer(File input, File output) {
    try {
      fileReader = new FileReader(input);
      bufferReader = new BufferedReader(fileReader);
      fileWriter = new FileWriter(output);
      bufferWriter = new BufferedWriter(fileWriter);
    } catch (IOException e) {
        e.printStackTrace();
    }
    Test();
  }

  void Test() {
    while(HasMoreCommands()) {
      try {
        bufferWriter.write(hasNext);
        bufferWriter.newLine();
      } catch (IOException e) {
        e.printStackTrace();
      } 
    }
  }

  private static boolean HasMoreCommands() {
    try {
      hasNext = bufferReader.readLine();
      if( hasNext == null) {
          bufferReader.close();
          fileReader.close();
          return false;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }    
    return true;
  }

  public static void Close() {
    try { 
      bufferWriter.close();
      fileWriter.close();
      // bufferReader.close();
      // fileReader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }   
  }
  
}