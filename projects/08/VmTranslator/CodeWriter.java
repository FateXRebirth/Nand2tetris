import java.io.*;
import java.util.ArrayList;

public class CodeWriter {

	public static class CodeGenerator {

		public void Push_Command(String segment, int index) {
			if ( (segment.equals("static")) || (segment.equals("constant"))
				|| (segment.equals("temp")) || (segment.equals("pointer"))  ) {
				
				switch(segment) {
					case "static":
						code.add("@" + GetFileName() + "$" + String.valueOf(index));
						code.add("D=M");
						break;
					case "constant":
						code.add("@" + String.valueOf(index));
						code.add("D=A");
						break;
					case "temp":
						code.add("@R" + String.valueOf( 5 + index ));
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
				|| (segment.equals("pointer"))  ) {
				
				switch(segment) {
					case "static":
						code.add("@" + GetFileName() + "$" + String.valueOf(index));
						code.add("D=A");
						break;
					case "constant":
						System.out.println("Shouldn't have this case");
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

				code.add("@R13");
				code.add("M=D");
				code.add("@SP");
				code.add("AM=M-1");
				code.add("D=M");
				code.add("@R13");
				code.add("A=M");
				code.add("M=D");
				

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

				code.add("@R13");
				code.add("M=D");
				code.add("@SP");
				code.add("AM=M-1");
				code.add("D=M");
				code.add("@R13");
				code.add("A=M");
				code.add("M=D");

			} else {
				code.add("@SP");
				code.add("AM=M-1");
				code.add("D=M");
				code.add("@R" + String.valueOf( 5 + index ));
				code.add("M=D");
			}

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
			code.add(CreateFalseStart(compareCounter));
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
			code.add(CreateContinueStart(compareCounter));
			code.add("0;JMP");
			code.add(CreateFalseEnd(compareCounter));
			code.add("@SP");
			code.add("A=M-1");
			code.add("M=0");
			code.add(CreateContinueEnd(compareCounter));
			compareCounter = compareCounter + 1;
		}


		public void Label_Command(String label) {
			code.add("(" + GetFileName() + "$" + label + ")");
		}

		public void If_Goto_Command(String label) {
			code.add("@SP");
			code.add("AM=M-1");
			code.add("D=M");
			code.add("@" + GetFileName() + "$" + label);
			code.add("D;JNE");
		}

		public void Goto_Command(String label) {
			code.add("@" + GetFileName() + "$" + label);
			code.add("0;JMP");
		}

		public void Call_Command(String functionName, int Args) {
			// push return Address
			code.add("@" + functionName + "$RET" + String.valueOf(jumpCounter));
			code.add("D=A");
			code.add("@SP");
			code.add("A=M");
			code.add("M=D");
			code.add("@SP");
			code.add("M=M+1");
			// push LCL
			code.add("@LCL");
			code.add("D=M");
			code.add("@SP");
			code.add("A=M");
			code.add("M=D");
			code.add("@SP");
			code.add("M=M+1");
			// push ARG
			code.add("@ARG");
			code.add("D=M");
			code.add("@SP");
			code.add("A=M");
			code.add("M=D");
			code.add("@SP");
			code.add("M=M+1");
			// push THIS
			code.add("@THIS");
			code.add("D=M");
			code.add("@SP");
			code.add("A=M");
			code.add("M=D");
			code.add("@SP");
			code.add("M=M+1");
			// push THAT
			code.add("@THAT");
			code.add("D=M");
			code.add("@SP");
			code.add("A=M");
			code.add("M=D");
			code.add("@SP");
			code.add("M=M+1");
			// ARG = SP-nArgs-5
			code.add("@SP");
			code.add("D=M");
			int count = 5 + Args;
			code.add("@" + String.valueOf(count));
			code.add("D=D-A");
			code.add("@ARG");
			code.add("M=D");
			// LCL = SP
			code.add("@SP");
			code.add("D=M");
			code.add("@LCL");
			code.add("M=D");
			// goto function
			code.add("@" + functionName);
			code.add("0;JMP");
			// return Address
			code.add("(" + functionName + "$RET" + String.valueOf(jumpCounter) + ")");
			jumpCounter = jumpCounter + 1;
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
			// // frame = LCL
			code.add("@LCL");
			code.add("D=M");
			code.add("@FRAME");
			code.add("M=D");
			// retAddr = *(frame - 5)
			code.add("@5");
			code.add("A=D-A");
			code.add("D=M");
			code.add("@RET");
			code.add("M=D");
			// *ARG = pop
			code.add("@ARG");
			code.add("D=M");
			code.add("@R13");
			code.add("M=D");
			code.add("@SP");
			code.add("AM=M-1");
			code.add("D=M");
			code.add("@R13");
			code.add("A=M");
			code.add("M=D");
			// SP = ARG + 1
			code.add("@ARG");
			code.add("D=M");
			code.add("@SP");
			code.add("M=D+1");
			// THAT = *(frame - 1)
			code.add("@FRAME");
			code.add("D=M-1");
			code.add("AM=D");
			code.add("D=M");
			code.add("@THAT");
			code.add("M=D");
			// THIS = *(frame - 2)
			code.add("@FRAME");
			code.add("D=M-1");
			code.add("AM=D");
			code.add("D=M");
			code.add("@THIS");
			code.add("M=D");
			// ARG = *(frame - 3)
			code.add("@FRAME");
			code.add("D=M-1");
			code.add("AM=D");
			code.add("D=M");
			code.add("@ARG");
			code.add("M=D");
			// LCL = *(frame - 4)
			code.add("@FRAME");
			code.add("D=M-1");
			code.add("AM=D");
			code.add("D=M");
			code.add("@LCL");
			code.add("M=D");
			// goto retAddr
			code.add("@RET");
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
	private static int compareCounter;
	private static int jumpCounter;
	private static String fileName;
	private static ArrayList<String> code;
	

	public CodeWriter(File output) {
		try {
			codeGenerator = new CodeGenerator();
			code = new ArrayList<String>();
			compareCounter = 0;
			jumpCounter = 0;
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
		code.add("@256");
		code.add("D=A");
		code.add("@SP");
		code.add("M=D");
		code.add("@Sys.init$RET");
		code.add("D=A");
		code.add("@SP");
		code.add("A=M");
		code.add("M=D");
		code.add("@SP");
		code.add("M=M+1");
		// push LCL
		code.add("@LCL");
		code.add("D=M");
		code.add("@SP");
		code.add("A=M");
		code.add("M=D");
		code.add("@SP");
		code.add("M=M+1");
		// push ARG
		code.add("@ARG");
		code.add("D=M");
		code.add("@SP");
		code.add("A=M");
		code.add("M=D");
		code.add("@SP");
		code.add("M=M+1");
		// push THIS
		code.add("@THIS");
		code.add("D=M");
		code.add("@SP");
		code.add("A=M");
		code.add("M=D");
		code.add("@SP");
		code.add("M=M+1");
		// push THAT
		code.add("@THAT");
		code.add("D=M");
		code.add("@SP");
		code.add("A=M");
		code.add("M=D");
		code.add("@SP");
		code.add("M=M+1");
		// ARG = SP-5
		code.add("@SP");
		code.add("D=M");
		code.add("@5");
		code.add("D=D-A");
		code.add("@ARG");
		code.add("M=D");
		// LCL = SP
		code.add("@SP");
		code.add("D=M");
		code.add("@LCL");
		code.add("M=D");
		// goto Sys.init
		code.add("@Sys.init");
		code.add("0;JMP");
		// return Sys.init
		code.add("(Sys.init$RET)");
		Write();
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
			compareCounter = 0;
			jumpCounter = 0;
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