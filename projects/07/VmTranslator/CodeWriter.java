import java.io.*;

public class CodeWriter {

	private static FileWriter fileWriter;
	private static BufferedWriter bufferWriter;

	public CodeWriter(String output) {
		try {
            fileWriter = new FileWriter(output);
            bufferWriter = new BufferedWriter(fileWriter);
            bufferWriter.write("Test");
            bufferWriter.newLine();            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}

	public static void writeArithmetic(String command) {

	}

	public static void writePushPop(String command, String segment, int index) {
	
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
