package me.mtk.magneticmoose;

import java.util.ArrayList;
import java.util.List;

// The Lexer is the part of the interpreter that takes
// a source program (written in the language that is
// being interpreted) as input and outputs a sequence
// of Token objects. These tokens will be used by the
// Parser to construct an abstract syntax tree (AST).
public class Lexer
{
    
    // Stores the begin index and end index
    // of a line in source. That is, a line
    // is a substring of source.
    private class Line
    {
        private final int beginIndex;
        private final int endIndex;

        public Line(int beginIndex, int endIndex)
        {
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
        }

        public int getBeginIndex() { return beginIndex; }
        public int getEndIndex() { return endIndex; }
    }

    // The source program, written in the language being interpreted.
    private String source;

    // Stores the accumulated tokens, which are to be given to the 
    // Parser as input.
    private List<Token> tokens = new ArrayList<>();

    // Stores the lines of source.
    private List<Line> lines = new ArrayList<>();

    // The line in source that is currently being processed.
    private int currentLineNumber = 1;

    // The current column of the line in source that is currently
    // being processed. This is reset whenever a new line
    // whitespace character is encountered.
    private int currentColumnNumber = 0;

    // The index (in source) of the first character of the
    // lexeme currently being processed. This gets reset when
    // the Lexer begins constructing another Token.
    private int lexemeStart = 0;

    // The index (in source) of the first character of the current line.
    private int lineStart = 0;

    // The current position in the source string (an index in source).
    // This member can take on any value in the range [0, n - 1], where
    // n is the length of source. This is the index in source of the next
    // character that is to be processed. That is, the index of the character
    // that is currently being processed is one less than this value.
    private int position = 0;

    public Lexer(String source)
    {
        this.source = source;
    }

    /**
     * Scans the input program and returns the tokens.
     * 
     * @return A list of tokens obtained by scanning the source program.
     */
    public List<Token> getTokens()
    {
        while (!isEndOfFile())
        {
            // We are at the beginning of the next lexeme
            lexemeStart = position;

            scanToken();
        }

        lines.add(new Line(lineStart, position));

        // Append the end-of-file token to the list
        tokens.add(new Token(TokenType.EOF, "", null,
            currentLineNumber, ++currentColumnNumber));
        
        return tokens;
    }

    /**
     * Returns the nth line of source.
     * 
     * @param n 
     * @return
     */
    public String getLine(int n)
    {
        if ((n - 1) < 0 || (n - 1) > lines.size() - 1)
        {
            throw new IllegalArgumentException("Argument n must be an integer " +
                "in the range [0, m - 1], where m is the amount of " +
                "lines in the source program");
        }

        Line line = lines.get(n - 1);

        return source.substring(line.beginIndex, line.endIndex);
    }

    /*
     * Starting at the current position in the source program,
     * scans the source program for a Token with the help of
     * lookahead characters.
     */
    private void scanToken()
    {
        char currentChar = nextChar();

        switch (currentChar)
        {
            // Grouping characters
            case '(': addToken(TokenType.LPAREN, null); break;
            case ')': addToken(TokenType.RPAREN, null); break;

            // Binary arithmetic operators
            case '+': addToken(TokenType.PLUS, null); break;
            case '-': addToken(TokenType.MINUS, null); break;
            case '*': addToken(TokenType.STAR, null); break;

            // Comments and binary division operator
            case '/': 
                if (match('/')) 
                    consumeInlineComment();
                else if (match('*')) 
                    consumeBlockComment();
                else 
                    addToken(TokenType.SLASH, null);
                break;

            default:
                if (isDigit(currentChar))
                    number();
                else if (isWhitespace(currentChar))
                {
                    // Ignore whitespace
                }
                else
                {
                    addToken(TokenType.UNIDENTIFIED, null);
                }
                break;
        }
    }

    /*
     * Returns the next character of the source program that is to be
     * scanned by the Lexer and advances the position in the source program.
     * 
     * @return The next character to be scanned by the Lexer.
     */
    private char nextChar()
    {
        char nextChar = source.charAt(position++);
        
        if (nextChar == '\n')
        {
            lines.add(new Line(lineStart, position - 1));
            currentLineNumber++;
            currentColumnNumber = 0;
            lineStart = position;
        }
        else
            currentColumnNumber++;

        return nextChar;
    }

