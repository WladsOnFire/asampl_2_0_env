package ua.kp13.mishchenko;

import ua.kp13.mishchenko.ast.Node;
import ua.kp13.mishchenko.ast.ProgramNode;

public class Program {

	public static void main(String[] args) {
		
		//char doubleQ = '"';
		//String code = "string a = \"test\"; string b = \" passed\"; string c = \"1\" + \"2\"; a = a + \"3\";";	
		//String code = "string ss = "+doubleQ+"ssaat 213"+doubleQ+"; string a1 = "+doubleQ+"a"+doubleQ+"; ss = ss + a;";
		
		//String code = "boolean a = true; boolean b = !false; boolean c = (a == b)&&false;";
		//String code = "string c =\"taras\"; string b = \"taras\"; boolean a = b == (\"ta\" + \"ras\");";
		//String code = "int c = 1; float b = 1.1; boolean a = b == c;";
		
		//String code = "boolean a = true; boolean b = true; if(a){int c = 54+45; if(b){int t = 0;} int d = 54+45; int d1 = 54+45;} int h = 1;";
		
		
		
		
		//IF TEST
		//String code = "int a = 5; int b = 6; if(a>b){a = a + b;} elif(a<b){a = b-a;} else {int a = 3;}";
		String code = "int a =5; int b=6; if(true){if(a<b){int c = a; a = b; b = c; if(c==5){ a = 7;} } };";
		
		
		//String code = "int a = 5;";
		
		//String code = "boolean a = !(true == true) && false;";
		
		//String code = "float a = 7+5+8*4/12*(12+3)/0.05; float b = b+2; ";
		
		Lexer lexer = new Lexer(code);
		//int a = 24 * 4;
		//float c = 25*2.4;
		
		/*for(int i =0; i<12; i++) {
			
			try {
				
				lexer.nextToken();
				System.out.println(lexer.getCurrentToken().getValue());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		
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
