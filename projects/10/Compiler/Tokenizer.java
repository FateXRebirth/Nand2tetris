package com.example.compile;

import java.io.*;
import java.util.regex.*;
import java.util.ArrayList;

public class Tokenizer {

    static CompilationEngine compilationEngine;
    static FileWriter fileWriter;
    static BufferedWriter bufferWriter;
    static FileReader fileReader;
    static BufferedReader bufferReader;
    static ArrayList<String> INPUT;
    static int INDEX;
    static Token TOKEN;
    static ArrayList<Token> TOKENS;
    static ArrayList<String> KEYWORDS;
    static ArrayList<String> SYMBOLS;
    static String KEYWORD = "keyword";
    static String SYMBOL = "symbol";
    static String STRING_CONST = "stringConstant";
    static String INT_CONST = "integerConstant";
    static String IDENTIFIER = "identifier";

    public class CompilationEngine {

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

        public void Close() {
            try {
                bufferWriter.write("</token>");
                bufferWriter.close();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void Write() {
            try {
                for(Token t : TOKENS) {
                    bufferWriter.write(t.XmlFormat());
                    bufferWriter.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void WriteToken(Token token) {
            System.out.println(token.XmlFormat());
        }

        public void WriteTag(String tag) {
            System.out.println(tag);
        }

        public void CompileClass() {
            WriteTag("<class>");
            WriteTag("</class>");
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

    public Tokenizer(File output) {
        compilationEngine = new CompilationEngine(output);
        INDEX = 0;
        INPUT = new ArrayList<String>();
        TOKENS = new ArrayList<Token>();
        KEYWORDS = new ArrayList<String>();
        SYMBOLS = new ArrayList<String>();
        final String[] keywords = { "class", "constructor", "function", "method", "field", "static", "var", "int", "char", "boolean", "void", "true", "false", "null", "this", "let", "do", "if", "else", "while", "return" };
        final String[] symbols = { "{", "}", "(", ")", "[", "]", ".", ",", ";", "+", "-", "*", "/", "&", "|", "<", ">", "=", "~" };
        for(String keyword : keywords) {
            KEYWORDS.add(keyword);
        }
        for(String symbol : symbols) {
            SYMBOLS.add(symbol);
        }
    }

    public void Open(File input) {
        try {
            fileReader = new FileReader(input);
            bufferReader = new BufferedReader(fileReader);
            int value = 0;
            // reads to the end of the stream
            while((value = bufferReader.read()) != -1) {
                // ignore space and \n
                if(value != 32) {
                    // converts int to character
                    char c = (char)value;
                    // prints character and its ascii code for debug
                    // System.out.println("char(" + c + ") -> int(" + value +")");
                    // save tokens
                    INPUT.add(Character.toString(c));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Close() {
        compilationEngine.Close();
        try {
            bufferReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Analyze() {
        String stream = "";
        String last = "";
        String commentString = "";
        boolean comment = false;
        int pos = 0;
        while(pos < INPUT.size()) {
            last = INPUT.get(pos);
            stream = stream + INPUT.get(pos);
            if (SYMBOLS.contains(last)) {
                if (last.equals("/") || last.equals("*")) {
                    commentString = commentString + last;
                    if (commentString.equals("//") || commentString.equals("/*")) {
                        String c;
                        int index = 1;
                        comment = true;
                        while (comment) {
                            c = INPUT.get(pos + index);
                            if (c.equals("/") || c.equals("\n")) {
                                pos = pos + index;
                                comment = false;
                            } else {
                                index = index + 1;
                            }
                        }
                    }
                } else {
                    if (stream.length() > 1) {
                        String subString = stream.substring(0, stream.length() - 1);
                        TOKENS.add(GetToken(subString));
                    }
                    TOKENS.add(GetToken(last));
                    stream = "";
                }

            } else if (last.equals("\n")) {
                stream = "";
            } else {
                if (SYMBOLS.contains(stream) || KEYWORDS.contains(stream)) {
                    TOKENS.add(GetToken(stream));
                    stream = "";
                }
            }
            pos = pos + 1;
        }
        compilationEngine.Write();
    }


    public boolean HasMoreTokens() {
        if(INDEX + 1  == TOKENS.size()) {
            return false;
        } else {
            INDEX = INDEX + 1;
            return  true;
        }
    }

    public void Advance() {
        TOKEN = TOKENS.get(INDEX);
    }

    public Token GetToken(String value) {
        if(KEYWORDS.contains(value)) {
            return new Token(KEYWORD, value);
        } else if(SYMBOLS.contains(value)) {
            if(value.equals("<")) {
                return new Token(SYMBOL, "&lt;");
            } else if(value.equals(">")) {
                return new Token(SYMBOL, "&gt;");
            } else if(value.equals("\"")) {
                return new Token(SYMBOL, "&quot;");
            } else if(value.equals("&")) {
                return new Token(SYMBOL, "&amp;");
            } else {
                return new Token(SYMBOL, value);
            }
        } else if(Pattern.matches("^\".+\"$", value)) {
            return new Token(STRING_CONST, value.substring(1, value.length()-1));
        } else if(Pattern.matches("^[0-9]{1,5}$", value) && Integer.valueOf(value) >= 0 && Integer.valueOf(value) <= 32767) {
            return new Token(INT_CONST, value);
        } else {
            return new Token(IDENTIFIER, value);
        }
    }
}