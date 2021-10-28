import java.io.*;
import java.util.regex.*;
import java.util.ArrayList;

public class JackTokenizer {

    private FileReader fileReader;
    private BufferedReader bufferedReader;
    private ArrayList<String> input;

    private int index;
    private Token token;
    private ArrayList<Token> tokens;

    private ArrayList<String> keywords;
    private ArrayList<String> symbols;
    private ArrayList<String> operators;

    public String keywordRegex = "";
    public String symbolRegex = "";
    public String integerRegex = "";
    public String stringRegex = "";
    public String identifierRegex = "";

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

    public JackTokenizer(File file) {
        index = 0;
        input = new ArrayList<String>();
        tokens = new ArrayList<Token>();
        keywords = new ArrayList<String>();
        symbols = new ArrayList<String>();
        operators = new ArrayList<String>();
        String[] KEYWORDS = { CLASS, CONSTRUCTOR, FUNCTION, METHOD, FIELD, STATIC, VAR, INT, CHAR, BOOLEAN, VOID, TRUE,
                FALSE, NULL, THIS, LET, DO, IF, ELSE, WHILE, RETURN };
        String[] SYMBOLS = { "{", "}", "(", ")", "[", "]", ".", ",", ";", "+", "-", "*", "/", "&", "|", "<", ">", "=",
                "~" };
        String[] OPERATORS = { "+", "-", "*", "/", "&", "|", "<", ">", "=", "&lt;", "&gt;", "&amp;", "&quot;" };
        for (String KEYWORD : KEYWORDS) {
            keywords.add(KEYWORD);
            keywordRegex += KEYWORD + "|";
        }
        symbolRegex += "[";
        for (String SYMBOL : SYMBOLS) {
            symbols.add(SYMBOL);
            symbolRegex += "\\" + SYMBOL;
        }
        symbolRegex += "]";
        for (String OPERATOR : OPERATORS) {
            operators.add(OPERATOR);
        }
        integerRegex = "^[0-9]{1,5}$";
        stringRegex = "\"[^\"\n]*\"";
        identifierRegex = "[\\w_]+";
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);

            String value = "";
            String next = "";
            String fragment = "";
            boolean isString = false;
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (!line.equals("") && !line.startsWith("//") && !line.startsWith("/*") && !line.startsWith("/**")
                        && !line.startsWith("*")) {
                    for (int i = 0; i < line.length(); i++) {
                        value = String.valueOf(line.charAt(i));
                        if (i < line.length() - 1)
                            next = String.valueOf(line.charAt(i + 1));
                        if (value.equals(" ") || value.equals("\t")) {
                            continue;
                        } else if (value.equals("/") && next.equals("/")) {
                            break;
                        } else if (value.equals("\"") && !isString) {
                            isString = true;
                            while (isString) {
                                fragment = fragment + String.valueOf(line.charAt(i));
                                i = i + 1;
                                if (String.valueOf(line.charAt(i)).equals("\"")) {
                                    isString = false;
                                }
                            }
                            fragment = fragment + "\"";
                            input.add(fragment);
                            fragment = "";
                        } else {
                            if (operators.contains(value) || symbols.contains(value)) {
                                input.add(value);
                            } else {
                                fragment = fragment + value;
                                if (keywords.contains(fragment) && next.equals(" ")) {
                                    input.add(fragment);
                                    fragment = "";
                                } else if (next.equals(" ")) {
                                    input.add(fragment);
                                    fragment = "";
                                } else if (symbols.contains(next)) {
                                    input.add(fragment);
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
        analyze();
    }

    public void analyze() {
        for (String token : input) {
            if (token.matches(keywordRegex)) {
                tokens.add(new Token(KEYWORD, token));
            } else if (token.matches(symbolRegex)) {
                if (token.equals("<")) {
                    tokens.add(new Token(SYMBOL, "&lt;"));
                } else if (token.equals(">")) {
                    tokens.add(new Token(SYMBOL, "&gt;"));
                } else if (token.equals("\"")) {
                    tokens.add(new Token(SYMBOL, "&quot;"));
                } else if (token.equals("&")) {
                    tokens.add(new Token(SYMBOL, "&amp;"));
                } else {
                    tokens.add(new Token(SYMBOL, token));
                }
            } else if (token.matches(integerRegex) && Integer.valueOf(token) >= 0 && Integer.valueOf(token) <= 32767) {
                tokens.add(new Token(INT_CONST, token));
            } else if (token.matches(stringRegex)) {
                tokens.add(new Token(STRING_CONST, token.substring(1, token.length() - 1)));
            } else if (token.matches(identifierRegex)) {
                tokens.add(new Token(IDENTIFIER, token));
            } else {
                throw new IllegalArgumentException("Unknown token: " + token);
            }
        }
    }

    public Token getToken() {
        return token;
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public String getType() {
        return token.getType();
    }

    public String getValue() {
        return token.getValue();
    }

    public boolean hasMoreTokens() {
        return index != tokens.size();
    }

    public void advance() {
        if (hasMoreTokens()) {
            token = tokens.get(index);
            index = index + 1;
        } else {
            throw new IllegalStateException("No more tokens");
        }
    }

    public void back() {
        if (index > 0)
            index = index - 1;
    }

    public boolean isOperator() {
        return operators.contains(token.getValue());
    }

    public Token classify(String value) {
        if (keywords.contains(value)) {
            return new Token(KEYWORD, value);
        } else if (symbols.contains(value)) {
            if (value.equals("<")) {
                return new Token(SYMBOL, "&lt;");
            } else if (value.equals(">")) {
                return new Token(SYMBOL, "&gt;");
            } else if (value.equals("\"")) {
                return new Token(SYMBOL, "&quot;");
            } else if (value.equals("&")) {
                return new Token(SYMBOL, "&amp;");
            } else {
                return new Token(SYMBOL, value);
            }
        } else if (Pattern.matches("^\".+\"$", value)) {
            return new Token(STRING_CONST, value.substring(1, value.length() - 1));
        } else if (Pattern.matches("^[0-9]{1,5}$", value) && Integer.valueOf(value) >= 0
                && Integer.valueOf(value) <= 32767) {
            return new Token(INT_CONST, value);
        } else {
            return new Token(IDENTIFIER, value);
        }
    }
}