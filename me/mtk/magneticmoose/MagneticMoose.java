package me.mtk.magneticmoose;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class MagneticMoose
{

    // Indicates if there is a known error
    // and prevents the execution of the code.
    static boolean hadError = false;

    public static void main(String[] args) throws IOException
    {
        if (args.length > 1) 
        {
            System.out.println("Usage: magneticmoose [script]");
            System.exit(64);
        }
        else if (args.length == 1)
            runFile(args[0]);
        else
            runPrompt();
    }

    /**
     * Reports an error message to the user.
     *
     * @param message A custom message describing the error.
     * @param line The line number where the error is located.
     * @param column The column number where the error is located.
     */
    public static void error(String message, int line, int column)
    {
        System.err.format("Error on line %d, column %d: %s.\n", line, 
            column, message);
        hadError = true;
    }

    /*
     * Reads and executes the file at the given path. 
     * @param path A path to a file.
     * @throws IOException 
     */
    private static void runFile(String path) throws IOException
    {
        // Read the input file and construct a String object
        // from the contents.
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String source = new String(bytes, Charset.defaultCharset());

		run(source);
		
		// Indicate an error in the exit code.
		if (hadError) System.exit(65);
    }

    /*
     * Runs the interpreter in interactive mode, allowing
     * the user to type source language into the console
     * and execute it directly.
     * @throws IOException
     */
    private static void runPrompt() throws IOException
    {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while (true)
        {
            System.out.print("> ");               
			run(reader.readLine());
			hadError = false;
        }
    }

    /**
     * Scans the source program for tokens,
     * creates an AST from the tokens, and 
     * executes the AST.
     * 
     * @param source A source program writtin in the
     * language being interpreted.
     */
    private static void run(String source)
    {
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.getTokens();

        // Print the tokens for now
        for (Token token : tokens)
            System.out.println(token);
    }
}