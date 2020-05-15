package me.mtk.magneticmoose;

import java.util.List;

public class Parser 
{
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;

    // Represents the index in tokens of the next
    // token waiting to be parsed.
    private int current = 0;

    public Parser(List<Token> tokens)
    {
        this.tokens = tokens;
    }

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

    // program -> expression ;
    private Expr program()
    {
        return expression();
    }

    // expression -> literal | arithmetic ;
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

    // literal -> number ;
    private Expr literal()
    {
        return new Expr.Literal((double) previous().literal);
    }

    // arithmetic -> "(" operator expression (" " expression)+ ")" ;
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