import java.io.*;
import java.util.ArrayList;

public class CompilationEngine {

    private Tokenizer tokenizer;
    private FileWriter fileWriter;
    private BufferedWriter bufferedWriter;
    private FileWriter TokenfileWriter;
    private BufferedWriter TokenbufferedWriter;

    private ArrayList<Token> TOKENS;

    public CompilationEngine(File inFile, File outFile, File outTokenFile) {
        TOKENS = new ArrayList<Token>();
        try {
            tokenizer = new Tokenizer(inFile);
            fileWriter = new FileWriter(outFile);
            bufferedWriter = new BufferedWriter(fileWriter);
            TokenfileWriter = new FileWriter(outTokenFile);
            TokenbufferedWriter = new BufferedWriter(TokenfileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void WriteTag(String tag) {
        System.out.println(tag);
    }

    public void Write() {

        try {
            bufferedWriter.write("<class>");
            bufferedWriter.newLine();
            TokenbufferedWriter.write("<token>");
            TokenbufferedWriter.newLine();
            for(Token t : tokenizer.GetTokens()) {
                bufferedWriter.write(t.XmlFormat());
                bufferedWriter.newLine();
                TokenbufferedWriter.write(t.XmlFormat());
                TokenbufferedWriter.newLine();
            }
            bufferedWriter.write("</class>");
            TokenbufferedWriter.write("</token>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void CompileClass() {
        WriteTag("<class>");
        WriteTag("</class>");
        Write();
        try {
            bufferedWriter.close();
            fileWriter.close();
            TokenbufferedWriter.close();
            TokenfileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void CompileClassVarDec() {
        WriteTag("<classVarDec>");
        WriteTag("</classVarDec>");
    }

    public void CompileSubRoutineDec() {
        WriteTag("<subRoutineDec>");
        WriteTag("</subRoutineDec>");
    }

    public void CompileSubRoutineBody() {
        WriteTag("<subRoutineBody>");
        WriteTag("</subRoutineBody>");
    }

    public void CompileParameterList() {
        WriteTag("<parameterList>");
        WriteTag("</parameterList>");
    }

    public void CompileVarDec() {
        WriteTag("<varDec>");
        WriteTag("</varDec>");
    }

    public void CompileStatements() {
        WriteTag("<statements>");
        WriteTag("</statements>");
    }

    public void CompileDo() {
        WriteTag("<doStatement>");
        WriteTag("</doStatement>");
    }

    public void CompileLet() {
        WriteTag("<letStatement>");
        WriteTag("</letStatement>");
    }

    public void CompileWhile() {
        WriteTag("<whileStatement>");
        WriteTag("</whileStatement>");
    }

    public void CompileReturn() {
        WriteTag("<returnStatement>");
        WriteTag("</returnStatement>");
    }

    public void CompileIf() {
        WriteTag("<ifStatement>");
        WriteTag("</ifStatement>");
    }

    public void CompileExpression() {
        WriteTag("<expression>");
        WriteTag("</expression>");
    }

    public void CompileTerm() {
        WriteTag("<term>");
        WriteTag("</term>");
    }

    public void CompileExpressionList() {
        WriteTag("<expressionList>");
        WriteTag("</expressionList>");
    }
}