package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.Declaration;
import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Selector;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;

public class Generator {
	// GE01: Generate CSS from AST
	public String generate(AST ast) {
		return generateStylesheet(ast.root);
	}

	private String generateStylesheet(ASTNode node) {
		String output = "";

		for (ASTNode child : node.getChildren()) {
			if (child instanceof Stylerule) {
				output += generateStylerule(child);
			}
		}

		return output;
	}

	private String generateStylerule(ASTNode node) {
		Stylerule stylerule = (Stylerule) node;
		String output = "";

		// Add all selectors, seperated by a comma and an enter
		output += stylerule.selectors.stream().map(Selector::toString).reduce((a, b) -> a + ",\n" + b).orElse("");

		output += " {\n";

		for (ASTNode child : stylerule.body) {
			if (child instanceof Declaration) {
				output += generateDeclaration(child);
			}
		}

		output += "}\n\n";
		return output;
	}

	private String generateDeclaration(ASTNode node) {
		Declaration declaration = (Declaration) node;
		String output = "";

		// GE02: Add 2 spaces per scope level.
		output += "  " + declaration.property.name + ": ";

		if (declaration.expression instanceof ColorLiteral) {
			output += ((ColorLiteral) declaration.expression).value + ";\n";
		} else if (declaration.expression instanceof PixelLiteral) {
			output += ((PixelLiteral) declaration.expression).value + "px;\n";
		} else if (declaration.expression instanceof PercentageLiteral) {
			output += ((PercentageLiteral) declaration.expression).value + "%;\n";
		}

		return output;
	}
}
