import java.io.*;

public class VmTranslator {

	public static void main(String[] args) {
		
		// check if user provides argument to handle
        if(args.length < 1) {
            throw new IllegalArgumentException("Missing argument! (e.g. java VmTranslator SimpleAdd.vm) ");
        }
        
        // Debugger
        boolean debug = false;
        if(args.length == 2) debug = true;

        // get input filename
        String input = args[0];
        // set output filename
        String output = input.replace(".vm", ".asm");

        Parser parser = new Parser(input);
        CodeWriter codewriter = new CodeWriter(output);

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

        codewriter.Close();
	}
}