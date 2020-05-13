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

    // The source program, written in the language being interpreted.
    private String source;

    // Stores the accumulated tokens, which are to be given to the 
    // Parser as input.
    private List<Token> tokens = new ArrayList<>();

    // The line in source that is currently being processed.
    private int currentLineNumber = 1;

    // The current column of the line in source that is currently
    // being processed. This is reset to 1 whenever a new line
    // whitespace character is encountered.
    private int currentColumnNumber = 1;

    // The index (in source) of the first character of the
    // lexeme currently being processed. This gets reset when
    // the Lexer begins constructing another Token.
    private int lexemeStart = 0;

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
        while(!isEndOfFile())
        {
            // We are at the beginning of the next lexeme
            lexemeStart = position;

            scanToken();
        }

        // Append the end-of-file token to the list
        tokens.add(new Token(TokenType.EOF, "", null,
            currentLineNumber, currentColumnNumber));
        
        return tokens;
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

            // Binary arithmetic characters
            case '+': addToken(TokenType.PLUS, null); break;
            case '-': addToken(TokenType.MINUS, null); break;
            case '*': addToken(TokenType.STAR, null); break;

            // New line character
            case '\n':
                currentLineNumber++;
                currentColumnNumber = 1;
                break;

            // Comment and binary division character
            case '/': 
                if (peek() == '/')
                {
                    // Current and next character are '/', denoting
                    // the start of a comment
                    consumeInlineComment();
                } 
                else
                {
                    // Division
                    addToken(TokenType.SLASH, null);
                }
                break;

            default:
                if (isDigit(currentChar))
                {
                    number();
                }
                else if (Character.isWhitespace(currentChar))
                {
                    currentColumnNumber++;
                }
                else
                {
                    // Unidentified character
                    String errMsg = String.format("Unexpected character \"%c\"" + 
                        " found", currentChar);
                    MagneticMoose.error(errMsg, currentLineNumber, 
                        currentColumnNumber); 
                    currentColumnNumber++;
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
        return source.charAt(position++);
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
        if(isEndOfFile()) return '\0';

        return source.charAt(position);
    }

    /*
     * Returns the second character of lookahead.
     * 
     * @return The first character of lookahead. 
     */
    private char peekNext()
    {
        if(position + 1 >= source.length())
        {
            // There is no next character, so return 
            // the null character.
            return '\0';
        }
    
        return source.charAt(position + 1);
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
        if(source.charAt(position) != c || isEndOfFile()) return false;

        consume();
        return true;
    }

    /*
     * Consumes a comment, silently advancing the position
     * in the source program. 
     */
    private void consumeInlineComment()
    {
        while (peek() != '\n' && !isEndOfFile()) consume();
    }

    /*
     * Silently advances the position in the source program.
     */
    private void consume()
    {
        nextChar();
        currentColumnNumber++;
    }

    /*
     * Handles the scanning of numbers, both integer
     * and decimal. 
     */
    private void number()
    {
        // Cache the column number at this point
        // because consume() updates the column.
        int startColumn = currentColumnNumber;

        // Consume the integer, or if a decimal number,
        // the left-hand side.
        while (isDigit(peek())) consume();

        if (peek() == '.' && isDigit(peekNext()))
        {
            // Consume the decimal
            consume();

            // Consume the right hand side of the decimal
            while (isDigit(peek())) consume();
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

        currentColumnNumber++;
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

}