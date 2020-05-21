import java.io.File;
import java.util.ArrayList;

public class JackCompiler {

    public static void main(String[] args) {

        // check if user provides argument to handle
        if(args.length < 1) {
            throw new IllegalArgumentException("Missing argument! (e.g. java JackAnalyzer Test.jack) ");
        }

        File fileIn = new File(args[0]);
        File fileOut, TokenfileOut;
        String fileOutPath, TokenfileOutPath;
        ArrayList<File> files = new ArrayList<File>();

        if( fileIn.isFile() ) {
            // if this input is file
            String path = fileIn.getAbsolutePath();
            if( !GetExtension(path).equals(".jack") ) {
                throw new IllegalArgumentException(".jack file is required!");
            }
            files.add(fileIn);
        } else if( fileIn.isDirectory() ) {
            // if this input is directory
            files = GetFiles(fileIn);
            if( files.size() == 0 ) {
                throw new IllegalArgumentException("No jack file in this directory!");
            }
        }

        for( File file : files ) {
            fileOutPath = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(".")) + ".xml";
            TokenfileOutPath = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(".")) + "T.xml";
            fileOut = new File(fileOutPath);
            TokenfileOut = new File(TokenfileOutPath);
            CompilationEngine compilationEngine = new CompilationEngine(file, fileOut, TokenfileOut);
            compilationEngine.CompileClass();
        }
    }

    // Get File's Extension
    public static String GetExtension(String fileName){
        int index = fileName.lastIndexOf('.');
        if (index != -1 ){
            return fileName.substring(index);
        } else {
            return "";
        }
    }
    // Get Files in Directory
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