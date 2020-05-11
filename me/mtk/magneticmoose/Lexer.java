package me.mtk.magneticmoose;

// The Lexer is the part of the interpreter that takes
// a source program (written in the language that is 
// being interpreted) as input and outputs a sequence 
// of Token objects. These tokens will be used by the
// Parser to construct an abstract syntax tree (AST).
public class Lexer
{

    // The raw input characters from the program
    private String source;

    public Lexer(String source)
    {
        this.source = source;
        System.out.println(this.source);
    }

}