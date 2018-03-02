import java.io.*;
import java.util.ArrayList;

public class VmTranslator2 {

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
        } 

        fileOut = new File(fileOutPath);
        CodeWriter codewriter = new CodeWriter(fileOut);
        
        for( File file : files ) {
            Parser parser = new Parser(file);
            while(parser.HasMoreCommands()) {
                parser.Advance();
                String type = parser.CommandType();
                if(type != "NULL") {
                    if(type == "C_PUSH" || type == "C_POP") {
                        parser.Handle();
                        if(debug) codewriter.PrintOutCommand(parser.GetCurrentCommand());
                        codewriter.WritePushPop(parser.Type(), parser.Arg1(), parser.Arg2());
                    } else {
                        parser.Handle();
                        if(debug) codewriter.PrintOutCommand(parser.GetCurrentCommand());
                        codewriter.WriteArithmetic(parser.Type());
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
        }else {
            return "";
        }
    }

}