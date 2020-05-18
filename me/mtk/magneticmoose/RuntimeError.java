package me.mtk.magneticmoose;

public class RuntimeError extends RuntimeException 
{
    final Token token;
    
    /**
     * Constructs a new RuntimeError.
     * 
     * @param token The token that caused the error.
     * @param msg The error message.
     */
    public RuntimeError(Token token, String msg)
    {
        super(msg);
        this.token = token;
    }
}