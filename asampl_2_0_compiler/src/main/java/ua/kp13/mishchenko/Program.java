package ua.kp13.mishchenko;

import ua.kp13.mishchenko.ast.Node;
import ua.kp13.mishchenko.ast.ProgramNode;

public class Program {

	public static void main(String[] args) {
		
		
		
		char doubleQ = '"';
		//String code = "string a = \"test\"; string b = \" passed\"; string c = \"1\" + \"2\"; string d = a + \"3\";";	
		//String code = "string ss = "+doubleQ+"ssaat 213"+doubleQ+"; string a1 = "+doubleQ+"a"+doubleQ+"; ss = ss + a;";
		//String code = "boolean a = true; boolean b = false;";
		
		String code = "float a = 7+5+8*4/12*(12+3)/0.05; float b = a+2; ";
		
		Lexer lexer = new Lexer(code);
		//int a = 24 * 4;
		//float c = 25*2.4;
		
	
		Parser parser = new Parser(lexer);
		
		Node ast = null;
		
		try {
			ast = parser.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Abstract Syntax Tree:");
		
	    ast.printAST("");
	    System.out.println("statements amount: " + ((ProgramNode)ast).getStatements().size());
	    
	    
	    System.out.println("###################");
	    
	    Interpreter interpr = new Interpreter(ast);
	    interpr.run();
	}

	
	
	
	
}
