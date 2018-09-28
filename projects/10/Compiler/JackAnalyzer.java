import java.io.File;
import java.util.ArrayList;

class JackAnalyzer {

  public static void main(String[] args) {
        
    // check if user provides argument to handle
    if(args.length < 1) {
      throw new IllegalArgumentException("Missing argument! (e.g. java Tokenizer Test.jack) ");
    }

    File fileIn = new File(args[0]);
    String fileOutPath = "";
    ArrayList<File> files = new ArrayList<File>();

    if( fileIn.isFile() ) {
      String path = fileIn.getAbsolutePath();
      if( !GetExtension(path).equals(".jack") ) {
        throw new IllegalArgumentException(".jack file is required!");
      }
      files.add(fileIn);
      fileOutPath = fileIn.getAbsolutePath().substring(0, fileIn.getAbsolutePath().lastIndexOf(".")) + ".xml";
    } else if( fileIn.isDirectory() ) {
      files = GetFiles(fileIn);
      if( files.size() == 0 ) {
          throw new IllegalArgumentException("No jack file in this directory!");
      }
      fileOutPath = fileIn.getAbsolutePath() + "/" +  fileIn.getName() + ".xml";
    }

    File fileOut = new File(fileOutPath);

    Tokenizer tokenizer = new Tokenizer(fileOut);

    for( File file : files ) {
      tokenizer.Open(file);
      while(tokenizer.HasMoreTokens()) {
        tokenizer.Handle();
      }
    }

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

  public static ArrayList<File> GetFiles(File dir){
    File[] files = dir.listFiles();
    ArrayList<File> result = new ArrayList<File>();
    for ( File file : files ){
        if ( file.getName().endsWith(".jack") ){
            result.add(file);
        }
    }
    return result;
  }

}