package com.example.compile;

import java.io.*;

public class CompilationEngine {

    private Tokenizer tokenizer;
    private FileWriter fileWriter;
    private BufferedWriter bufferedWriter;
    private FileWriter TokenfileWriter;
    private BufferedWriter TokenbufferedWriter;

    private int TabSize;

    public CompilationEngine(File inFile, File outFile, File outTokenFile) {
        TabSize = 1;
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

    public void WriteTag(String type, String tag) {
        if(type.equals("start")) {
            try {
                bufferedWriter.write( Indentation() + "<" + tag + ">");
                bufferedWriter.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Indent();
        } else {
            Dedent();
            try {
                bufferedWriter.write( Indentation() +  "</" + tag + ">");
                bufferedWriter.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void Write(Token token) {
        try {
            bufferedWriter.write( Indentation() + token.XmlFormat());
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

        WriteTag("start", "classVarDec");

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

        WriteTag("end", "classVarDec");

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

        WriteTag("start", "subRoutineDec");

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
        WriteTag("end", "subRoutineDec");
        CompileSubRoutineDec();
    }

    public void CompileSubRoutineBody() {
        WriteTag("start", "subRoutineBody");
        Expect("{");
        CompileVarDec();
        WriteTag("start", "statements");
        CompileStatements();
        WriteTag("end", "statements");
        Expect("}");
        WriteTag("end", "subRoutineBody");
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
        WriteTag("start", "parameterList");
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
        WriteTag("end", "parameterList");
    }

    public void CompileVarDec() {
        tokenizer.Advance();
        if(NotEqual(tokenizer.GetType(), tokenizer.KEYWORD) || NotEqual(tokenizer.GetValue(), tokenizer.VAR)) {
            tokenizer.Back();
            return;
        }
        WriteTag("start", "varDec");
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
        WriteTag("end", "varDec");
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
        WriteTag("start", "doStatement");
        Write(tokenizer.GetToken());
        CompileSubroutineCall();
        Expect(";");
        WriteTag("end", "doStatement");
    }

    public void CompileLet() {
        WriteTag("start", "letStatement");
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
        WriteTag("end", "letStatement");
    }

    public void CompileWhile() {
        WriteTag("start", "whileStatement");
        tokenizer.Advance();
        Write(tokenizer.GetToken());
        Expect("(");
        CompileExpression();
        Expect(")");
        Expect("{");
        WriteTag("start", "statements");
        CompileStatements();
        WriteTag("end", "statements");
        Expect("}");
        WriteTag("end", "whileStatement");
    }

    public void CompileReturn() {
        WriteTag("start", "returnStatement");
        Write(tokenizer.GetToken());
        tokenizer.Advance();
        if(Equal(tokenizer.GetType(), tokenizer.SYMBOL) && Equal(tokenizer.GetValue(), ";")) {
            Write(tokenizer.GetToken());
            WriteTag("end", "returnStatement");
            return;
        }

        tokenizer.Back();
        CompileExpression();
        Expect(";");
        WriteTag("end", "returnStatement");
    }

    public void CompileIf() {
        WriteTag("start", "ifStatement");
        tokenizer.Advance();
        Write(tokenizer.GetToken());
        Expect("(");
        CompileExpression();
        Expect(")");
        Expect("{");
        WriteTag("start", "statements");
        CompileStatements();
        WriteTag("end", "statements");
        Expect("}");

        tokenizer.Advance();
        if(Equal(tokenizer.GetType(), tokenizer.KEYWORD) && Equal(tokenizer.GetValue(), tokenizer.ELSE)) {
            Write(tokenizer.GetToken());
            Expect("{");
            WriteTag("start", "statements");
            CompileStatements();
            WriteTag("end", "statements");
            Expect("}");
        } else {
            tokenizer.Back();
        }
        WriteTag("end", "ifStatement");
    }

    public void CompileExpression() {
        WriteTag("start", "expression");
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
        WriteTag("end", "expression");
    }

    public void CompileTerm() {
        WriteTag("start", "term");
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
        WriteTag("end", "term");
    }

    public void CompileExpressionList() {
        WriteTag("start", "expressionList");
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
        WriteTag("end", "expressionList");
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

    public void Indent() {
        TabSize = TabSize + 1;
    }

    public void Dedent() {
        TabSize = TabSize - 1;
    }

    public String Indentation() {
        String result = "";
        for(int i = 0; i < TabSize; i++) {
            result += "  ";
        }
        return result;
    }
}