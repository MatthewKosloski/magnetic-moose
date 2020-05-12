package me.mtk.magneticmoose;

enum TokenType 
{
    // Grouping tokens
    LPAREN, RPAREN,

    // Binary arithmetic tokens
    PLUS, MINUS, STAR,
    
    // Comment and binary division token
    SLASH,

    // Literal token
    NUMBER,

    // End of file token
    EOF
}