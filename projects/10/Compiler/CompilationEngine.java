import java.io.*;

public class CompilationEngine {

    static FileWriter fileWriter;
    static BufferedWriter bufferWriter;

    public CompilationEngine(File output) {
        try {
            fileWriter = new FileWriter(output);
            bufferWriter = new BufferedWriter(fileWriter);
            bufferWriter.write("<token>");
            bufferWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Write(String content) {
        try {
            bufferWriter.write(content);
            bufferWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Parser(Token token) {
        try {
            bufferWriter.write(token.XmlFormat());
            bufferWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Close() {
        try {
            bufferWriter.write("</token>");
            bufferWriter.newLine();
            bufferWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void CompileClass() {

    }

    private void CompileClassVarDec() {

    }

    private void CompileSubRoutineDec() {

    }

    private void CompileSubRoutineBody() {

    }

    private void CompileParameterList() {

    }

    private void CompileVarDec() {

    }

    private void CompileStatements() {

    }

    private void CompileDo() {

    }

    private void CompileLet() {

    }

    private void CompileWhile() {

    }

    private void CompileReturn() {

    }

    private void CompileIf() {

    }

    private void CompileExpression() {

    }

    private void CompileTerm() {

    }

    private void CompileExpressionList() {

    }
}