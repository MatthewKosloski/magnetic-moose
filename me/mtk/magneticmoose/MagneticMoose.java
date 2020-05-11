package me.mtk.magneticmoose;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MagneticMoose
{

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

    private static void runPrompt() throws IOException
    {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while(true)
        {
            System.out.print("> ");               
			run(reader.readLine());
			hadError = false;
        }
    }

    private static void run(String source)
    {
        Lexer lexer = new Lexer(source);
    }

}