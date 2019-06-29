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

    public String KeywordRegex = "";
    public String SymbolRegex = "";
    public String IntegerRegex = "";
    public String StringRegex = "";
    public String IdentifierRegex = "";

    public String CLASS = "class";
    public String METHOD = "method";
    public String FUNCTION = "function";
    public String CONSTRUCTOR = "constructor";
    public String INT = "int";
    public String ARRAY = "Array";
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
        String[] keywords = { "class", "constructor", "function", "method", "field", "static", "var", "int", "Array", "char", "boolean", "void", "true", "false", "null", "this", "let", "do", "if", "else", "while", "return" };
        String[] symbols = { "{", "}", "(", ")", "[", "]", ".", ",", ";", "+", "-", "*", "/", "&", "|", "<", ">", "=", "~" };
        String[] operators = { "+", "-", "*", "/", "&", "|", "<", ">", "=" };
        for(String keyword : keywords) {
            KEYWORDS.add(keyword);
            KeywordRegex += keyword + "|";
        }
        SymbolRegex += "[";
        for(String symbol : symbols) {
            SYMBOLS.add(symbol);
            SymbolRegex += "\\" + symbol;
        }
        SymbolRegex += "]";
        for(String operator : operators) {
            OPERATORS.add(operator);
        }
        IntegerRegex = "^[0-9]{1,5}$";
        StringRegex = "\"[^\"\n]*\"";
        IdentifierRegex = "[\\w_]+";
        try {
            fileReader = new FileReader(input);
            bufferedReader = new BufferedReader(fileReader);

            String value = "";
            String next = "";
            String fragment = "";
            boolean isString = false;
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                if(!line.startsWith("//") && !line.startsWith("/*") && !line.equals("")) {
                    for(int i = 0; i < line.length(); i++) {
                        value = String.valueOf(line.charAt(i));
                        if(i < line.length()-1) next = String.valueOf(line.charAt(i+1));
                        if(value.equals(" ") || value.equals("\t")) {
                            continue;
                        } else if(value.equals("\"") && !isString) {
                            isString = true;
                            while (isString) {
                                fragment = fragment + String.valueOf(line.charAt(i));
                                i = i + 1;
                                if(String.valueOf(line.charAt(i)).equals("\"")) {
                                    isString = false;
                                }
                            }
                            fragment = fragment + "\"";
                            INPUT.add(fragment);
                            fragment = "";
                        } else {
                            if(OPERATORS.contains(value) || SYMBOLS.contains(value)) {
                                INPUT.add(value);
                            } else {
                                fragment = fragment + value;
                                if(KEYWORDS.contains(fragment)) {
                                    INPUT.add(fragment);
                                    fragment = "";
                                } else if(next.equals(" ")) {
                                    INPUT.add(fragment);
                                    fragment = "";
                                } else if(SYMBOLS.contains(next)) {
                                    INPUT.add(fragment);
                                    fragment = "";
                                }
                            }
                        }
                    }
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
        for(String input : INPUT) {
            if (input.matches(KeywordRegex)){
                TOKENS.add(new Token(KEYWORD, input));
            } else if (input.matches(SymbolRegex)){
                if(input.equals("<")) {
                    TOKENS.add(new Token(SYMBOL, "&lt;"));
                } else if(input.equals(">")) {
                    TOKENS.add(new Token(SYMBOL, "&gt;"));
                } else if(input.equals("\"")) {
                    TOKENS.add(new Token(SYMBOL, "&quot;"));
                } else if(input.equals("&")) {
                    TOKENS.add(new Token(SYMBOL, "&amp;"));
                } else {
                    TOKENS.add(new Token(SYMBOL, input));
                }
            } else if (input.matches(IntegerRegex) && Integer.valueOf(input) >= 0 && Integer.valueOf(input) <= 32767){
                TOKENS.add(new Token(INT_CONST, input));
            } else if (input.matches(StringRegex)){
                TOKENS.add(new Token(STRING_CONST, input.substring(1, input.length()-1)));
            } else if (input.matches(IdentifierRegex)){
                TOKENS.add(new Token(IDENTIFIER, input));
            } else {
                throw new IllegalArgumentException("Unknown token: " + input);
            }
        }
        for(Token token : TOKENS) {
            System.out.println(token.XmlFormat());
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
        return INDEX != TOKENS.size();
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
        if(INDEX > 0) INDEX = INDEX - 1;
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