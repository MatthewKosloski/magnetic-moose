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

    // An instance of an interpreter. Is static final
    // because it is to be reused to store programa state.
    static final Interpreter interpreter = new Interpreter();

    // Indicates if there is a known error
    // and prevents the execution of the code.
    static boolean hadError = false;

    // Indicates if a RuntimeError was thrown while
    // the Interpreter was evaluating the program.
    static boolean hadRuntimeError = false;

    // Indicates whether the interpreter is running
    // in interactive mode or non-interactive mode (with
    // a file).
    static boolean isInteractive = false;

    // The name of the file when running in 
    // non-interactive mode.
    static String filename;

    public static void main(String[] args) throws IOException
    {
        if (args.length > 1) 
        {
            System.out.println("Usage: magneticmoose [script]");
            System.exit(64);
        }
        else if (args.length == 1)
        {
            filename = args[0];
            runFile(args[0]);
        }
        else
        {
            isInteractive = true;
            runPrompt();
        }
    }

    public static void error(String message, int line, int column)
    {
        displayErrorMessage("Error", message, line, column);
        hadError = true;
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
        List<Token> tokens = new Lexer(source).getTokens();

        // If the only token is EOF (no program), then don't try
        // to parse and interpret.
        if (tokens.size() != 1)
        {
            try
            {
                Expr expr = new Parser(tokens).parse();
                System.out.println(interpreter.interpret(expr)); 
            }
            catch (ParseError err)
            {
                displayErrorMessage(err);
                hadError = true;
            }
            catch (RuntimeError err)
            {
                displayErrorMessage(err);
                hadRuntimeError = true;
            }
        }
    }

    /*
     * Reads and executes the file at the given path. 
     * 
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
        if (hadRuntimeError) System.exit(70);
    }

    /*
     * Runs the interpreter in interactive mode, allowing
     * the user to type source language into the console
     * and execute it directly.
     * 
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

    private static void displayErrorMessage(String errorName, String message, 
        int line, int column)
    {
        if(isInteractive)
        {
            System.err.format("%s on column %d: %s\n", errorName, column,
                message);
        }
        else
        {
            System.err.format("%s:%d:%d: %s: %s\n", filename, line, column,
                errorName, message);
        }
    }

    private static void displayErrorMessage(InterpreterError err)
    {
        Token token = err.getToken();
        displayErrorMessage(err.getErrorName(), err.getMessage(),
            token.line, token.column);
    }
}