package com.github.yash777.commons.file;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.MethodCallExpr;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * A utility class for analyzing Java source files to identify potential refactoring opportunities.
 * This analyzer evaluates code quality by examining class size, method length, unused private methods,
 * and constructor implementations. It provides actionable suggestions to improve code maintainability
 * and reduce technical debt.
 * 
 * <h2>Analysis Capabilities:</h2>
 * <ul>
 *     <li>Class size analysis (total lines of code)</li>
 *     <li>Method length analysis (identifies methods exceeding recommended length)</li>
 *     <li>Unused private method detection</li>
 *     <li>Constructor implementation analysis</li>
 *     <li>Inner class and enum analysis</li>
 * </ul>
 * 
 * <h2>Example Usage:</h2>
 * <pre>
 *     JavaParserRefactorAnalyzer.analyzeJavaClass("path/to/YourClass.java");
 *     // or run with default configuration
 *     JavaParserRefactorAnalyzer.main(new String[0]);
 * </pre>
 * 
 * <h2>Configuration:</h2>
 * <p>The analyzer uses the following default thresholds:</p>
 * <ul>
 *     <li>Maximum method length: 50 lines</li>
 *     <li>Maximum class length: 300 lines</li>
 * </ul>
 * 
 * @author 🔐 Yash
 * @since 1.0
 */
public class JavaParserRefactorAnalyzer {
	
	/**
	 * Default maximum number of lines allowed in a method before it's considered too long.
	 */
	public static final int MAX_METHOD_LINES = 50;
	
	/**
	 * Default maximum number of lines allowed in a class before it's considered too large.
	 */
	public static final int MAX_CLASS_LINES = 300;
	
	/**
	 * Main entry point for running the analyzer with default configuration.
	 * 
	 * @param args Command line arguments (not used)
	 * @throws IOException If there's an error reading the default file
	 */
	public static void main(String[] args) throws IOException {
		
		String workspacePath = PathUtility.getParentProjectPath().toString();
		System.out.println("Current Project - Parent Path: " + workspacePath);

		String projectSrcPath = "\\myworld-common\\src\\main\\java\\";
		String packagePath = "com\\github\\yash777\\security\\crypto\\";
		String classFileName = "AesCryptoManager.java";
		
		String fullFilePath = workspacePath + projectSrcPath + packagePath + classFileName;
		// String fullFilePath = "D:/Yash/FindClass.java"; // your input file
		analyzeJavaClass(fullFilePath);
	}
	
	/**
	 * Analyzes a Java source file and prints refactoring suggestions to standard output.
	 * 
	 * @param filePath Path to the Java source file to analyze
	 * @throws IOException If there's an error reading the file
	 */
	public static void analyzeJavaClass(String filePath) throws IOException {
		CompilationUnit cu = StaticJavaParser.parse(new File(filePath));
		List<MethodCallExpr> methodCalls = cu.findAll(MethodCallExpr.class);
		List<Comment> allComments = cu.getAllContainedComments();
		
		Set<String> calledMethods = new HashSet<>();
		methodCalls.forEach(call -> calledMethods.add(call.getNameAsString()));
		
		int totalLines = cu.toString().split("\r?\n").length;
		int commentLines = (int) allComments.stream()
				.map(Comment::getContent)
				.flatMap(content -> content.lines())
				.count();
		int codeOnlyLines = totalLines - commentLines;
		
		System.out.println("=== Refactoring Suggestions ===");
		System.out.println("Total lines in class: " + totalLines);
		System.out.println("Lines without comments: " + codeOnlyLines);
		if (codeOnlyLines > MAX_CLASS_LINES) {
			System.err.println("⚠️ Class is too large (" + codeOnlyLines + " lines). Consider splitting it.");
		}
		
		System.out.println("\n=== TypeDeclaration Analysis ===");
		List<TypeDeclaration<?>> types = cu.getTypes();
		for (TypeDeclaration<?> type : types) {
			analyzeType(type, methodCalls);
		}
	}
	
