package com.example.compile;

import java.io.*;

public class CompilationEngine {

    private Tokenizer tokenizer;
    private FileWriter fileWriter;
    private BufferedWriter bufferedWriter;
    private FileWriter TokenfileWriter;
    private BufferedWriter TokenbufferedWriter;

    public CompilationEngine(File inFile, File outFile, File outTokenFile) {
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

    public void Start() {
        try {
            bufferedWriter.write("<class>");
            bufferedWriter.newLine();
            TokenbufferedWriter.write("<token>");
            TokenbufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void End() {
        try {
            bufferedWriter.write("</class>");
            TokenbufferedWriter.write("</token>");
            bufferedWriter.close();
            fileWriter.close();
            TokenbufferedWriter.close();
            TokenfileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void WriteTag(String tag) {
        try {
            bufferedWriter.write(tag);
            bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Write(Token token) {
        try {
            bufferedWriter.write(token.XmlFormat());
            bufferedWriter.newLine();
            TokenbufferedWriter.write(token.XmlFormat());
            TokenbufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void  CompileType() {
        tokenizer.Advance();

        if( Equal(tokenizer.GetType(), tokenizer.KEYWORD) && Equal(tokenizer.GetValue(), tokenizer.INT) || Equal(tokenizer.GetValue(), tokenizer.ARRAY) || Equal(tokenizer.GetValue(), tokenizer.CHAR) || Equal(tokenizer.GetValue(), tokenizer.BOOLEAN)) {
            Write(tokenizer.GetToken());
            return;
        }
        if( Equal(tokenizer.GetType(), tokenizer.IDENTIFIER)) {
            Write(tokenizer.GetToken());
            return;
        }
        Exception("Int | Array | Char | Boolean | ClassName");
        return;
    }

    public void CompileClass() {
        tokenizer.Advance();

        if(NotEqual(tokenizer.GetType(), tokenizer.KEYWORD) || NotEqual(tokenizer.GetValue(), tokenizer.CLASS)) {
            Exception("Class");
        }

        Start();
        Write(tokenizer.GetToken());
        tokenizer.Advance();

        if(NotEqual(tokenizer.GetType(), tokenizer.IDENTIFIER)) {
            Exception("ClassName");
        }
        Write(tokenizer.GetToken());
        Expect("{");

        CompileClassVarDec();
        CompileSubRoutineDec();

        Expect("}");

        if(tokenizer.HasMoreTokens()) {
            throw new IllegalStateException("Unexpected Tokens");
        }

        End();
    }

    public void CompileClassVarDec() {
        tokenizer.Advance();

        if(Equal(tokenizer.GetType(), tokenizer.SYMBOL) && Equal(tokenizer.GetValue(), "}")) {
            tokenizer.Back();
            return;
        }

        if(NotEqual(tokenizer.GetType(), tokenizer.KEYWORD)) {
            Exception("Keyword");
        }

        if(Equal(tokenizer.GetValue(), tokenizer.CONSTRUCTOR) || Equal(tokenizer.GetValue(), tokenizer.FUNCTION) || Equal(tokenizer.GetValue(), tokenizer.METHOD)) {
            tokenizer.Back();
            return;
        }

        WriteTag("<classVarDec>");

        if(NotEqual(tokenizer.GetValue(), tokenizer.STATIC) && NotEqual(tokenizer.GetValue(),tokenizer.FIELD)) {
            Exception("Static or Field");
        }

        Write(tokenizer.GetToken());

        CompileType();

        do {
            tokenizer.Advance();
            if(NotEqual(tokenizer.GetType(), tokenizer.IDENTIFIER)) {
                Exception("Identifier");
            }
            Write(tokenizer.GetToken());
            tokenizer.Advance();
            if(NotEqual(tokenizer.GetType(), tokenizer.SYMBOL) || ( NotEqual(tokenizer.GetValue(), ",") && NotEqual(tokenizer.GetValue(), ";"))) {
                Exception("',' or ';'");
            }
            if(Equal(tokenizer.GetValue(), ",")) {
                Write(tokenizer.GetToken());
            } else {
                Write(tokenizer.GetToken());
                break;
            }
        } while (true);

        WriteTag("</classVarDec>");

        CompileClassVarDec();
    }

    public void CompileSubRoutineDec() {

        tokenizer.Advance();

        if(Equal(tokenizer.GetType(), tokenizer.SYMBOL) && Equal(tokenizer.GetValue(), "}")) {
            tokenizer.Back();
            return;
        }

        if(NotEqual(tokenizer.GetType(), tokenizer.KEYWORD) || (NotEqual(tokenizer.GetValue(), tokenizer.CONSTRUCTOR)) && (NotEqual(tokenizer.GetValue(), tokenizer.FUNCTION)) && (NotEqual(tokenizer.GetValue(), tokenizer.METHOD))) {
            Exception("Constructor | Function | Method");
        }

        WriteTag("<subRoutineDec>");
        Write(tokenizer.GetToken());
        tokenizer.Advance();
        if(Equal(tokenizer.GetType(), tokenizer.KEYWORD) && Equal(tokenizer.GetValue(), tokenizer.VOID)) {
            Write(tokenizer.GetToken());
        } else {
            tokenizer.Back();
            CompileType();
        }

        tokenizer.Advance();
        if(NotEqual(tokenizer.GetType(), tokenizer.IDENTIFIER)) {
            Exception("SubroutineName");
        }
        Write(tokenizer.GetToken());

        Expect("(");
        CompileParameterList();
        Expect(")");
        CompileSubRoutineBody();
        WriteTag("</subRoutineDec>");
        CompileSubRoutineDec();
    }

    public void CompileSubRoutineBody() {
        WriteTag("<subRoutineBody>");
        Expect("{");
        CompileVarDec();
        WriteTag("<statements>");
        CompileStatements();
        WriteTag("</statements>");
        Expect("}");
        WriteTag("</subRoutineBody>");
    }

    public void CompileSubroutineCall() {
        tokenizer.Advance();
        if(NotEqual(tokenizer.GetType(), tokenizer.IDENTIFIER)) {
            Exception("Identifier");
        }
        Write(tokenizer.GetToken());
        tokenizer.Advance();
        if(Equal(tokenizer.GetType(), tokenizer.SYMBOL) && Equal(tokenizer.GetValue(), "(")) {
            Write(tokenizer.GetToken());
            CompileExpressionList();
            Expect(")");
        } else if(Equal(tokenizer.GetType(), tokenizer.SYMBOL) && Equal(tokenizer.GetValue(), ".")) {
            Write(tokenizer.GetToken());
            tokenizer.Advance();
            if(NotEqual(tokenizer.GetType(), tokenizer.IDENTIFIER)) {
                Exception("Identifier");
            }
            Write(tokenizer.GetToken());
            Expect("(");
            CompileExpressionList();
            Expect(")");
        } else {
            Exception("'(' or '.'");
        }
    }

    public void CompileParameterList() {
        tokenizer.Advance();
        if(Equal(tokenizer.GetType(), tokenizer.SYMBOL) && Equal(tokenizer.GetValue(), ")")) {
            tokenizer.Back();
            return;
        }
        WriteTag("<parameterList>");
        tokenizer.Advance();
        do {
            CompileType();

            tokenizer.Advance();
            if(NotEqual(tokenizer.GetType(), tokenizer.IDENTIFIER)) {
                Exception("Identifier");
            }
            Write(tokenizer.GetToken());
            tokenizer.Advance();
            if(NotEqual(tokenizer.GetType(), tokenizer.SYMBOL) || ( NotEqual(tokenizer.GetValue(), ",") && NotEqual(tokenizer.GetValue(), ")"))) {
                Exception("',' or ')'" );
            }
            if(Equal(tokenizer.GetValue(), ",")) {
                Write(tokenizer.GetToken());
            } else {
                tokenizer.Back();
                break;
            }
        } while (true);
        WriteTag("</parameterList>");
    }

    public void CompileVarDec() {
        tokenizer.Advance();
        if(NotEqual(tokenizer.GetType(), tokenizer.KEYWORD) || NotEqual(tokenizer.GetValue(), tokenizer.VAR)) {
            tokenizer.Back();
            return;
        }
        WriteTag("<varDec>");
        Write(tokenizer.GetToken());
        CompileType();
        do {
            tokenizer.Advance();
            if(NotEqual(tokenizer.GetType(), tokenizer.IDENTIFIER)) {
                Exception("Identifier");
            }
            Write(tokenizer.GetToken());
            tokenizer.Advance();
            if(NotEqual(tokenizer.GetType(), tokenizer.SYMBOL) || ( NotEqual(tokenizer.GetValue(), ",") && NotEqual(tokenizer.GetValue(), ";"))) {
                Exception("',' or ';'");
            }
            if(Equal(tokenizer.GetValue(), ",")) {
                Write(tokenizer.GetToken());
            } else {
                Write(tokenizer.GetToken());
                break;
            }
        } while (true);
        WriteTag("</varDec>");
        CompileVarDec();
    }

    public void CompileStatements() {
        tokenizer.Advance();
        if(Equal(tokenizer.GetType(), tokenizer.SYMBOL) && Equal(tokenizer.GetValue(), "}")) {
            tokenizer.Back();
            return;
        }

        if(NotEqual(tokenizer.GetType(), tokenizer.KEYWORD)) {
            Exception("Keyword");
        } else {
            if(Equal(tokenizer.GetValue(), tokenizer.LET)) {
                CompileLet();
            } else if(Equal(tokenizer.GetValue(), tokenizer.IF)) {
                CompileIf();
            } else if(Equal(tokenizer.GetValue(), tokenizer.WHILE)) {
                CompileWhile();
            } else if(Equal(tokenizer.GetValue(), tokenizer.DO)) {
                CompileDo();
            } else if(Equal(tokenizer.GetValue(), tokenizer.RETURN)) {
                CompileReturn();
            } else {
                Exception("Let | If | While | Do | Return");
            }
        }
        CompileStatements();
    }

    public void CompileDo() {
        WriteTag("<doStatement>");
        Write(tokenizer.GetToken());
        CompileSubroutineCall();
        Expect(";");
        WriteTag("</doStatement>");
    }

    public void CompileLet() {
        WriteTag("<letStatement>");
        tokenizer.Advance();
        Write(tokenizer.GetToken());
        tokenizer.Advance();
        if(NotEqual(tokenizer.GetType(), tokenizer.IDENTIFIER)) {
            Exception("VarName");
        }
        Write(tokenizer.GetToken());
        tokenizer.Advance();
        if(NotEqual(tokenizer.GetType(), tokenizer.SYMBOL) || ( NotEqual(tokenizer.GetValue(), "[") && NotEqual(tokenizer.GetValue(), "="))) {
            Exception("'[' or '='");
        }

        boolean KeepGoing = false;
        if(Equal(tokenizer.GetValue(), "[")) {
            KeepGoing = true;
            Write(tokenizer.GetToken());
            CompileExpression();
            tokenizer.Advance();
            if(Equal(tokenizer.GetType(), tokenizer.SYMBOL) && Equal(tokenizer.GetValue(), "]")) {
                Write(tokenizer.GetToken());
            } else {
                Exception("']");
            }
        }
        if(KeepGoing) tokenizer.Advance();

        Write(tokenizer.GetToken());
        CompileExpression();
        Expect(";");
        WriteTag("</letStatement>");
    }

    public void CompileWhile() {
        WriteTag("<whileStatement>");
        tokenizer.Advance();
        Write(tokenizer.GetToken());
        Expect("(");
        CompileExpression();
        Expect(")");
        Expect("{");
        WriteTag("<statements>");
        CompileStatements();
        WriteTag("</statements>");
        Expect("}");
        WriteTag("</whileStatement>");
    }

    public void CompileReturn() {
        WriteTag("<returnStatement>");
        Write(tokenizer.GetToken());
        tokenizer.Advance();
        if(Equal(tokenizer.GetType(), tokenizer.SYMBOL) && Equal(tokenizer.GetValue(), ";")) {
            Write(tokenizer.GetToken());
            WriteTag("</returnStatement>");
            return;
        }

        tokenizer.Back();
        CompileExpression();
        Expect(";");
        WriteTag("</returnStatement>");
    }

    public void CompileIf() {
        WriteTag("<ifStatement>");
        tokenizer.Advance();
        Write(tokenizer.GetToken());
        Expect("(");
        CompileExpression();
        Expect(")");
        Expect("{");
        WriteTag("<statements>");
        CompileStatements();
        WriteTag("</statements>");
        Expect("}");

        tokenizer.Advance();
        if(Equal(tokenizer.GetType(), tokenizer.KEYWORD) && Equal(tokenizer.GetValue(), tokenizer.ELSE)) {
            Write(tokenizer.GetToken());
            Expect("{");
            WriteTag("<statements>");
            CompileStatements();
            WriteTag("</statements>");
            Expect("}");
        } else {
            tokenizer.Back();
        }
        WriteTag("</ifStatement>");
    }

    public void CompileExpression() {
        WriteTag("<expression>");
        CompileTerm();
        do {
            tokenizer.Advance();
            if(Equal(tokenizer.GetType(), tokenizer.SYMBOL) && tokenizer.IsOperator()) {
                if(Equal(tokenizer.GetValue(), ">")) {
                    Write(tokenizer.GetToken());
                } else if(Equal(tokenizer.GetValue(), "<")) {
                    Write(tokenizer.GetToken());
                } else if(Equal(tokenizer.GetValue(), "&")) {
                    Write(tokenizer.GetToken());
                } else {
                    Write(tokenizer.GetToken());
                }
                CompileTerm();
            } else {
                tokenizer.Back();
                break;
            }
        } while (true);
        WriteTag("</expression>");
    }

    public void CompileTerm() {
        WriteTag("<term>");
        tokenizer.Advance();
        if(Equal(tokenizer.GetType(), tokenizer.IDENTIFIER)) {
            Token identifier = tokenizer.GetToken();
            tokenizer.Advance();
            if(Equal(tokenizer.GetType(), tokenizer.SYMBOL) && Equal(tokenizer.GetValue(), "[")) {
                Write(identifier);
                Write(tokenizer.GetToken());
                CompileExpression();
                Expect("]");
            } else if(Equal(tokenizer.GetType(), tokenizer.SYMBOL) && ( Equal(tokenizer.GetValue(), "(") || Equal(tokenizer.GetValue(), "."))) {
                tokenizer.Back();
                tokenizer.Back();
                CompileSubroutineCall();
            } else {
                Write(identifier);
                tokenizer.Back();
            }
        } else {
            if(Equal(tokenizer.GetType(), tokenizer.INT_CONST)) {
                Write(tokenizer.GetToken());
            } else if(Equal(tokenizer.GetType(), tokenizer.STRING_CONST)) {
                Write(tokenizer.GetToken());
            } else if(Equal(tokenizer.GetType(), tokenizer.KEYWORD) && ( Equal(tokenizer.GetValue(), tokenizer.TRUE) || Equal(tokenizer.GetValue(), tokenizer.FALSE) || Equal(tokenizer.GetValue(), tokenizer.NULL) || Equal(tokenizer.GetValue(), tokenizer.THIS))) {
                Write(tokenizer.GetToken());
            } else if(Equal(tokenizer.GetType(), tokenizer.SYMBOL) && Equal(tokenizer.GetValue(), "(")) {
                Write(tokenizer.GetToken());
                CompileExpression();
                Expect(")");
            } else if(Equal(tokenizer.GetType(), tokenizer.SYMBOL) && ( Equal(tokenizer.GetValue(), "-") || Equal(tokenizer.GetValue(), "~"))) {
                Write(tokenizer.GetToken());
                CompileTerm();
            } else {
                Exception("IntegerConstant | StringConstant | KeywordConstant| '(' expression ')' | unaryOp term");
            }
        }
        WriteTag("</term>");
    }

    public void CompileExpressionList() {
        WriteTag("<expressionList>");
        tokenizer.Advance();
        if(Equal(tokenizer.GetType(), tokenizer.SYMBOL) && Equal(tokenizer.GetValue(), ")")) {
            tokenizer.Back();
        } else {
            tokenizer.Back();
            CompileExpression();
            do {
                tokenizer.Advance();
                if(Equal(tokenizer.GetType(), tokenizer.SYMBOL) && Equal(tokenizer.GetValue(), ",")) {
                    Write(tokenizer.GetToken());
                    CompileExpression();
                } else {
                    tokenizer.Back();
                    break;
                }
            } while (true);
        }
        WriteTag("</expressionList>");
    }

    public boolean Equal(String value1, String value2) {
        return value1.equals(value2);
    }

    public boolean NotEqual(String value1, String value2) {
        return !value1.equals(value2);
    }

    public void Exception(String message) {
        throw new IllegalStateException("Expected : " + message + ", But :" + tokenizer.GetValue());
    }

    public void Expect(String symbol) {
        tokenizer.Advance();
        if(Equal(tokenizer.GetType(), tokenizer.SYMBOL) && Equal(tokenizer.GetValue(), symbol)) {
            Write(tokenizer.GetToken());
        } else {
            Exception(symbol);
        }
    }
}