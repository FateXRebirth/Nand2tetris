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
        switch (command) {
            case "+":
                write("add");
                break;
            case "-":
                write("sub");
                break;
            case "--":
                write("neg");
                break;
            case "=":
                write("eq");
                break;
            case ">":
            case "&gt;":
                write("gt");
                break;
            case "<":
            case "&lt;":
                write("lt");
                break;
            case "&":
            case "&amp;":
                write("and");
                break;
            case "|":
                write("or");
                break;
            case "~":
                write("not");
                break;
            case "*":
                writeCall("Math.multiply", 2);
                break;
            case "/":
                writeCall("Math.divide", 2);
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