import java.io.*;
import java.util.ArrayList;

public class VmTranslator {

    public static void main(String[] args) {
        
        // check if user provides argument to handle
        if(args.length < 1) {
            throw new IllegalArgumentException("Missing argument! (e.g. java VmTranslator SimpleAdd.vm) ");
        }
        
        // Debugger
        boolean debug = false;
        if(args.length == 2) debug = true;

        File fileIn = new File(args[0]);
        String fileOutPath = "";
        File fileOut;
        ArrayList<File> files = new ArrayList<File>();

        if( fileIn.isFile() ) {
            String path = fileIn.getAbsolutePath();
            if( !GetExtension(path).equals(".vm") ) {
                throw new IllegalArgumentException(".vm file is required!");
            }
            files.add(fileIn);
            fileOutPath = fileIn.getAbsolutePath().substring(0, fileIn.getAbsolutePath().lastIndexOf(".")) + ".asm";
        } else if( fileIn.isDirectory() ) {
            files = GetFiles(fileIn);
            if( files.size() == 0 ) {
                throw new IllegalArgumentException("No vm file in this directory!");
            }
            fileOutPath = fileIn.getAbsolutePath() + "/" +  fileIn.getName() + ".asm";
        }

        fileOut = new File(fileOutPath);
        CodeWriter codewriter = new CodeWriter(fileOut);

        for( File file : files ) {
            if(file.getName().equals("Sys.vm")) {
                codewriter.WriteInit();
            }
        }
        
        for( File file : files ) {
            Parser parser = new Parser(file);
            codewriter.SetFileName(file.getName());
            while(parser.HasMoreCommands()) {
                parser.Advance();
                String type = parser.CommandType();
                if(type != "NULL") {
                    parser.Handle();
                    if(debug) codewriter.PrintOutCommand(parser.GetCurrentCommand());
                    switch(type) {
                        case "C_PUSH":                    
                            codewriter.WritePushPop(parser.Type(), parser.Arg1(), parser.Arg2());
                            break;
                        case "C_POP":
                            codewriter.WritePushPop(parser.Type(), parser.Arg1(), parser.Arg2());
                            break;
                        case "C_LABEL":
                            codewriter.WriteLabel(parser.Arg1());
                            break;
                        case "C_GOTO":
                            codewriter.WriteGoto(parser.Arg1());
                            break;
                        case "C_IF":
                            codewriter.WriteIf(parser.Arg1());
                            break;
                        case "C_FUNCTION":
                            codewriter.WriteFunction(parser.Arg1(), parser.Arg2());
                            break;
                        case "C_RETURN":
                            codewriter.WriteReturn();
                            break;
                        case "C_CALL":
                            codewriter.WriteCall(parser.Arg1(), parser.Arg2());
                            break;
                        case "C_ARITHMETIC":
                            codewriter.WriteArithmetic(parser.Type());
                            break;
                        default:
                            System.out.println("Unknown type");
                            break;
                    }
                }
            }
        }
        
        codewriter.Close();
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
            if ( file.getName().endsWith(".vm") ){
                result.add(file);
            }
        }
        return result;
    }
}