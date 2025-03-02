package ua.kp13.mishchenko.visitor;

public interface Visitor<T> {

	T visitProgram();
	T visitAssignment();
	T visitBinaryOperation();
	
}
