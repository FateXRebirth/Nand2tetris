import java.io.*;
import java.util.*;

public class CodeWriter {

	public static class CodeGenerator {

		public void Push_Command(String segment, int index) {
			code.add("@"+ String.valueOf(index));
			code.add("D=A");
			code.add("@SP");
			code.add("A=M");
			code.add("M=D");
			code.add("@SP");
			code.add("M=M+1");
		}

		public void Pop_Command(String segment, int index) {
			code.add("@SP");
			code.add("M=M-1");
			code.add("@SP");
			code.add("A=M");
			code.add("D=M");
			code.add("@"+ String.valueOf(index));
			code.add("M=D");
		}
	}

	private static CodeGenerator codeGenerator;
	private static FileWriter fileWriter;
	private static BufferedWriter bufferWriter;

	// Store generated code 
    private static ArrayList<String> code;
	

	public CodeWriter(String output) {
		try {
			codeGenerator = new CodeGenerator();
			code = new ArrayList<String>();
            fileWriter = new FileWriter(output);
            bufferWriter = new BufferedWriter(fileWriter);          
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}
	
	public static void writeArithmetic(String command) {
		write();
	}

	public static void writePushPop(String command, String segment, int index) {
		switch(command) {
			case "push":
				codeGenerator.Push_Command(segment, index);
				break;
			case "pop":
				codeGenerator.Pop_Command(segment, index);
				break;
			default:
				System.out.println("Invalid type");
				break;
		}
		write();
	}

	public static void write() {
		try {
			for(String content : code) {
				bufferWriter.write(content);
				bufferWriter.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		code.clear();
	}

	public static void close() {
		try {
			bufferWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}