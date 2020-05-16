package me.mtk.magneticmoose;

import java.util.List;

// The Parser is the part of the interpreter that takes
// a list of Token objects as input and, from those tokens, 
// constructs an abstract syntax tree (AST). The construction
// of the AST is based on the grammar of the language of 
// the source program. That is, the Parser is an implementation
// of the grammar. Each nonterminal symbol of the grammar
// is implemented as a method.
public class Parser 
{
    private static class ParseError extends RuntimeException {}

    // The tokens of the source program. These come from
    // the Lexer.
    private final List<Token> tokens;

    // Represents the index in tokens of the next
    // token waiting to be parsed.
    private int current = 0;

    /**
     * Constructs a new Parser object, initializing
     * it with a list of tokens.
     * 
     * @param tokens A list of tokens.
     */
    public Parser(List<Token> tokens)
    {
        this.tokens = tokens;
    }

    /**
     * Parses the program.
     * 
     * @return A tree of expressions representing the program.
     */
    public Expr parse()
    {
        try
        {
            return program();
        }
        catch(ParseError err)
        {
            return null;
        }
    }

    /*
     * Implements the following production rule:
     * program -> expression ;
     *
     * @return An expression.
     */
    private Expr program()
    {
        return expression();
    }

    /*
     * Implements the following production rule:
     * expression -> literal | arithmetic ;
     *
     * @return An expression.
     */
    private Expr expression()
    {
        if (match(TokenType.NUMBER))
        {
            // expression -> literal ;
            return literal();
        }
        else if (match(TokenType.LPAREN))
        {
            // expression -> arithmetic ;
            return arithmetic();
        }

        throw error(peek(), "Expected expression.");
    }

    /*
     * Implements the following production rule:
     * literal -> number ;
     *
     * @return An expression.
     */
    private Expr literal()
    {
        return new Expr.Literal((double) previous().literal);
    }

    /*
     * Implements the following production rule:
     * arithmetic -> "(" operator expression (" " expression)+ ")" ;
     *
     * @return An expression.
     */
    private Expr arithmetic()
    {
        Token operator = advance();
        Expr first = expression();
        Expr second = expression();
        consume(TokenType.RPAREN, "Expected ')' after expression.");
        
        return new Expr.Arithmetic(operator, first, second);
    }

    private boolean match(TokenType... types)
    {
        for (TokenType type : types)
        {
            if (check(type))
            {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TokenType type, String msg)
    {
        if (check(type)) return advance();
        throw error(peek(), msg);
    }

    private boolean check(TokenType type)
    {
        return peek().type == type;
    }

    private Token advance()
    {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd()
    {
        return peek().type == TokenType.EOF;
    }

    private Token peek()
    {
        return tokens.get(current);
    }

    private Token previous()
    {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String msg)
    {
        MagneticMoose.error(msg, token.line, token.column);
        return new ParseError();
    }
}