package com.example.compile;

import java.io.*;
import java.util.regex.*;
import java.util.ArrayList;

public class Tokenizer {

    private FileReader fileReader;
    private BufferedReader bufferedReader;
    private ArrayList<String> INPUT;
    private int INDEX;
    private Token TOKEN;
    private ArrayList<Token> TOKENS;
    private ArrayList<String> KEYWORDS;
    private ArrayList<String> SYMBOLS;
    private ArrayList<String> OPERATORS;

    public String KEYWORD = "keyword";
    public String SYMBOL = "symbol";
    public String STRING_CONST = "stringConstant";
    public String INT_CONST = "integerConstant";
    public String IDENTIFIER = "identifier";
    public String CLASS = "class";
    public String METHOD = "method";
    public String FUNCTION = "function";
    public String CONSTRUCTOR = "constructor";
    public String INT = "int";
    public String BOOLEAN = "boolean";
    public String CHAR = "char";
    public String VOID = "void";
    public String VAR = "var";
    public String STATIC = "static";
    public String FIELD = "field";
    public String LET = "let";
    public String DO = "do";
    public String IF = "if";
    public String ELSE = "else";
    public String WHILE = "while";
    public String RETURN = "return";
    public String TRUE = "true";
    public String FALSE = "false";
    public String NULL = "null";
    public String THIS = "this";

    public Tokenizer(File input) {
        INDEX = 0;
        INPUT = new ArrayList<String>();
        TOKENS = new ArrayList<Token>();
        KEYWORDS = new ArrayList<String>();
        SYMBOLS = new ArrayList<String>();
        OPERATORS = new ArrayList<String>();
        String[] keywords = { "class", "constructor", "function", "method", "field", "static", "var", "int", "char", "boolean", "void", "true", "false", "null", "this", "let", "do", "if", "else", "while", "return" };
        String[] symbols = { "{", "}", "(", ")", "[", "]", ".", ",", ";", "+", "-", "*", "/", "&", "|", "<", ">", "=", "~" };
        String[] operators = { "+", "-", "*", "/", "&", "|", "<", ">", "=" };
        for(String keyword : keywords) {
            KEYWORDS.add(keyword);
        }
        for(String symbol : symbols) {
            SYMBOLS.add(symbol);
        }
        for(String operator : operators) {
            OPERATORS.add(operator);
        }
        try {
            fileReader = new FileReader(input);
            bufferedReader = new BufferedReader(fileReader);
            int value = 0;
            // reads to the end of the stream
            while((value = bufferedReader.read()) != -1) {
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
            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Analyze();
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
                        TOKENS.add(Classify(subString));
                    }
                    TOKENS.add(Classify(last));
                    stream = "";
                }

            } else if (last.equals("\n")) {
                stream = "";
            } else {
                if (SYMBOLS.contains(stream) || KEYWORDS.contains(stream)) {
                    TOKENS.add(Classify(stream));
                    stream = "";
                }
            }
            pos = pos + 1;
        }
    }

    public Token GetToken() {
        return TOKEN;
    }

    public ArrayList<Token> GetTokens() {
        return TOKENS;
    }

    public String GetType() {
        return TOKEN.getType();
    }

    public String GetValue() {
        return TOKEN.getValue();
    }

    public boolean HasMoreTokens() {
        return INDEX + 1 != TOKENS.size();
    }

    public void Advance() {
        if(HasMoreTokens()) {
            TOKEN = TOKENS.get(INDEX);
            INDEX = INDEX + 1;
        } else {
            throw new IllegalStateException("No more tokens");
        }
    }

    public void Back() {
        INDEX = INDEX - 1;
        TOKEN = TOKENS.get(INDEX);
    }

    public boolean IsOperator() {
        return OPERATORS.contains(TOKEN.getValue());
    }

    public Token Classify(String value) {
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