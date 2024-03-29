import java.io.*;
import java.util.ArrayList;

public class CompilationEngine {

    private JackTokenizer tokenizer;
    private SymbolTable symbolTable;
    private VMWriter vmWriter;

    private String className;
    private String returnType;
    private String currentType;
    private String currentKind;
    private ArrayList<String> functionList;
    private int whileCount;
    private int ifCount;

    public CompilationEngine(File inFile, File outFile) {
        tokenizer = new JackTokenizer(inFile);
        symbolTable = new SymbolTable();
        vmWriter = new VMWriter(outFile);
        className = outFile.getName().substring(0, outFile.getName().lastIndexOf("."));
        returnType = "";
        currentType = "";
        currentKind = "";
        functionList = new ArrayList<String>();
        whileCount = 0;
        ifCount = 0;
    }

    public void compileType() {
        tokenizer.advance();

        currentType = tokenizer.getValue();

        if (equal(tokenizer.getType(), tokenizer.KEYWORD) && equal(tokenizer.getValue(), tokenizer.INT)
                || equal(tokenizer.getValue(), tokenizer.CHAR) || equal(tokenizer.getValue(), tokenizer.BOOLEAN)) {
            return;
        }
        if (equal(tokenizer.getType(), tokenizer.IDENTIFIER)) {
            return;
        }
        exception("Int | Char | Boolean | ClassName");
    }

    public void compileReturnType() {
        tokenizer.advance();

        returnType = tokenizer.getValue();

        if (equal(tokenizer.getType(), tokenizer.KEYWORD) && (equal(tokenizer.getValue(), tokenizer.VOID)
                || equal(tokenizer.getValue(), tokenizer.INT) || equal(tokenizer.getValue(), tokenizer.CHAR)
                || equal(tokenizer.getValue(), tokenizer.BOOLEAN))) {
            return;
        }
        if (equal(tokenizer.getType(), tokenizer.IDENTIFIER)) {
            return;
        }
        exception("Void | Int | Char | Boolean | ClassName");
    }

    public void compileClass() {
        tokenizer.advance();

        if (notEqual(tokenizer.getType(), tokenizer.KEYWORD) || notEqual(tokenizer.getValue(), tokenizer.CLASS)) {
            exception("Class");
        }

        tokenizer.advance();

        if (notEqual(tokenizer.getType(), tokenizer.IDENTIFIER)) {
            exception("ClassName");
        }

        expect("{");

        compileClassVarDec();
        compileSubroutineDec();

        expect("}");

        if (tokenizer.hasMoreTokens()) {
            throw new IllegalStateException("Unexpected Tokens");
        }

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

        if (notEqual(tokenizer.getValue(), tokenizer.STATIC) && notEqual(tokenizer.getValue(), tokenizer.FIELD)) {
            exception("Static or Field");
        }

        currentKind = tokenizer.getValue();

        compileType();

        do {
            tokenizer.advance();
            if (notEqual(tokenizer.getType(), tokenizer.IDENTIFIER)) {
                exception("Identifier");
            }

            symbolTable.define(tokenizer.getValue(), currentType, currentKind);

            tokenizer.advance();
            if (notEqual(tokenizer.getType(), tokenizer.SYMBOL)
                    || (notEqual(tokenizer.getValue(), ",") && notEqual(tokenizer.getValue(), ";"))) {
                exception("',' or ';'");
            }
            if (notEqual(tokenizer.getValue(), ",")) {
                break;
            }
        } while (true);

        compileClassVarDec();
    }

