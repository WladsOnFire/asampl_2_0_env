package ua.kp13.mishchenko;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Scanner;

import ua.kp13.mishchenko.ast.Node;
import ua.kp13.mishchenko.ast.ProgramNode;
import ua.kp13.mishchenko.exceptions.CompilerException;
import ua.kp13.mishchenko.exceptions.InterpreterException;
import ua.kp13.mishchenko.exceptions.ParserException;

public class Program {

	// TRUE = TURN ON DEBUGGING FEATURE
	public static boolean isDebug = false;
	public static String codeFilePath = "C:\\Users\\vladislav\\Desktop\\code.txt";
	
	public static void main(String[] args) throws IOException, URISyntaxException {

		//UNCOMMENT BELOW IN ORDER TO RUN FROM JAR FILE
		if(args.length < 1) {
			System.out.println("ERROR. PROGRAM EXECUTION MAY INCLUDE TWO PARAMS: fileUrl[\"C:\\example.asamp\"], isDebug[true/false](optional)");
			return;
		} else if(args.length == 2) {
			isDebug = Boolean.parseBoolean(args[1]);
		}
		codeFilePath = args[0];
		//
		
		String code = "";
		try {
			code = new String(Files.readAllBytes(Paths.get(codeFilePath)));	
		} catch(NoSuchFileException ex) {
			System.out.println("File not found: " + ex.getMessage());
			return;
		}
		
		Lexer lexer = new Lexer(code);
		Parser parser = new Parser(lexer);

		Node ast = null;

		try {
			ast = parser.parse();
		} catch (ParserException e) {
			System.out.println(e.getMessage());
			return;
		}

		Interpreter interpr = new Interpreter(ast);

		if (isDebug) {
			System.out.println("###################");
			System.out.println("Code: ");
			System.out.println(code);
			System.out.println("###################");
			System.out.println("Abstract Syntax Tree:");

			ast.printAST("");
			System.out.println("statements amount: " + ((ProgramNode) ast).getStatements().size());

			try {
				interpr.debug();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

			System.out.println("###################");
			System.out.println("program end.");
			interpr.printVariablesMap(interpr.getVariableMap());
			interpr.printFunctionsMap();
		} else {
			try {
				interpr.run();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

	}
}
