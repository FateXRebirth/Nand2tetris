import java.io.*;
import java.util.*;

public class CodeWriter {

	public static class CodeGenerator {

		public void Push_Command(String segment, int index) {
			if ( (segment.equals("static")) || (segment.equals("constant"))
				|| (segment.equals("temp")) || (segment.equals("pointer"))  ) {
				
				switch(segment) {
					case "static":
						code.add("@" + String.valueOf( 16 + index));
						code.add("D=M");
						break;
					case "constant":
						code.add("@" + String.valueOf(index));
						code.add("D=A");
						break;
					case "temp":
						code.add("@R5");
						code.add("D=M");
						code.add("@" + String.valueOf( 5 + index));
						code.add("A=D+A");
						code.add("D=M");
						break;
					case "pointer":
						if (index == 0) {
							code.add("@THIS");
						} else {
							code.add("@THAT");
						}
						code.add("D=M");
						break;
					default:
						System.out.println("Push_Command Error");
						break;
				}

			} else if ( (segment.equals("local")) || (segment.equals("argument")) 
				|| (segment.equals("this")) || (segment.equals("that"))) {
				
				switch(segment) {
					case "local":
						code.add("@LCL");
						break;
					case "argument":
						code.add("@ARG");
						break;
					case "this":
						code.add("@THIS");
						break;
					case "that":
						code.add("@THAT");
						break;
					default:
						System.out.println("Push_Command Error");
						break;
				}

				code.add("D=M");
				code.add("@" + String.valueOf(index));
				code.add("A=D+A");
				code.add("D=M");
			} 	

			code.add("@SP");
			code.add("A=M");
			code.add("M=D");
			code.add("@SP");
			code.add("M=M+1");
		}

		public void Pop_Command(String segment, int index) {
			if ( (segment.equals("static")) || (segment.equals("constant"))
				|| (segment.equals("temp")) || (segment.equals("pointer"))  ) {
				
				switch(segment) {
					case "static":
						code.add("@" + String.valueOf(16 + index));
						code.add("D=A");
						break;
					case "constant":
						System.out.println("Shouldn't have this case");
						break;
					case "temp":
						code.add("@R5");
						code.add("D=M");
						code.add("@" + String.valueOf( 5 + index));
						code.add("D=D+A");
						break;
					case "pointer":
						if (index == 0) {
							code.add("@THIS");
						} else {
							code.add("@THAT");
						}
						code.add("D=A");
						break;
					default:
						System.out.println("Push_Command Error");
						break;
				}
				

			} else if ( (segment.equals("local")) || (segment.equals("argument")) 
				|| (segment.equals("this")) || (segment.equals("that"))) {

				switch(segment) {
					case "local":
						code.add("@LCL");
						break;
					case "argument":
						code.add("@ARG");
						break;
					case "this":
						code.add("@THIS");
						break;
					case "that":
						code.add("@THAT");
						break;
					default:
						System.out.println("Push_Command Error");
						break;
				}
				
				code.add("D=M");
				code.add("@"+ String.valueOf(index));
				code.add("D=D+A");
			}

			code.add("@R13");
			code.add("M=D");
			code.add("@SP");
			code.add("AM=M-1");
			code.add("D=M");
			code.add("@R13");
			code.add("A=M");
			code.add("M=D");
		}

		public void Add_Sub_And_Or_Command(String type) {
			code.add("@SP");
			code.add("AM=M-1");
			code.add("D=M");
			code.add("A=A-1");
			switch(type) {
				case "Add":
					code.add("M=M+D");
					break;
				case "Sub":
					code.add("M=M-D");
					break;
				case "And":
					code.add("M=M&D");
					break;
				case "Or":
					code.add("M=M|D");
					break;
				default:
					System.out.println("Add_Sub_And_Or_Command Error");
					break;
			}
		}

		public void Neg_Not_Command(String type) {
			code.add("@SP");
			code.add("A=M-1");
			switch(type) {
				case "Not":
					code.add("M=!M");
					break;
				case "Neg":
					code.add("M=-M");
					break;
				default:
					System.out.println("Neg_Not_Command Error");
					break;
			}
		}

		public void Compare_Command(String type) {
			code.add("@SP");
			code.add("AM=M-1");
			code.add("D=M");
			code.add("A=A-1");
			code.add("D=M-D");
			code.add(CreateFalseStart(labelCounter));
			switch(type) {
				case "Eq":
					code.add("D;JNE");
					break;
				case "Gt":
					code.add("D;JLE");
					break;
				case "Lt":
					code.add("D;JGE");
					break;
				default:
					System.out.println("Compare_Command Error");
					break;
			}
			code.add("@SP");
			code.add("A=M-1");
			code.add("M=-1");
			code.add(CreateContinueStart(labelCounter));
			code.add("0;JMP");
			code.add(CreateFalseEnd(labelCounter));
			code.add("@SP");
			code.add("A=M-1");
			code.add("M=0");
			code.add(CreateContinueEnd(labelCounter));
			labelCounter = labelCounter + 1;
		}

		public String CreateFalseStart(int index) {
			return "@" + "FALSE" + String.valueOf(index);
		}

		public String CreateFalseEnd(int index) {
			return "(" + "FALSE" + String.valueOf(index) + ")";
		}

		public String CreateContinueStart(int index) {
			return "@" + "CONTINUE" + String.valueOf(index);
		}

		public String CreateContinueEnd(int index) {
			return "(" + "CONTINUE" + String.valueOf(index) + ")";
		}
	}

	private static CodeGenerator codeGenerator;
	private static FileWriter fileWriter;
	private static BufferedWriter bufferWriter;
	private static int labelCounter;
    private static ArrayList<String> code;
	

	public CodeWriter(String output) {
		try {
			codeGenerator = new CodeGenerator();
			code = new ArrayList<String>();
			labelCounter = 0;
            fileWriter = new FileWriter(output);
            bufferWriter = new BufferedWriter(fileWriter);          
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}

	public static void WriteArithmetic(String command) {
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
				System.out.println("WriteArithmetic Error");
				break;
		}
		Write();
	}

	public static void WritePushPop(String command, String segment, int index) {
		switch(command) {
			case "push":
				codeGenerator.Push_Command(segment, index);
				break;
			case "pop":
				codeGenerator.Pop_Command(segment, index);
				break;
			default:
				System.out.println("WritePushPop Error");
				break;
		}
		Write();
	}

	public static void Write() {
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

	public static void Close() {
		try {
			labelCounter = 0;
			bufferWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Debugger 
	public static void PrintOutCommand(String command) {
		try {
			bufferWriter.newLine();
			bufferWriter.write("--------");
			bufferWriter.write(command);
			bufferWriter.write("--------");
			bufferWriter.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}