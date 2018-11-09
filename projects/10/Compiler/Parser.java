import java.io.*;

public class Parser {

  static FileWriter fileWriter;
  static BufferedWriter bufferWriter;

  public Parser(File output) {
    try {
      fileWriter = new FileWriter(output);
      bufferWriter = new BufferedWriter(fileWriter);
    } catch (IOException e) {
        e.printStackTrace();
    }
  }
  
  public void Write(String content) {
    try {
      bufferWriter.write(content);
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
}