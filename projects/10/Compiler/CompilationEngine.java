import java.io.*;
import java.util.ArrayList;

public class CompilationEngine {

    static Token token;
    static FileWriter fileWriter;
    static BufferedWriter bufferWriter;
    static Tokenizer tokenizer;
    static ArrayList<Token> TOKENS;

    public CompilationEngine(File output) {
        tokenizer = new Tokenizer();
        TOKENS = new ArrayList<Token>();
        try {
            fileWriter = new FileWriter(output);
            bufferWriter = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Open(File input) {
        tokenizer.Open(input);
    }

    public void Close() {
        tokenizer.Close();
        try {
            bufferWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Write() {
        try {
            bufferWriter.write("<token>");
            bufferWriter.newLine();
            for(Token t : TOKENS) {
                bufferWriter.write(t.XmlFormat());
                bufferWriter.newLine();
            }
            bufferWriter.write("</token>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Parser() {
        while (tokenizer.HasMoreTokens()) {
            tokenizer.Advance();
        }
        for( Token t : tokenizer.GetTokens()) {
            TOKENS.add(t);
        }
        Write();
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