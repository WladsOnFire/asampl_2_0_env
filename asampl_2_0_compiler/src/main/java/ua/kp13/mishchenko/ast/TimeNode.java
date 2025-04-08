package ua.kp13.mishchenko.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ua.kp13.mishchenko.Token;

public class TimeNode extends Node{

	private final Token token;

    public TimeNode(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
	
    public static String sum(String time1, String time2) {
    	String[] time1StringArr = time1.split(":");
    	String[] time2StringArr = time2.split(":");
    	
    	List<Integer> time1Values = new ArrayList<Integer>();
    	List<Integer> time2Values = new ArrayList<Integer>();
    	
    	for(String value : time1StringArr) {
    		time1Values.add(Integer.parseInt(value));
    	}
    	
    	for(String value : time2StringArr) {
    		time2Values.add(Integer.parseInt(value));
    	}
    	
    	
    	List<Integer> resultList = new ArrayList<Integer>();
    	int diff = 0;
    	for(int i = 3; i>-1; i--) {
    		int sum = time1Values.get(i) + time2Values.get(i) + diff;
    		diff = 0;
    		if(i == 3 && sum>=100) { //Ms
    			sum -= 100;
    			diff = 1;
    		}
    		else if((i == 2 || i == 1) && sum>=60) { //Sec and Min
    			sum -= 60;
    			diff = 1;
    		}
    		else if(i == 0 && sum>= 24) { //hrs
    			sum = sum - 24;
    		}
    		resultList.add(sum);
    	}
    	
    	Collections.reverse(resultList);
    	
    	String result = "";
    	for(int i=0; i<4; i++) {
    		String value = resultList.get(i) + "";
    		while(value.length() != 2) {
    			value = "0" + value;
    		}
    		result +=  value;
    		
    		if(i!=3) result += ":";
    	}
    	return result;
    }
    
    public static boolean less(String time1, String time2) {
    	String[] time1StringArr = time1.split(":");
    	String[] time2StringArr = time2.split(":");
    	
    	List<Integer> time1Values = new ArrayList<Integer>();
    	List<Integer> time2Values = new ArrayList<Integer>();
    	
    	for(String value : time1StringArr) {
    		time1Values.add(Integer.parseInt(value));
    	}
    	
    	for(String value : time2StringArr) {
    		time2Values.add(Integer.parseInt(value));
    	}
    	
    	for(int i =0; i<4; i++) {
    		if(time1Values.get(i) < time2Values.get(i)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public static boolean more(String time1, String time2) {
    	String[] time1StringArr = time1.split(":");
    	String[] time2StringArr = time2.split(":");
    	
    	List<Integer> time1Values = new ArrayList<Integer>();
    	List<Integer> time2Values = new ArrayList<Integer>();
    	
    	for(String value : time1StringArr) {
    		time1Values.add(Integer.parseInt(value));
    	}
    	
    	for(String value : time2StringArr) {
    		time2Values.add(Integer.parseInt(value));
    	}
    	
    	for(int i =0; i<4; i++) {
    		if(time1Values.get(i) > time2Values.get(i)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public static String diff(String time1, String time2) {
    	String[] time1StringArr = time1.split(":");
    	String[] time2StringArr = time2.split(":");
    	
    	List<Integer> time1Values = new ArrayList<Integer>();
    	List<Integer> time2Values = new ArrayList<Integer>();
    	
    	for(String value : time1StringArr) {
    		time1Values.add(Integer.parseInt(value));
    	}
    	
    	for(String value : time2StringArr) {
    		time2Values.add(Integer.parseInt(value));
    	}
    	
    	
    	List<Integer> resultList = new ArrayList<Integer>();
    	int diff = 0;
    	for(int i = 3; i>-1; i--) {
    		int sum = time1Values.get(i)+ diff - time2Values.get(i);
    		diff = 0;
    		if(i == 3 && sum<0) { //Ms
    			sum = 100 + sum;
    			diff = -1;
    		}
    		else if((i == 2 || i == 1) && sum<0) { //Sec and Min
    			sum = 60 + sum;
    			diff = -1;
    		}
    		else if(i == 0 && sum < 0) { //hrs
    			sum = 24 + sum;
    		}
    		resultList.add(sum);
    	}
    	
    	Collections.reverse(resultList);
    	
    	String result = "";
    	for(int i=0; i<4; i++) {
    		String value = resultList.get(i) + "";
    		while(value.length() != 2) {
    			value = "0" + value;
    		}
    		result +=  value;
    		
    		if(i!=3) result += ":";
    	}
    	return result;
    }
    
    
	@Override
	public void printAST(String indent) {
		System.out.println(indent + "Time: " + token.getValue());
	}

}
