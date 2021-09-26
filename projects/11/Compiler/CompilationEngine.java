import java.io.*;

public class CompilationEngine {

    private JackTokenizer tokenizer;
    private SymbolTable symbolTable;
    private VMWriter vmWriter;
    private FileWriter fileWriter;
    private BufferedWriter bufferedWriter;
    private FileWriter tokenFileWriter;
    private BufferedWriter tokenBufferedWriter;

    private String className;
    private String functionName;
    private String currentType;
    private String returnType;
    private String currentKind;
    private int tabSize;
    private int parametersCount;
    private int labelCount;

    public CompilationEngine(File inFile, File outFile, File outTokenFile, File outVMFile) {
        try {
            tokenizer = new JackTokenizer(inFile);
            symbolTable = new SymbolTable();
            vmWriter = new VMWriter(outVMFile);
            fileWriter = new FileWriter(outFile);
            bufferedWriter = new BufferedWriter(fileWriter);
            tokenFileWriter = new FileWriter(outTokenFile);
            tokenBufferedWriter = new BufferedWriter(tokenFileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        className = outVMFile.getName().substring(0, outVMFile.getName().lastIndexOf("."));
        functionName = "";
        currentType = "";
        returnType = "";
        currentKind = "";
        tabSize = 1;
        parametersCount = 0;
        labelCount = 0;
    }

    public void start() {
        try {
            bufferedWriter.write("<class>");
            bufferedWriter.newLine();
            tokenBufferedWriter.write("<tokens>");
            tokenBufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void end() {
        try {
            bufferedWriter.write("</class>");
            bufferedWriter.newLine();
            tokenBufferedWriter.write("</tokens>");
            tokenBufferedWriter.newLine();
            bufferedWriter.close();
            fileWriter.close();
            tokenBufferedWriter.close();
            tokenFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeTag(String type, String tag) {
        if (type.equals("start")) {
            try {
                bufferedWriter.write(indentation() + "<" + tag + ">");
                bufferedWriter.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            indent();
        } else {
            dedent();
            try {
                bufferedWriter.write(indentation() + "</" + tag + ">");
                bufferedWriter.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void write(Token token) {
        try {
            bufferedWriter.write(indentation() + token.xmlFormat());
            bufferedWriter.newLine();
            tokenBufferedWriter.write(token.xmlFormat());
            tokenBufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compileType() {
        tokenizer.advance();

        currentType = tokenizer.getValue();

        if (equal(tokenizer.getType(), tokenizer.KEYWORD) && equal(tokenizer.getValue(), tokenizer.INT)
                || equal(tokenizer.getValue(), tokenizer.CHAR) || equal(tokenizer.getValue(), tokenizer.BOOLEAN)) {
            write(tokenizer.getToken());
            return;
        }
        if (equal(tokenizer.getType(), tokenizer.IDENTIFIER)) {
            write(tokenizer.getToken());
            return;
        }
        exception("Int | Char | Boolean | ClassName");
    }

    public void compileReturnType() {
        tokenizer.advance();

        returnType = tokenizer.getValue();

        if (equal(tokenizer.getType(), tokenizer.KEYWORD) && equal(tokenizer.getValue(), tokenizer.INT)
                || equal(tokenizer.getValue(), tokenizer.CHAR) || equal(tokenizer.getValue(), tokenizer.BOOLEAN)) {
            write(tokenizer.getToken());
            return;
        }
        if (equal(tokenizer.getType(), tokenizer.IDENTIFIER)) {
            write(tokenizer.getToken());
            return;
        }
        exception("Int | Char | Boolean | ClassName");
    }

    public void compileClass() {
        tokenizer.advance();

        if (notEqual(tokenizer.getType(), tokenizer.KEYWORD) || notEqual(tokenizer.getValue(), tokenizer.CLASS)) {
            exception("Class");
        }

        start();
        write(tokenizer.getToken());
        tokenizer.advance();

        if (notEqual(tokenizer.getType(), tokenizer.IDENTIFIER)) {
            exception("ClassName");
        }
        write(tokenizer.getToken());
        expect("{");

        compileClassVarDec();
        compileSubRoutineDec();

        expect("}");

        if (tokenizer.hasMoreTokens()) {
            throw new IllegalStateException("Unexpected Tokens");
        }

        end();
        vmWriter.close();
    }

    public void compileClassVarDec() {
        tokenizer.advance();

        if (equal(tokenizer.getType(), tokenizer.SYMBOL) && equal(tokenizer.getValue(), "}")) {
            tokenizer.back();
            return;
        }

        if (notEqual(tokenizer.getType(), tokenizer.KEYWORD)) {
            exception("Keyword");
        }

        if (equal(tokenizer.getValue(), tokenizer.CONSTRUCTOR) || equal(tokenizer.getValue(), tokenizer.FUNCTION)
                || equal(tokenizer.getValue(), tokenizer.METHOD)) {
            tokenizer.back();
            return;
        }

        writeTag("start", "classVarDec");

        if (notEqual(tokenizer.getValue(), tokenizer.STATIC) && notEqual(tokenizer.getValue(), tokenizer.FIELD)) {
            exception("Static or Field");
        }

        write(tokenizer.getToken());

        currentKind = tokenizer.getValue();

        compileType();

        do {
            tokenizer.advance();
            if (notEqual(tokenizer.getType(), tokenizer.IDENTIFIER)) {
                exception("Identifier");
            }
            write(tokenizer.getToken());

            symbolTable.define(tokenizer.getValue(), currentType, currentKind);

            tokenizer.advance();
            if (notEqual(tokenizer.getType(), tokenizer.SYMBOL)
                    || (notEqual(tokenizer.getValue(), ",") && notEqual(tokenizer.getValue(), ";"))) {
                exception("',' or ';'");
            }
            if (equal(tokenizer.getValue(), ",")) {
                write(tokenizer.getToken());
            } else {
                write(tokenizer.getToken());
                break;
            }
        } while (true);

        writeTag("end", "classVarDec");

        compileClassVarDec();
    }

    public void compileSubRoutineDec() {

        symbolTable.resetSubroutineScope();

        tokenizer.advance();

        if (equal(tokenizer.getType(), tokenizer.SYMBOL) && equal(tokenizer.getValue(), "}")) {
            tokenizer.back();
            return;
        }

        if (notEqual(tokenizer.getType(), tokenizer.KEYWORD) || (notEqual(tokenizer.getValue(), tokenizer.CONSTRUCTOR))
                && (notEqual(tokenizer.getValue(), tokenizer.FUNCTION))
                && (notEqual(tokenizer.getValue(), tokenizer.METHOD))) {
            exception("Constructor | Function | Method");
        }

        writeTag("start", "subroutineDec");

        write(tokenizer.getToken());
        tokenizer.advance();
        if (equal(tokenizer.getType(), tokenizer.KEYWORD) && equal(tokenizer.getValue(), tokenizer.VOID)) {
            write(tokenizer.getToken());
            returnType = tokenizer.getValue();
        } else {
            tokenizer.back();
            compileType();
        }

        tokenizer.advance();
        if (notEqual(tokenizer.getType(), tokenizer.IDENTIFIER)) {
            exception("SubroutineName");
        }
        write(tokenizer.getToken());
        functionName = tokenizer.getValue();

        expect("(");
        writeTag("start", "parameterList");
        compileParameterList();
        writeTag("end", "parameterList");
        expect(")");

        vmWriter.writeFunction(String.format("%s.%s", className, functionName), parametersCount);
        parametersCount = 0;

        compileSubRoutineBody();
        writeTag("end", "subroutineDec");
        compileSubRoutineDec();
    }

    public void compileSubRoutineBody() {
        writeTag("start", "subroutineBody");
        expect("{");
        compileVarDec();
        writeTag("start", "statements");
        compileStatements();
        writeTag("end", "statements");
        expect("}");
        writeTag("end", "subroutineBody");
    }

    public void compileSubroutineCall() {
        tokenizer.advance();
        if (notEqual(tokenizer.getType(), tokenizer.IDENTIFIER)) {
            exception("Identifier");
        }
        write(tokenizer.getToken());
        functionName = tokenizer.getValue();

        tokenizer.advance();
        if (equal(tokenizer.getType(), tokenizer.SYMBOL) && equal(tokenizer.getValue(), "(")) {
            write(tokenizer.getToken());
            compileExpressionList();
            expect(")");
        } else if (equal(tokenizer.getType(), tokenizer.SYMBOL) && equal(tokenizer.getValue(), ".")) {
            write(tokenizer.getToken());
            functionName += tokenizer.getValue();
            tokenizer.advance();
            if (notEqual(tokenizer.getType(), tokenizer.IDENTIFIER)) {
                exception("Identifier");
            }
            write(tokenizer.getToken());
            functionName += tokenizer.getValue();
            expect("(");
            compileExpressionList();
            expect(")");
        } else {
            exception("'(' or '.'");
        }
        vmWriter.writeCall(functionName, parametersCount);
        parametersCount = 0;
        if (equal(returnType, tokenizer.VOID)) {
            vmWriter.writePop("temp", 0);
        }
    }

    public void compileParameterList() {
        tokenizer.advance();
        if (equal(tokenizer.getType(), tokenizer.SYMBOL) && equal(tokenizer.getValue(), ")")) {
            tokenizer.back();
            return;
        }
        tokenizer.back();
        do {
            currentKind = tokenizer.ARGUMENT;

            compileType();

            tokenizer.advance();
            if (notEqual(tokenizer.getType(), tokenizer.IDENTIFIER)) {
                exception("Identifier");
            }
            write(tokenizer.getToken());

            symbolTable.define(tokenizer.getValue(), currentType, currentKind);

            parametersCount++;

            tokenizer.advance();
            if (notEqual(tokenizer.getType(), tokenizer.SYMBOL)
                    || (notEqual(tokenizer.getValue(), ",") && notEqual(tokenizer.getValue(), ")"))) {
                exception("',' or ')'");
            }
            if (equal(tokenizer.getValue(), ",")) {
                write(tokenizer.getToken());
            } else {
                tokenizer.back();
                break;
            }
        } while (true);
    }

    public void compileVarDec() {
        tokenizer.advance();
        if (notEqual(tokenizer.getType(), tokenizer.KEYWORD) || notEqual(tokenizer.getValue(), tokenizer.VAR)) {
            tokenizer.back();
            return;
        }
        writeTag("start", "varDec");
        write(tokenizer.getToken());

        currentKind = tokenizer.VAR;

        compileType();
        do {
            tokenizer.advance();
            if (notEqual(tokenizer.getType(), tokenizer.IDENTIFIER)) {
                exception("Identifier");
            }
            write(tokenizer.getToken());

            symbolTable.define(tokenizer.getValue(), currentType, currentKind);

            tokenizer.advance();
            if (notEqual(tokenizer.getType(), tokenizer.SYMBOL)
                    || (notEqual(tokenizer.getValue(), ",") && notEqual(tokenizer.getValue(), ";"))) {
                exception("',' or ';'");
            }
            if (equal(tokenizer.getValue(), ",")) {
                write(tokenizer.getToken());
            } else {
                write(tokenizer.getToken());
                break;
            }
        } while (true);
        writeTag("end", "varDec");
        compileVarDec();
    }

    public void compileStatements() {
        tokenizer.advance();
        if (equal(tokenizer.getType(), tokenizer.SYMBOL) && equal(tokenizer.getValue(), "}")) {
            tokenizer.back();
            return;
        }

        if (notEqual(tokenizer.getType(), tokenizer.KEYWORD)) {
            exception("Keyword");
        } else {
            if (equal(tokenizer.getValue(), tokenizer.LET)) {
                compileLet();
            } else if (equal(tokenizer.getValue(), tokenizer.IF)) {
                compileIf();
            } else if (equal(tokenizer.getValue(), tokenizer.WHILE)) {
                compileWhile();
            } else if (equal(tokenizer.getValue(), tokenizer.DO)) {
                compileDo();
            } else if (equal(tokenizer.getValue(), tokenizer.RETURN)) {
                compileReturn();
            } else {
                exception("Let | If | While | Do | Return");
            }
        }
        compileStatements();
    }

    public void compileDo() {
        writeTag("start", "doStatement");
        write(tokenizer.getToken());
        compileSubroutineCall();
        expect(";");
        writeTag("end", "doStatement");

        vmWriter.writePop("temp", 0);
    }

    public void compileLet() {
        writeTag("start", "letStatement");
        write(tokenizer.getToken());
        tokenizer.advance();
        if (notEqual(tokenizer.getType(), tokenizer.IDENTIFIER)) {
            exception("VarName");
        }

        write(tokenizer.getToken());
        String name = tokenizer.getValue();
        Symbol symbol = symbolTable.getSymbolByName(name);

        tokenizer.advance();
        if (notEqual(tokenizer.getType(), tokenizer.SYMBOL)
                || (notEqual(tokenizer.getValue(), "[") && notEqual(tokenizer.getValue(), "="))) {
            exception("'[' or '='");
        }

        boolean keepGoing = false;
        if (equal(tokenizer.getValue(), "[")) {
            keepGoing = true;
            write(tokenizer.getToken());
            compileExpression();
            tokenizer.advance();
            if (equal(tokenizer.getType(), tokenizer.SYMBOL) && equal(tokenizer.getValue(), "]")) {
                write(tokenizer.getToken());
            } else {
                exception("']");
            }
        }
        if (keepGoing)
            tokenizer.advance();

        write(tokenizer.getToken());
        compileExpression();
        expect(";");
        writeTag("end", "letStatement");

        vmWriter.writePop(symbol.getKind(), symbol.getIndex());
    }

    public void compileWhile() {
        writeTag("start", "whileStatement");

        String startLabel = String.format("%s_LABEL_%d_START", functionName, labelCount);
        String endLabel = String.format("%s_LABEL_%d_END", functionName, labelCount);
        labelCount++;

        write(tokenizer.getToken());
        vmWriter.writeLabel(startLabel);

        expect("(");

        compileExpression();

        expect(")");

        vmWriter.writeArithmetic("~");
        vmWriter.writeIf(endLabel);

        expect("{");
        writeTag("start", "statements");
        compileStatements();
        writeTag("end", "statements");
        expect("}");

        vmWriter.writeGoto(startLabel);
        vmWriter.writeLabel(endLabel);

        writeTag("end", "whileStatement");
    }

    public void compileReturn() {
        writeTag("start", "returnStatement");
        write(tokenizer.getToken());
        tokenizer.advance();
        if (equal(tokenizer.getType(), tokenizer.SYMBOL) && equal(tokenizer.getValue(), ";")) {
            write(tokenizer.getToken());
            if (equal(returnType, tokenizer.VOID)) {
                vmWriter.writePush("constant", 0);
            }
        } else {
            tokenizer.back();
            compileExpression();
            expect(";");
        }
        vmWriter.writeReturn();
        writeTag("end", "returnStatement");
    }

    public void compileIf() {
        writeTag("start", "ifStatement");

        String startLabel = String.format("%s_LABEL_%d_START", functionName, labelCount);
        String endLabel = String.format("%s_LABEL_%d_END", functionName, labelCount);
        labelCount++;

        write(tokenizer.getToken());
        expect("(");
        compileExpression();
        expect(")");

        vmWriter.writeArithmetic("~");
        vmWriter.writeIf(startLabel);

        expect("{");
        writeTag("start", "statements");
        compileStatements();
        writeTag("end", "statements");
        expect("}");

        vmWriter.writeGoto(endLabel);
        vmWriter.writeLabel(startLabel);

        tokenizer.advance();
        if (equal(tokenizer.getType(), tokenizer.KEYWORD) && equal(tokenizer.getValue(), tokenizer.ELSE)) {
            write(tokenizer.getToken());
            expect("{");
            writeTag("start", "statements");
            compileStatements();
            writeTag("end", "statements");
            expect("}");
        } else {
            tokenizer.back();
        }

        vmWriter.writeLabel(endLabel);

        writeTag("end", "ifStatement");
    }

    public void compileExpression() {
        writeTag("start", "expression");
        compileTerm();
        do {
            tokenizer.advance();
            if (equal(tokenizer.getType(), tokenizer.SYMBOL) && tokenizer.isOperator()) {
                write(tokenizer.getToken());
                String operator = tokenizer.getValue();
                compileTerm();
                vmWriter.writeArithmetic(operator);
            } else {
                tokenizer.back();
                break;
            }
        } while (true);
        writeTag("end", "expression");
    }

    public void compileTerm() {
        writeTag("start", "term");
        tokenizer.advance();
        if (equal(tokenizer.getType(), tokenizer.IDENTIFIER)) {
            Token identifier = tokenizer.getToken();
            tokenizer.advance();
            if (equal(tokenizer.getType(), tokenizer.SYMBOL) && equal(tokenizer.getValue(), "[")) {
                write(identifier);
                write(tokenizer.getToken());
                compileExpression();
                expect("]");
            } else if (equal(tokenizer.getType(), tokenizer.SYMBOL)
                    && (equal(tokenizer.getValue(), "(") || equal(tokenizer.getValue(), "."))) {
                tokenizer.back();
                tokenizer.back();
                compileSubroutineCall();
            } else {
                write(identifier);
                String name = identifier.getValue();
                Symbol symbol = symbolTable.getSymbolByName(name);
                vmWriter.writePush(symbol.getKind(), symbol.getIndex());
                tokenizer.back();
            }
        } else {
            if (equal(tokenizer.getType(), tokenizer.INT_CONST)) {
                write(tokenizer.getToken());
                vmWriter.writePush("constant", Integer.valueOf(tokenizer.getValue()));
            } else if (equal(tokenizer.getType(), tokenizer.STRING_CONST)) {
                write(tokenizer.getToken());
            } else if (equal(tokenizer.getType(), tokenizer.KEYWORD) && (equal(tokenizer.getValue(), tokenizer.TRUE)
                    || equal(tokenizer.getValue(), tokenizer.FALSE) || equal(tokenizer.getValue(), tokenizer.NULL)
                    || equal(tokenizer.getValue(), tokenizer.THIS))) {
                write(tokenizer.getToken());
                switch (tokenizer.getValue()) {
                    case "true":
                        vmWriter.writePush("constant", 1);
                        vmWriter.writeArithmetic("--");
                        break;
                    case "false":
                    case "null":
                        vmWriter.writePush("constant", 0);
                        break;
                    case "this":
                        break;
                }
            } else if (equal(tokenizer.getType(), tokenizer.SYMBOL) && equal(tokenizer.getValue(), "(")) {
                write(tokenizer.getToken());
                compileExpression();
                expect(")");
            } else if (equal(tokenizer.getType(), tokenizer.SYMBOL)
                    && (equal(tokenizer.getValue(), "-") || equal(tokenizer.getValue(), "~"))) {
                write(tokenizer.getToken());
                String operator = equal(tokenizer.getValue(), "~") ? tokenizer.getValue() : "--";
                compileTerm();
                vmWriter.writeArithmetic(operator);
            } else {
                exception("IntegerConstant | StringConstant | KeywordConstant| '(' expression ')' | unaryOp term");
            }
        }
        writeTag("end", "term");
    }

    public void compileExpressionList() {
        writeTag("start", "expressionList");
        tokenizer.advance();
        if (equal(tokenizer.getType(), tokenizer.SYMBOL) && equal(tokenizer.getValue(), ")")) {
            tokenizer.back();
        } else {
            tokenizer.back();
            compileExpression();
            parametersCount++;
            do {
                tokenizer.advance();
                if (equal(tokenizer.getType(), tokenizer.SYMBOL) && equal(tokenizer.getValue(), ",")) {
                    write(tokenizer.getToken());
                    compileExpression();
                    parametersCount++;
                } else {
                    tokenizer.back();
                    break;
                }
            } while (true);
        }
        writeTag("end", "expressionList");
    }

    public boolean equal(String value1, String value2) {
        return value1.equals(value2);
    }

    public boolean notEqual(String value1, String value2) {
        return !value1.equals(value2);
    }

    public void exception(String message) {
        throw new IllegalStateException("Expected : " + message + ", But : " + tokenizer.getValue());
    }

    public void expect(String symbol) {
        tokenizer.advance();
        if (equal(tokenizer.getType(), tokenizer.SYMBOL) && equal(tokenizer.getValue(), symbol)) {
            write(tokenizer.getToken());
        } else {
            exception(symbol);
        }
    }

    public void indent() {
        tabSize = tabSize + 1;
    }

    public void dedent() {
        tabSize = tabSize - 1;
    }

    public String indentation() {
        String result = "";
        for (int i = 0; i < tabSize; i++) {
            result += "  ";
        }
        return result;
    }
}