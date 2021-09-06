import java.io.*;

public class VMWriter {

    private FileWriter fileWriter;
    private BufferedWriter bufferedWriter;

    public VMWriter(File file) {
        try {
            fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writePush() {

    }

    public void writePop() {
        
    }

    public void writeArithmetic() {
        
    }

    public void writeLabel() {
        
    }

    public void writeGoto() {
        
    }

    public void writeIf() {
        
    }

    public void writeCall() {
        
    }

    public void writeFunction() {
        
    }

    public void writeReturn() {
        
    }
}