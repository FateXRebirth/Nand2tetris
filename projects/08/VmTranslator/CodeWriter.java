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


		public void Label_Command(String label) {
			code.add("(" + GetFileName() + "$" + label + ")");
		}

		public void If_Goto_Command(String label) {
			code.add("@SP");
			code.add("AM=M-1");
			code.add("D=M");
			code.add("A=A-1");
			code.add("@" + GetFileName() + "$" + label);
			code.add("D;JNE");
		}

		public void Goto_Command(String label) {
			code.add("@" + GetFileName() + "$" + label);
			code.add("0;JMP");
		}

		public void Call_Command(String functionName, int Args) {
			
		}

		public void Function_Command(String functionName, int locals) {
			code.add("(" + functionName + ")");
			for(int i = 0 ; i < locals ; i++) {
				code.add("@0");
				code.add("D=A");
				code.add("@SP");
				code.add("A=M");
				code.add("M=D");
				code.add("@SP");
				code.add("M=M+1");
			}
		}

		public void Return_Command() {
			// frame = LCL
			code.add("@LCL");
			code.add("D=M");
			code.add("@R14");
			code.add("M=D");
			// retAddr = *(frame - 5)
			code.add("@5");
			code.add("A=D-A");
			code.add("D=M");
			code.add("@R15");
			code.add("M=D");
			// *ARG = pop
			code.add("@SP");
			code.add("AM=M-1");
			code.add("D=M");
			code.add("@ARG");
			code.add("A=M");
			code.add("M=D");
			// SP = ARG + 1
			code.add("@ARG");
			code.add("D=M+1");
			code.add("@SP");
			code.add("M=D");
			// THAT = *(frame - 1)
			code.add("@R14");
			code.add("D=M");
			code.add("@1");
			code.add("A=D-A");
			code.add("D=M");
			code.add("@THAT");
			code.add("M=D");
			// THIS = *(frame - 2)
			code.add("@R14");
			code.add("D=M");
			code.add("@2");
			code.add("A=D-A");
			code.add("D=M");
			code.add("@THIS");
			code.add("M=D");
			// ARG = *(frame - 3)
			code.add("@R14");
			code.add("D=M");
			code.add("@3");
			code.add("A=D-A");
			code.add("D=M");
			code.add("@ARG");
			code.add("M=D");
			// LCL = *(frame - 4)
			code.add("@R14");
			code.add("D=M");
			code.add("@4");
			code.add("A=D-A");
			code.add("D=M");
			code.add("@LCL");
			code.add("M=D");
			// goto retAddr
			code.add("@R14");
			code.add("A=M");
			code.add("0;JMP");
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
	private static String fileName;
	private static ArrayList<String> code;
	

	public CodeWriter(File output) {
		try {
			codeGenerator = new CodeGenerator();
			code = new ArrayList<String>();
			labelCounter = 0;
			fileName = "";
            fileWriter = new FileWriter(output);
            bufferWriter = new BufferedWriter(fileWriter);          
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}

	public static void SetFileName(String name) {
		fileName = name;
		if(name.indexOf(".vm") != -1) {
			fileName = name.substring(0, name.indexOf(".vm"));
		}
	}

	public static String GetFileName() {
		return fileName;
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

	public static void WriteInit() {

	}

	public static void WriteLabel(String label) {
		codeGenerator.Label_Command(label);
		Write();
	}

	public static void WriteGoto(String label) {
		codeGenerator.Goto_Command(label);
		Write();
	}

	public static void WriteIf(String label) {
		codeGenerator.If_Goto_Command(label);
		Write();
	}
	
	public static void WriteCall(String functionName, int Args) {
		codeGenerator.Call_Command(functionName, Args);
		Write();
	}

	public static void WriteReturn() {
		codeGenerator.Return_Command();
		Write();
	}

	public static void WriteFunction(String functionName, int locals) {
		codeGenerator.Function_Command(functionName, locals);
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