     /*
     * Returns the next character to be scanned (one character
     * of lookahead).
     * 
     * @return The first character of lookahead. 
     */
    private char peek()
    {
        // There is no next character, so return 
        // the null character.
        if (isEndOfFile()) return '\0';

        char peek = source.charAt(position);
        
        return peek;
    }

    /*
     * Returns the second character of lookahead.
     * 
     * @return The first character of lookahead. 
     */
    private char peekNext()
    {
        if (position + 1 >= source.length())
        {
            // There is no next character, so return 
            // the null character.
            return '\0';
        }
        
        char peekNext = source.charAt(position + 1);

        return peekNext;
    }

    /*
     * If the next character is c, then consume it and return true. 
     *  
     * @param c The character for which we would like to find a match.
     * @return True if the first character of lookahead is the provided
     * character c; False otherwise.
     */
    private boolean match(char c)
    {
        if (peek() == c)
        {
            nextChar();
            return true;
        }

        return false;
    }

    /*
     * If the next character is a (first character of lookahead)
     * and the next next character is b (second character of lookahead),
     * then consume the characters and return true.
     * 
     * @param a The first character for which we would like to find a match.
     * @param b The character after a for which we would like to find a match.
     * @return True if the first character of lookahead is a and the second
     * character of lookahead is b; False otherwise.
     */
    private boolean match(char a, char b)
    {
        if (peek() == a && peekNext() == b)
        {
            nextChar();
            nextChar();
            return true;
        }
        
        return false;
    }

    /*
     * Consumes an inline comment, silently advancing the position
     * in the source program. 
     */
    private void consumeInlineComment()
    {
        while (peek() != '\n' && !isEndOfFile()) nextChar();
    }

    /*
     * Consumes a C-style block comment, silently advancing the position
     * in the source program.
     */
    private void consumeBlockComment()
    {
        while (!match('*', '/')) nextChar();
    }

    /*
     * Handles the scanning of numbers, both integer
     * and decimal. 
     */
    private void number()
    {
        // Cache the column number at this point
        // because subsequent calls to nextChar()
        // will update the column number.
        int startColumn = currentColumnNumber;

        // Consume the integer, or if a decimal number,
        // the left-hand side.
        while (isDigit(peek())) nextChar();

        if (peek() == '.' && isDigit(peekNext()))
        {
            // Consume the decimal
            nextChar();

            // Consume the right hand side of the decimal
            while (isDigit(peek())) nextChar();
        }

        double literal = Double.parseDouble(getLexeme());
        addToken(TokenType.NUMBER, literal, currentLineNumber, startColumn);
    }

    /*
     * Indicates if the end of file has been reached. That is,
     * the character that is currently being processed is the last
     * character in the source program.
     * 
     * @return True if EOF; False otherwise.
     */
    private boolean isEndOfFile()
    {
        return position >= source.length();
    }

    /*
     * Returns the current lexeme being processed.
     *  
     * @return The lexeme being processed.
     */
    private String getLexeme()
    {
        return source.substring(lexemeStart, position);
    }

    /*
     * Adds a token to the accumulated list of tokens.
     * 
     * @param type The type of the token 
     * @param literal The literal value (if number)
     * @param line The line at which the token is located
     * @param column The starting column at which the token is located
     */
    private void addToken(TokenType type, Object literal, int line, int column)
    {
        Token token = new Token(type, getLexeme(), literal, line, column);
        tokens.add(token);
    }

    /*
     * Adds a token to the accumulated list of tokens.
     * 
     * @param type The type of the token 
     * @param literal The literal value (if number)
     */
    private void addToken(TokenType type, Object literal)
    {
        addToken(type, literal, currentLineNumber, currentColumnNumber);
    }

    /*
     * Indicates if the provided character is a digit as
     * specified by the regular expression [0-9].
     * 
     * @param c A character
     * @return True if c is a digit; False otherwise.
     */
    private boolean isDigit(char c)
    {
        return c >= '0' && c <= '9';
    }

    /**
     * Indicates if the provided character is a white space
     * character.
     * 
     * @param c A character 
     * @return True if c is white space; False otherwise.
     */
    private boolean isWhitespace(char c)
    {
        return c == ' ' || c == '\t' || c == '\r' || c == '\n';
    }
}