    public void compileSubroutineDec() {
        // reset when new subroutine starts
        String functionName = "";
        String functionType = "";
        symbolTable.startSubroutine();
        whileCount = 0;
        ifCount = 0;

        tokenizer.advance();

        functionType = tokenizer.getValue();

        if (equal(tokenizer.getType(), tokenizer.SYMBOL) && equal(tokenizer.getValue(), "}")) {
            tokenizer.back();
            return;
        }

        if (notEqual(tokenizer.getType(), tokenizer.KEYWORD) || (notEqual(tokenizer.getValue(), tokenizer.CONSTRUCTOR))
                && (notEqual(tokenizer.getValue(), tokenizer.FUNCTION))
                && (notEqual(tokenizer.getValue(), tokenizer.METHOD))) {
            exception("Constructor | Function | Method");
        }

        if (equal(tokenizer.getValue(), tokenizer.METHOD)) {
            symbolTable.define(className, className, "argument");
        }

        compileReturnType();

        tokenizer.advance();
        if (notEqual(tokenizer.getType(), tokenizer.IDENTIFIER)) {
            exception("SubroutineName");
        }

        functionName = tokenizer.getValue();
        functionList.add(functionName);

        expect("(");
        compileParameterList();
        expect(")");

        compileSubroutineBody(functionName, functionType);
        compileSubroutineDec();
    }

    public void compileSubroutineBody(String functionName, String functionType) {
        expect("{");
        compileVarDec();

        vmWriter.writeFunction(String.format("%s.%s", className, functionName), symbolTable.indexOf(tokenizer.VAR));

        if (equal(functionType, tokenizer.CONSTRUCTOR)) {
            vmWriter.writePush("constant", symbolTable.indexOf("field"));
            vmWriter.writeCall("Memory.alloc", 1);
            vmWriter.writePop("pointer", 0);
        }

        if (equal(functionType, tokenizer.METHOD)) {
            Symbol symbol = symbolTable.getSymbolByName(className);
            vmWriter.writePush(symbol.getKind(), symbol.getIndex());
            vmWriter.writePop("pointer", 0);
        }

        compileStatements();
        expect("}");
    }

