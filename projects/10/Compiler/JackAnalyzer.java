import java.io.File;

class JackAnalyzer {

  public static void main(String[] args) {
        
    // check if user provides argument to handle
    if(args.length < 1) {
      throw new IllegalArgumentException("Missing argument! (e.g. java Tokenizer Test.jack) ");
    }

    File fileIn = new File(args[0]);

    String fileOutPath = "";

    if( fileIn.isFile() ) {
      String path = fileIn.getAbsolutePath();
      if( !GetExtension(path).equals(".jack") ) {
        throw new IllegalArgumentException(".jack file is required!");
      }
      fileOutPath = fileIn.getAbsolutePath().substring(0, fileIn.getAbsolutePath().lastIndexOf(".")) + ".xml";
    } 

    File fileOut = new File(fileOutPath);

    Tokenizer tokenizer = new Tokenizer(fileIn, fileOut);

    tokenizer.Close();
    
  }

  public static String GetExtension(String fileName){
    int index = fileName.lastIndexOf('.');
    if (index != -1 ){
      return fileName.substring(index);
    } else {
      return "";
    }
  }

}