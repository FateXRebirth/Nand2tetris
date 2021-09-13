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

    public void write(String code) {
        try {
            bufferedWriter.write(code);
            bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writePush(String segment, int index) {
        write(String.format("push %s %s", segment, index));
    }

    public void writePop(String segment, int index) {
        write(String.format("pop %s %s", segment, index));
    }

    public void writeArithmetic(String command) {
        switch(command) {
            case "ADD":
                write("add");
                break;
            case "SUB":
                write("sub");
                break;
            case "NEG":
                write("neg");
                break;
            case "EQ":
                write("eq");
                break;
            case "GT":
                write("gt");
                break;
            case "LT":
                write("lt");
                break;
            case "AND":
                write("and");
                break;
            case "OR":
                write("or");
                break;
            case "NOT":
                write("not");
                break;
            default:
                throw new NoSuchFieldError("no such command");
        }
    }

    public void writeLabel(String label) {
        write(String.format("label %s", label));
    }

    public void writeGoto(String label) {
        write(String.format("goto %s", label));
    }

    public void writeIf(String label) {
        write(String.format("if-goto %s", label));
    }

    public void writeCall(String name, int arguments) {
        write(String.format("call %s %s", name, arguments));
    }

    public void writeFunction(String name, int locals) {
        write(String.format("function %s %s", name, locals));
    }

    public void writeReturn() {
        write("return");
    }
}