    public void compileSubroutineCall() {
        String functionName = "";
        int parametersCount = 0;

        tokenizer.advance();
        if (notEqual(tokenizer.getType(), tokenizer.IDENTIFIER)) {
            exception("Identifier");
        }

        try {
            if (functionList.contains(tokenizer.getValue())) {
                functionName = tokenizer.getValue();
            } else if (equal(tokenizer.getValue(), className)) {
                functionName = className;
            } else {
                Symbol symbol = symbolTable.getSymbolByName(tokenizer.getValue());
                vmWriter.writePush(symbol.getKind(), symbol.getIndex());
                functionName = symbol.getType();
                parametersCount += 1;
            }
        } catch (NoSuchFieldError error) {
            functionName = tokenizer.getValue();
        }

        tokenizer.advance();
        if (equal(tokenizer.getType(), tokenizer.SYMBOL) && equal(tokenizer.getValue(), "(")) {
            vmWriter.writePush("pointer", 0);
            functionName = String.format("%s.%s", className, functionName);
            parametersCount += 1;
            parametersCount += compileExpressionList();
            expect(")");
        } else if (equal(tokenizer.getType(), tokenizer.SYMBOL) && equal(tokenizer.getValue(), ".")) {
            functionName += tokenizer.getValue();
            tokenizer.advance();
            if (notEqual(tokenizer.getType(), tokenizer.IDENTIFIER)) {
                exception("Identifier");
            }
            functionName += tokenizer.getValue();
            expect("(");
            parametersCount += compileExpressionList();
            expect(")");
        } else {
            exception("'(' or '.'");
        }
        vmWriter.writeCall(functionName, parametersCount);
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

            symbolTable.define(tokenizer.getValue(), currentType, currentKind);

            tokenizer.advance();
            if (notEqual(tokenizer.getType(), tokenizer.SYMBOL)
                    || (notEqual(tokenizer.getValue(), ",") && notEqual(tokenizer.getValue(), ")"))) {
                exception("',' or ')'");
            }
            if (notEqual(tokenizer.getValue(), ",")) {
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

        currentKind = tokenizer.VAR;

        compileType();
        do {
            tokenizer.advance();
            if (notEqual(tokenizer.getType(), tokenizer.IDENTIFIER)) {
                exception("Identifier");
            }

            symbolTable.define(tokenizer.getValue(), currentType, currentKind);

            tokenizer.advance();
            if (notEqual(tokenizer.getType(), tokenizer.SYMBOL)
                    || (notEqual(tokenizer.getValue(), ",") && notEqual(tokenizer.getValue(), ";"))) {
                exception("',' or ';'");
            }
            if (notEqual(tokenizer.getValue(), ",")) {
                break;
            }
        } while (true);
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
        compileSubroutineCall();
        expect(";");
        vmWriter.writePop("temp", 0);
    }

    public void compileLet() {
        boolean handleArray = false;
        tokenizer.advance();
        if (notEqual(tokenizer.getType(), tokenizer.IDENTIFIER)) {
            exception("VarName");
        }

        String name = tokenizer.getValue();
        Symbol symbol = symbolTable.getSymbolByName(name);

        tokenizer.advance();
        if (notEqual(tokenizer.getType(), tokenizer.SYMBOL)
                || (notEqual(tokenizer.getValue(), "[") && notEqual(tokenizer.getValue(), "="))) {
            exception("'[' or '='");
        }

        if (equal(tokenizer.getValue(), "[")) {
            handleArray = true;
            compileExpression();
            vmWriter.writePush(symbol.getKind(), symbol.getIndex());
            vmWriter.writeArithmetic("+");
            tokenizer.advance();
            if (!(equal(tokenizer.getType(), tokenizer.SYMBOL) && equal(tokenizer.getValue(), "]"))) {
                exception("']");
            }
            tokenizer.advance();
        }

        compileExpression();
        expect(";");

        if (handleArray) {
            vmWriter.writePop("temp", 0);
            vmWriter.writePop("pointer", 1);
            vmWriter.writePush("temp", 0);
            vmWriter.writePop("that", 0);
            handleArray = false;
        } else {
            vmWriter.writePop(symbol.getKind(), symbol.getIndex());
        }
    }

    public void compileWhile() {
        String startLabel = String.format("WHILE_EXP%d", whileCount);
        String endLabel = String.format("WHILE_END%d", whileCount);
        whileCount++;

        vmWriter.writeLabel(startLabel);

        expect("(");

        compileExpression();

        expect(")");

        vmWriter.writeArithmetic("~");
        vmWriter.writeIf(endLabel);

        expect("{");
        compileStatements();
        expect("}");

        vmWriter.writeGoto(startLabel);
        vmWriter.writeLabel(endLabel);
    }

    public void compileReturn() {
        tokenizer.advance();
        if (equal(tokenizer.getType(), tokenizer.SYMBOL) && equal(tokenizer.getValue(), ";")
                && equal(returnType, tokenizer.VOID)) {
            vmWriter.writePush("constant", 0);
        } else {
            tokenizer.back();
            compileExpression();
            expect(";");
        }
        vmWriter.writeReturn();
    }

    public void compileIf() {
        String trueLabel = String.format("IF_TRUE%d", ifCount);
        String falseLabel = String.format("IF_FALSE%d", ifCount);
        String endLabel = String.format("IF_END%d", ifCount);
        ifCount++;

        expect("(");
        compileExpression();
        expect(")");

        vmWriter.writeIf(trueLabel);
        vmWriter.writeGoto(falseLabel);
        vmWriter.writeLabel(trueLabel);

        expect("{");
        compileStatements();
        expect("}");

        tokenizer.advance();
        if (equal(tokenizer.getType(), tokenizer.KEYWORD) && equal(tokenizer.getValue(), tokenizer.ELSE)) {
            vmWriter.writeGoto(endLabel);
            vmWriter.writeLabel(falseLabel);
            expect("{");
            compileStatements();
            expect("}");
            vmWriter.writeLabel(endLabel);
        } else {
            vmWriter.writeLabel(falseLabel);
            tokenizer.back();
        }
    }

    public void compileExpression() {
        compileTerm();
        do {
            tokenizer.advance();
            if (equal(tokenizer.getType(), tokenizer.SYMBOL) && tokenizer.isOperator()) {
                String operator = tokenizer.getValue();
                compileTerm();
                vmWriter.writeArithmetic(operator);
            } else {
                tokenizer.back();
                break;
            }
        } while (true);
    }

    public void compileTerm() {
        tokenizer.advance();
        Token token = tokenizer.getToken();
        if (equal(tokenizer.getType(), tokenizer.IDENTIFIER)) {
            tokenizer.advance();
            if (equal(tokenizer.getType(), tokenizer.SYMBOL) && equal(tokenizer.getValue(), "[")) {
                Symbol symbol = symbolTable.getSymbolByName(token.getValue());
                compileExpression();
                vmWriter.writePush(symbol.getKind(), symbol.getIndex());
                vmWriter.writeArithmetic("+");
                vmWriter.writePop("pointer", 1);
                vmWriter.writePush("that", 0);
                expect("]");
            } else if (equal(tokenizer.getType(), tokenizer.SYMBOL)
                    && (equal(tokenizer.getValue(), "(") || equal(tokenizer.getValue(), "."))) {
                tokenizer.back();
                tokenizer.back();
                compileSubroutineCall();
            } else {
                Symbol symbol = symbolTable.getSymbolByName(token.getValue());
                vmWriter.writePush(symbol.getKind(), symbol.getIndex());
                tokenizer.back();
            }
        } else {
            if (equal(tokenizer.getType(), tokenizer.INT_CONST)) {
                vmWriter.writePush("constant", Integer.valueOf(tokenizer.getValue()));
            } else if (equal(tokenizer.getType(), tokenizer.STRING_CONST)) {
                String string = token.getValue();
                // filter escape character "\"
                if (string.contains("\\")) {
                    string = string.replaceAll("\\\\", "");
                }
                int stringLength = string.length();
                vmWriter.writePush("constant", stringLength);
                vmWriter.writeCall("String.new", 1);
                for (int asciiCode : string.toCharArray()) {
                    vmWriter.writePush("constant", asciiCode);
                    vmWriter.writeCall("String.appendChar", 2);
                }
            } else if (equal(tokenizer.getType(), tokenizer.KEYWORD) && (equal(tokenizer.getValue(), tokenizer.TRUE)
                    || equal(tokenizer.getValue(), tokenizer.FALSE) || equal(tokenizer.getValue(), tokenizer.NULL)
                    || equal(tokenizer.getValue(), tokenizer.THIS))) {
                switch (tokenizer.getValue()) {
                    case "true":
                        vmWriter.writePush("constant", 0);
                        vmWriter.writeArithmetic("~");
                        break;
                    case "false":
                    case "null":
                        vmWriter.writePush("constant", 0);
                        break;
                    case "this":
                        vmWriter.writePush("pointer", 0);
                        break;
                }
            } else if (equal(tokenizer.getType(), tokenizer.SYMBOL) && equal(tokenizer.getValue(), "(")) {
                compileExpression();
                expect(")");
            } else if (equal(tokenizer.getType(), tokenizer.SYMBOL)
                    && (equal(tokenizer.getValue(), "-") || equal(tokenizer.getValue(), "~"))) {
                String operator = equal(tokenizer.getValue(), "~") ? tokenizer.getValue() : "--";
                compileTerm();
                vmWriter.writeArithmetic(operator);
            } else {
                exception("IntegerConstant | StringConstant | KeywordConstant| '(' expression ')' | unaryOp term");
            }
        }
    }

    public int compileExpressionList() {
        int parametersCount = 0;
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
                    compileExpression();
                    parametersCount++;
                } else {
                    tokenizer.back();
                    break;
                }
            } while (true);
        }

        return parametersCount;
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
        } else {
            exception(symbol);
        }
    }
}