	/**
	 * Analyzes a type declaration (class, interface, enum) and its members for refactoring opportunities.
	 * 
	 * @param type The type declaration to analyze
	 * @param methodCalls List of all method calls found in the compilation unit
	 */
	private static void analyzeType(TypeDeclaration<?> type, List<MethodCallExpr> methodCalls) {
		boolean includingModifiers = true, includingThrows = true, includingParameterName = true;
		
		Set<String> calledMethods = new HashSet<>();
		methodCalls.forEach(call -> calledMethods.add(call.getNameAsString()));
		
		// Analyze constructors
		System.out.println("\n=== Constructor Analysis ===");
		for (ConstructorDeclaration constructor : type.getConstructors()) {
			int len = constructor.getEnd().get().line - constructor.getBegin().get().line + 1;
			String sig = constructor.getDeclarationAsString(includingModifiers, includingThrows, includingParameterName);
			System.out.printf("✅ Constructor '%s' is %d %s. No action needed.%n", sig, len, lineWord(len));
		}
		
		// Analyze methods
		for (MethodDeclaration method : type.getMethods()) {
			int len = method.getEnd().get().line - method.getBegin().get().line + 1;
			String sig = method.getDeclarationAsString(includingModifiers, includingThrows, includingParameterName);
			if (len > MAX_METHOD_LINES) {
				System.err.printf("🔧 Method '%s' is too long (%d %s). Consider refactoring.%n", sig, len, lineWord(len));
			} else {
				System.out.printf("✅ Method '%s' is %d %s. No action needed.%n", sig, len, lineWord(len));
			}
			
			boolean isPrivate = method.isPrivate();
			boolean isUsed = calledMethods.contains(method.getNameAsString());
			
			if (isPrivate && !isUsed) {
				System.err.println("🗑️ Private method '" + sig + "' appears unused. Consider removing.");
			}
		}
		
		// Analyze Inner Members: Enums and Inner Classes
		if (!type.getMembers().isEmpty()) {
			for (BodyDeclaration<?> member : type.getMembers()) {
				
				// Analyze inner enums
				if (member instanceof EnumDeclaration) {
					EnumDeclaration enumDecl = (EnumDeclaration) member;
					System.out.println("\n=== Enum Declarations ===");
					System.out.println("🟦 Enum: " + enumDecl.getName());
					for (ConstructorDeclaration constructor : enumDecl.getConstructors()) {
						int len = constructor.getEnd().get().line - constructor.getBegin().get().line + 1;
						String sig = constructor.getDeclarationAsString(includingModifiers, includingThrows, includingParameterName);
						System.out.printf("  ✅ Enum Constructor '%s' is %d %s. No action needed.%n", sig, len, lineWord(len));
					}
				}
				
				// Analyze inner classes
				if (member instanceof ClassOrInterfaceDeclaration) {
					ClassOrInterfaceDeclaration innerClass = (ClassOrInterfaceDeclaration) member;
					System.out.println("\n=== Inner Class Declarations ===");
					System.out.println("📦 Inner Class: " + innerClass.getName());
					
					// Constructors
					for (ConstructorDeclaration constructor : innerClass.getConstructors()) {
						int len = constructor.getEnd().get().line - constructor.getBegin().get().line + 1;
						String sig = constructor.getDeclarationAsString(includingModifiers, includingThrows, includingParameterName);
						System.out.printf("  ✅ Constructor '%s' is %d %s. No action needed.%n", sig, len, lineWord(len));
					}
					
					// Methods
					for (MethodDeclaration method : innerClass.getMethods()) {
						int len = method.getEnd().get().line - method.getBegin().get().line + 1;
						String sig = method.getDeclarationAsString(includingModifiers, includingThrows, includingParameterName);
						if (len > MAX_METHOD_LINES) {
							System.err.printf("  ❗ Method '%s' is too long (%d %s). Consider refactoring.%n", sig, len, lineWord(len));
						} else {
							System.out.printf("  ✅ Method '%s' is %d %s. No action needed.%n", sig, len, lineWord(len));
						}
					}
				}
			}
		}
	}
	
	/**
	 * Helper method to return the appropriate word ("line" or "lines") based on the number of lines.
	 * 
	 * @param lines Number of lines
	 * @return "line" if lines == 1, "lines" otherwise
	 */
	private static String lineWord(int lines) {
		return lines == 1 ? "line" : "lines";
	}
}