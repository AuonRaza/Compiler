import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class compiler {
    public static int l = 0, k = 4, linno, flag3 = 0;
    public static ArrayList<String> symbolTable2 = new ArrayList<>();
    public String expr = "";
    private int indexOfInput = -1;
    public static Stack<String> stk = new Stack<String>();
    public String[][] ruleTable = {{"E$", null, null, "E$", null, null},
            {"TK", null, null, "TK", null, ""},
            {null, "+TK", null, null, "", ""},
            {"FH", null, null, "FH", null, null},
            {null, "", "*FH", null, "", ""},
            {"a", null, null, "(E)", null, null}};
    String[] nonTerms = {"G", "E", "K", "T", "H", "F"};
    String[] terms = {"a", "+", "*", "(", ")", "$"};
    public static String[][] symbolTable = {{"int", "int", "0", "yes"},
            {"char", "char", "1", "yes"},
            {"string", "String", "2", "yes"},
            {"if", "if", "3", "yes"},
            {"else", "else", "4", "yes"},
            {"do", "do", "5", "yes"},
            {"while", "while", "6", "yes"},
            {"any identifier", "id", "(next value)", "yes"},
            {"any string value in double quotes", "sl", "(next value)", "yes"},
            {"any int value", "in", "(next value)", "yes"},
            {"<", "ro", "lt", "no"},
            {"<=", "ro", "le", "no"},
            {"==", "ro", "eq", "no"},
            {"<>", "ro", "ne", "no"},
            {">=", "ro", "ge", "no"},
            {">", "ro", "gt", "no"},
            {"+", "ao", "ad", "no"},
            {"-", "ao", "sb", "no"},
            {"*", "ao", "ml", "no"},
            {"/", "ao", "dv", "no"},
            {"=", "oo", "as", "no"},
            {"{", "oo", "op", "no"},
            {"}", "oo", "cp", "no"},
            {"{", "oo", "ob", "no"},
            {"}", "oo", "cb", "no"},
            {";", "oo", "tr", "no"}};

    public compiler(String in) {
        this.expr = in;
    }

    public void algorithm() {
        push(this.expr.charAt(0) + "");
        push("G");
        String token = read();
        String top = null;
        do {
            top = this.pop();
            if (isNonTerminal(top)) {
                String rule = this.rule(top, token);
                this.pushRule(rule);
            } else if (isTerminal(top)) {
                if (!top.equals(token)) {
                    System.out.println("The token is non-coherent, By Grammar rule; \nToken: (" + token + ")");
                } else {
                    System.out.println("Matching: Terminal :( " + token + " )");
                    token = read();
                }
            } else {
                System.out.println("Never Happens , Because top : ( " + top + " )");
            }
            if (token.equals("$")) {
                break;
            }
        } while (true);
        if (token.equals("$")) {
            System.out.println("Compiled Successfully");
        } else {
            System.out.println("Compilation Error: Unsuccessful");
        }
    }

    private void pushRule(String rule) {
        for (int i = rule.length() - 1; i >= 0; i--) {
            char ch = rule.charAt(i);
            String str = String.valueOf(ch);
            push(str);
        }
    }

    private boolean isNonTerminal(String s) {
        for (int i = 0; i < this.nonTerms.length; i++) {
            if (s.equals(this.nonTerms[i])) {
                return true;
            }
        }
        return false;
    }

    private boolean isTerminal(String str) {
        for (int i = 0; i < this.terms.length; i++) {
            if (str.equals(this.terms[i])) {
                return true;
            }
        }
        return false;
    }

    private String read() {
        indexOfInput++;
        char ch = this.expr.charAt(indexOfInput);
        String str = String.valueOf(ch);

        return str;
    }

    private void push(String s) {
        this.stk.push(s);
    }

    private String pop() {
        return this.stk.pop();
    }

    public String rule(String non, String term) {
        int row = nonTermIX(non);
        int column = termIX(term);
        String rule = this.ruleTable[row][column];
        if (rule == null) {
            System.out.println("There is no Rule by this , Non-Terminal(" + non + ") ,Terminal(" + term + ") ");
        }
        return rule;
    }

    private void error(String message) {
        System.out.println(message);
        throw new RuntimeException(message);
    }

    private int termIX(String term) {
        for (int i = 0; i < this.terms.length; i++) {
            if (term.equals(this.terms[i])) {
                return i;
            }
        }
        System.out.println(term + " is Non-Terminal");
        return -1;
    }

    private int nonTermIX(String non) {
        for (int i = 0; i < this.nonTerms.length; i++) {
            if (non.equals(this.nonTerms[i])) {
                return i;
            }
        }
        System.out.println(non + " is Terminal");
        return -1;
    }

    static boolean Keyword(String str) {
        String keywords[] = {"int", "char", "string", "if", "else", "do", "while"};
        if (!Character.isLowerCase(str.charAt(0))) {
            return false;
        }
        for (int i = 0; i < 6; i++) {
            if (str.matches(keywords[i])) {
                l = i;
                return true;
            }
        }
        return false;
    }

    static boolean chString(String str) {
        int stCheck = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '"') {
                stCheck++;
                System.out.println("value of stCheck =" + stCheck);
            }
        }
        if (stCheck == 2) {
            return true;
        } else {
            return false;
        }
    }

    static void tokenize(String str, int lineNo) {
        String lexeme = "", err1 = "";
        flag3 = 0;
        int flag4 = 0;
        for (int i = 0; i < str.length(); i++) {
            if ((Character.isLetter(str.charAt(i)) || str.charAt(i) == '"') && flag3 == 0) {
                lexeme = lexeme + str.charAt(i++);
                while (i < str.length() && (Character.isLetterOrDigit(str.charAt(i)) || str.charAt(i) == '_') && flag3 == 0 || str.charAt(i) == '"') {
                    lexeme = lexeme + str.charAt(i++);
                }
                i--;
                if (Keyword(lexeme)) {
                    if (!symbolTable2.contains(lexeme)) {
                        System.out.println(lexeme + " is a keyword.");
                        symbolTable2.add(symbolTable[l][2]);
                        symbolTable2.add(symbolTable[l][1]);
                        symbolTable2.add("-");
                        symbolTable2.add("-");
                    }
                } else if (chString(lexeme)) {
                    System.out.println(lexeme + " is a String");
                } else {
                    if (flag4 == 1) {
                        System.out.println("ERROR:\n At line no: " + lineNo);
                        System.out.println(err1 + lexeme + " Is not a Keyword, String Literal or an Identifier");
                    } else {
                        if (!symbolTable2.contains(lexeme)) {
                            System.out.println(lexeme + " is an identifier");
                            symbolTable2.add(Integer.toString(k));
                            symbolTable2.add("id");
                            symbolTable2.add("-");
                            symbolTable2.add(lexeme);
                            k++;
                        }
                    }
                }
                lexeme = "";
            } else if (Character.isDigit(str.charAt(i)) && flag3 == 0) {
                int flag1 = 0, flag2 = 0;
                lexeme += str.charAt(i++);
                while (i < str.length() && (Character.isDigit(str.charAt(i)) || str.charAt(i) == '/' || str.charAt(i) == '*' || str.charAt(i) == '+' || str.charAt(i) == '-') && flag3 == 0) {
                    if (Character.isDigit(str.charAt(i))) {
                        lexeme += str.charAt(i++);
                        if (Character.isLetter(str.charAt(i + 1))) {
                            flag1 = 1;
                            flag4 = flag1;
                        }
                    }
                    else if (flag2 == 0 && i < str.length() - 1) {
                        if (Character.isDigit(str.charAt(i + 1))) {
                            flag2 = 1;
                            lexeme += str.charAt(i++);
                        } else if ((str.charAt(i + 1) == '+' || str.charAt(i + 1) == '-' || str.charAt(i + 1) == '*' || str.charAt(i + 1) == '/') && i < str.length() - 2) {
                            if (Character.isDigit(str.charAt(i + 2))) {
                                flag2 = 1;
                                lexeme += str.charAt(i++);
                            }
                        } else {
                            break;
                        }
                    } else if ((str.charAt(i) == '+' || str.charAt(i) == '-' || str.charAt(i + 1) == '*' || str.charAt(i + 1) == '/') && i < str.length() - 1) {
                        if (Character.isDigit(str.charAt(i + 1))) {
                            lexeme += str.charAt(i++);
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                i--;
                if (flag1 == 1) {
                    err1 = lexeme;
                } else if (flag1 == 0) {
                    System.out.println("int " + lexeme + " num ");
                }
                lexeme = "";
            } else if (str.charAt(i) == '<') {
                if (str.charAt(i + 1) == '=') {
                    System.out.println(symbolTable[11][0] + " " + symbolTable[11][1] + " " + symbolTable[11][2] + " " + symbolTable[11][3] + " ");
                } else if (str.charAt(i + 1) == '>') {
                    System.out.println(symbolTable[13][0] + " " + symbolTable[13][1] + " " + symbolTable[13][2] + " " + symbolTable[13][3] + " ");
                } else {
                    System.out.println(symbolTable[10][0] + " " + symbolTable[10][1] + " " + symbolTable[10][2] + " " + symbolTable[10][3] + " ");
                }
                lexeme = "";
            } else if (str.charAt(i) == '=') {
                if (str.charAt(i + 1) == '=') {
                    System.out.println(symbolTable[12][0] + " " + symbolTable[12][1] + " " + symbolTable[12][2] + " " + symbolTable[12][3] + " ");
                } else {
                    System.out.println(symbolTable[20][0] + " " + symbolTable[20][1] + " " + symbolTable[20][2] + " " + symbolTable[20][3] + " ");
                }
                lexeme = "";
            } else if (str.charAt(i) == '>') {
                if (str.charAt(i + 1) == '=') {
                    System.out.println(symbolTable[14][0] + " " + symbolTable[14][1] + " " + symbolTable[14][2] + " " + symbolTable[14][3] + " ");
                } else {
                    System.out.println(symbolTable[15][0] + " " + symbolTable[15][1] + " " + symbolTable[15][2] + " " + symbolTable[15][3] + " ");
                }
                lexeme = "";
            } else if (str.charAt(i) == '+') {
                System.out.println(symbolTable[16][0] + " " + symbolTable[16][1] + " " + symbolTable[16][2] + " " + symbolTable[16][3] + " ");
            } else if (str.charAt(i) == '-') {
                System.out.println(symbolTable[17][0] + " " + symbolTable[17][1] + " " + symbolTable[17][2] + " " + symbolTable[17][3] + " ");
            } else if (str.charAt(i) == '*') {
                if (str.charAt(i + 1) == '/') {
                    System.out.println("comment ends here");
                    flag3 = 0;
                } else {
                    System.out.println(symbolTable[18][0] + " " + symbolTable[18][1] + " " + symbolTable[18][2] + " " + symbolTable[18][3] + " ");
                }
            } else if (str.charAt(i) == '/') {
                if (str.charAt(i+1) == '*') {
                    System.out.println("comment starts here");
                    flag3 = 1;
                } else if (str.charAt(i+1) == '/') {
                    System.out.println("single line comment starts here");
                    flag3 = 1;
                } else if (str.charAt(i+1) != '/') {
                    System.out.println("ERROR:\nLine no: " + lineNo + "--> Unexpected token : '" + str.charAt(i)+"'");
                    System.out.println("// EXPECTED!");
                    flag3=1;
                } else {
                    System.out.println(symbolTable[19][0] + " " + symbolTable[19][1] + " " + symbolTable[19][2] + " " + symbolTable[19][3] + " ");
                }
            } else if (str.charAt(i) == '(') {
                System.out.println(symbolTable[21][0] + " " + symbolTable[21][1] + " " + symbolTable[21][2] + " " + symbolTable[21][3] + " ");
            } else if (str.charAt(i) == ')') {
                System.out.println(symbolTable[22][0] + " " + symbolTable[22][1] + " " + symbolTable[22][2] + " " + symbolTable[22][3] + " ");
            } else if (str.charAt(i) == '{') {
                System.out.println(symbolTable[23][0] + " " + symbolTable[23][1] + " " + symbolTable[23][2] + " " + symbolTable[23][3] + " ");
            } else if (str.charAt(i) == '}') {
                System.out.println(symbolTable[24][0] + " " + symbolTable[24][1] + " " + symbolTable[24][2] + " " + symbolTable[24][3] + " ");
            } else if (str.charAt(i) == ';') {
                System.out.println(symbolTable[25][0] + " " + symbolTable[25][1] + " " + symbolTable[25][2] + " " + symbolTable[25][3] + " ");
            } else if (str.charAt(i) != ' ' && flag3 == 0) {
                System.out.println("ERROR:\nLine no: " + lineNo + "--> Unexpected token : '" + str.charAt(i)+"'");
            }
        }
    }

    public static String isOperand(String str) {
        String lexeme="", err1="", exp="";
        flag3 = 0;
        int flag4 = 0;
        for (int i = 0; i < str.length(); i++) {
            if ((Character.isLetter(str.charAt(i)) || str.charAt(i) == '"') && flag3 == 0) {
                lexeme = lexeme + str.charAt(i++);
                while (i < str.length() && (Character.isLetterOrDigit(str.charAt(i)) || str.charAt(i) == '_')) {
                    lexeme = lexeme + str.charAt(i++);
                }
                i--;
                if (Keyword(lexeme)) {
                    break;
                } else if (chString(lexeme)) {
                    System.out.println(lexeme + " is a String");
                } else {
                    if (flag4 == 1) {
                        System.out.println(err1 + lexeme + " Is not a Keyword, String Literal or an Identifier x2");
                    } else {
                        System.out.println(lexeme + " is an identifier (PARSER)");
                        k++;
                        exp = exp + "a";
                    }
                }
                lexeme = "";
            } else if (str.charAt(i) == '+' || str.charAt(i) == '/' || str.charAt(i) == '*' || str.charAt(i) == '-' || str.charAt(i) == '(' || str.charAt(i) == ')') {
                exp = exp + str.charAt(i);
            } else if (str.charAt(i) == ' ') {
                break;
            }
        }
        return exp;
    }

    public static void main(String[] args) throws FileNotFoundException {
        char choice = ' ';
        int a = 0; // counter for index array of idUsed[]
        System.out.println("THE COMPILER");
        System.out.println("\nChoose your desired character code: \n");
        System.out.println("L - Read input source code to perfom Lexiacl Analysis Only.");
        System.out.println("S - Read input to perfom Lexical Analysis and Syntax Analysis both.");
        System.out.println("X - Exit");
        Scanner sc = new Scanner(System.in);
        System.out.print("Choice: ");
        choice = sc.next().charAt(0);
        if (choice == 'L' || choice == 'l') {
            String line;
            k=7;
            try {
                FileReader fileReader = new FileReader("input.txt");
                try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                    int i = 1, p = 0;
                    while ((line = bufferedReader.readLine()) != null) {
                        linno = i++;
                        System.out.println(line);
                        tokenize(line, linno);
                    }
                    System.out.println("\nSYMBOL TABLE\nAttribute Value\t|Token Name\t|Type\t|Value");
                    Iterator<String> tb = symbolTable2.iterator();
                    while (tb.hasNext()) {
                        p++;
                        if (p % 4 == 1) {
                            System.out.print("   \t\t\t");
                        }
                        System.out.print(tb.next() + "\t\t");
                        if ((p % 4) == 0) {
                            System.out.println();
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println("ERROR:\n';' expected at line no: " + linno);
                System.out.println();
            }
        } else if (choice == 'S' || choice == 's') {
            String line="", stmt, exp;
            System.out.println("Enter Equation/Line To Perform Syntax Analysis(NO GAPS)");
            stmt = sc.next();
            exp = isOperand(stmt);
            System.out.println();
            System.out.println("Converted Expression: " + exp);
            try {
                FileReader fileReader = new FileReader("input.txt");
                try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                    int i = 1, p = 0;
                    while ((line = bufferedReader.readLine()) != null) {
                        linno = i++;
                        tokenize(line, linno);
                    }
                    compiler parser = new compiler(exp + "$");//i*i+(i+i)$
                    parser.algorithm();
                    System.out.println("\nSYMBOL TABLE\nAttribute Value\t|Token Name\t|Type\t|Value");
                    Iterator<String> tb = symbolTable2.iterator();
                    while (tb.hasNext()) {
                        p++;
                        if (p % 4 == 1) {
                            System.out.print("   \t\t\t");
                        }
                        System.out.print(tb.next() + "\t\t");
                        if ((p % 4) == 0) {
                            System.out.println();
                        }
                    }
                }
            } catch (Exception ex) {
            }
        }
    }
}
