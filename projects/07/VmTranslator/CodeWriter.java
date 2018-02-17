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
			code.add("M=0");
			code.add("@"+ String.valueOf(index));
			code.add("M=D");
		}

		public void Add_Sub_And_Or_Command(String type) {
			code.add("@SP");
			code.add("M=M-1");
			code.add("@SP");
			code.add("A=M");
			code.add("D=M");
			code.add("@SP");
			code.add("M=M-1");
			code.add("@SP");
			code.add("A=M");
			code.add("A=M");
			switch(type) {
				case "Add":
					code.add("D=A+D");
					break;
				case "Sub":
					code.add("D=A-D");
					break;
				case "And":
					code.add("D=D&A");
					break;
				case "Or":
					code.add("D=D|A");
					break;
				default:
					System.out.println("Add_Sub_And_Or_Command Error");
					break;
			}
			code.add("@SP");
			code.add("A=M");
			code.add("M=D");
			code.add("@SP");
			code.add("M=M+1");
		}

		public void Neg_Not_Command(String type) {
			code.add("@SP");
			code.add("M=M-1");
			code.add("@SP");
			code.add("A=M");
			code.add("D=M");
			switch(type) {
				case "Not":
					code.add("D=!D");
					break;
				case "Neg":
					code.add("D=-D");
					break;
				default:
					System.out.println("Neg_Not_Command Error");
					break;
			}
			code.add("@SP");
			code.add("A=M");
			code.add("M=D");
			code.add("@SP");
			code.add("M=M+1");
		}

		public void Compare_Command(String type) {

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
		switch(command) {
			case "add":
				codeGenerator.Add_Sub_And_Or_Command("Add");
				break;
			case "sub":
				codeGenerator.Add_Sub_And_Or_Command("Sub");
				break;
			case "neg":
				codeGenerator.Neg_Not_Command("Neg");
				break;
			case "eq":
				codeGenerator.Compare_Command("Eq");
				break;
			case "gt":
				codeGenerator.Compare_Command("Gt");
				break;
			case "lt":
				codeGenerator.Compare_Command("Lt");
				break;
			case "and":
				codeGenerator.Add_Sub_And_Or_Command("And");
				break;
			case "or":
				codeGenerator.Add_Sub_And_Or_Command("Or");
				break;
			case "not":
				codeGenerator.Neg_Not_Command("Not");
				break;
			default:
				System.out.println("writeArithmetic Error");
				break;
		}
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
				System.out.println("writePushPop Error");
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