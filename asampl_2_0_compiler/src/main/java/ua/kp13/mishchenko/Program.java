package ua.kp13.mishchenko;

import ua.kp13.mishchenko.ast.Node;
import ua.kp13.mishchenko.ast.ProgramNode;

public class Program {

	public static void main(String[] args) {
		
		//char doubleQ = '"';
		//String code = "string a = \"test\"; string b = \" passed\"; string c = \"1\" + \"2\"; a = a + \"3\";";	
		//String code = "string ss = "+doubleQ+"ssaat 213"+doubleQ+"; string a1 = "+doubleQ+"a"+doubleQ+"; ss = ss + a;";
		
		//String code = "boolean a = true; boolean b = !false; boolean c = (a == b)&&false;";
		//String code = "string c =\"hello\"; string b = \"hello\"; boolean a = b == (\"hel\" + \"lo\");";
		//String code = "int c = 1; float b = 1.1; boolean a = b == c;";
		
		//String code = "boolean a = true; boolean b = true; if(a){int c = 54+45; if(b){int t = 0;} int d = 54+45; int d1 = 54+45;} int h = 1;";
		
		//IF TEST
		//String code = "int a = 5; int b = 6; if(a>b){a = a + b;} elif(a<b){a = b-a;} else {int a = 3;}";
		//String code = "int a =5; int b=6; if(true){if(a<b){int c = a; a = b; b = c; if(c==5){ a = 7;} } };";
		
		
		//String code = "int a = 5;";
		
		//String code = "boolean a = !(true == true) && false;";
		
		//String code = "float a = 7+5+8*4/12*(12+3)/0.05; float b = b+2; ";
		
		//WHILE TEST
		//String code = "int i = 0; int a = 0; while(i<10) {if(a>-1){a = a + 2;} i = i + 1; while(a>0){a = a-2;} int b = 6;}; ";
		//String code = "int c = 0; int i = 0; int a = 0; while(i<10) {a = a + 2; i = i + 1; if(a==14){a = a+10;} int j = 0;  while(j<10){c = c + 1; j = j+1;}}; ";
		
		
		//FOR TEST
		//String code = "int c = 0; int a = 0; for( int i = 0; i<10; i = i+1;) {a = a + 2; if(a==14){a = a+10;} for(int j = 0; j<10; j = j +1;){c = c + 1;}}; ";
		//String code = "int c = 0; for( int i = 0; i<10; i = i+1;){c = c + 1;} ";
		
		
		//FUNCTIONS TEST
		//String code = "int a=5; int b=6; int function sum(int a, int b){return a+b;}";
		//String code = "int a=5; int b=6;  int function sum(int a, int b){int c = a + b; return c;} int c = sum(a,b);";
		/*String code = "int function factorial(int n){ \n"
				+ "  if(n==0){\n"
				+ "  return 1;\n"
				+ "  } \n"
				+ "  int b = n - 1; \n"
				+ "  int mul = factorial(b); \n"
				+ "  int res = n * mul;\n "
				+ "	 return res;}\n"
				+ "int r = factorial(5);\n";*/
		
		//String code = "int function haha1(){return 5;} int b = haha1();";
		//String code = "int function decr(int n){if(n==0){return 0;} n = n - 1; int c = decr(n); return c;} int b = decr(3);";
		
		//String code = "int a = 432; int c; c = a;";
		
		
		//TIME TEST
		//String code = "time a = 12:47:00:00; time b = 13:13:13:13; time c = a + b; time d = b - a; time e = 24:00:00:00 - 23:00:00:10; boolean x = 24:00:00:00 == 23:00:00:00; ";
		
		
		//String code = "Boolean a = \"taras\" == \"taras\";";
		//String code = "int b = 5; int[] a = [1,2,3,3+1,b]; int[] c; c = a ; int x = a-c; ";
		
		//String code = "int[] a = [1,2,3,4,5]; int[] b = [5,6,7,8,9];  int[] c; c = b; c = uni(a,b);";
		
		//String code = "int[] a = [1,2,3,4,5,5,5]; int[] b = [5,6,7,8,9];  int[] c; c = b; c = dif(a,b);";
		//String code = "int[] a = [1,2,3,4,5]; int[] b = [5,6,7,8,9];  int[] c; c = b; c = sdif(a,b);";
		
		//String code = "int[] a = [1,2,3,4,5,5]; int[] b = [5,5,5,6,7,8,9];  int[] c; c = sec(a,b);";
		//String code = "int[] a = [1,2,3,4,5,5]; int[] b = [5,5,5,6,7,8,9];  int[] c; c = xsec(a,b);";
		
		//String code = "int[] a = [9,8,7,6,5,4,3,2,10]; int[] b = [5,5,5,6,7,8,9];  int[] c; c = ord(a);";
		//String code = "int[] a = [9,8,7,6,5,4,3,2,10]; int[] b = [5,5,5,6,7,8,9];  int[] c; c = asort(a);";
		//String code = "int[] a = [9,8,7,6,5,4,3,2,10]; int[] b = [5,5,5,6,7,8,9];  int[] c; c = dsort(a);";
		//String code = "String[] a = [\"x\",\"x\",\"x\",\"y\",\"y\",\"z\"]; int[] b = [5,5,5,6,7,8,9];  String[] c; c = singl(a);";
		//String code = "String[] a = [\"b\",\"x\",\"x\",\"c\",\"y\",\"a\"]; int[] b = [5,5,5,6,7,8,9];  String[] c; c = ord(a);";
		
		//String code = "int[] a = [1,2,3,4,5,5]; int[] b = [5,5,5,6,7,8,9];  int c; int i = 3; c = extr(a, i);";
		//String code = "int[] a = [1,2,3,4,5,5]; int[] b = [5,5,5,6,7,8,9];  int c; int i = 3; c = get(a, i);";
		//String code = "int[] a = [1,2,3,4,5,5];int b = 114; int i = 2; int c = ins(a, i, b);";
		//String code = "int b = 5;";
		
		String code = "int[] a = [9,8,7,6,5,4,3,2,10]; int[] b = [5,5,5,6,7,8,9];  Agregate c = [a,b];";
		
		Lexer lexer = new Lexer(code);
		
		
		
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
		
		System.out.println("###################");
		System.out.println("Code: ");
		System.out.println(code);
		System.out.println("###################");
		System.out.println("Abstract Syntax Tree:");
		
	    ast.printAST("");
	    System.out.println("statements amount: " + ((ProgramNode)ast).getStatements().size());
	    
	    
	    System.out.println("###################");
	    
	    Interpreter interpr = new Interpreter(ast);
	    interpr.run();
	}	
}
