package nl.han.ica.icss.parser;

import java.util.Stack;

import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {

	// Accumulator attributes:
	private AST ast;

	// Use this to keep track of the parent nodes when recursively traversing the
	// ast
	private IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
	}

	public AST getAST() {
		return ast;
	}

	@Override
	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		currentContainer.push(new Stylesheet());
	}

	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		Stylesheet stylesheet = (Stylesheet) currentContainer.pop();
		ast.setRoot(stylesheet);
	}

	@Override
	public void enterStylerule(ICSSParser.StyleruleContext ctx) {
		currentContainer.push(new Stylerule());
	}

	@Override
	public void exitStylerule(ICSSParser.StyleruleContext ctx) {
		Stylerule stylerule = (Stylerule) currentContainer.pop();
		Stylesheet stylesheet = (Stylesheet) currentContainer.peek();
		stylesheet.addChild(stylerule);
	}

	@Override
	public void enterClassSelector(ICSSParser.ClassSelectorContext ctx) {
		ClassSelector classSelector = new ClassSelector(ctx.getText());
		currentContainer.push(classSelector);
	}

	@Override
	public void exitClassSelector(ICSSParser.ClassSelectorContext ctx) {
		ClassSelector classSelector = (ClassSelector) currentContainer.pop();
		Stylerule stylerule = (Stylerule) currentContainer.peek();
		stylerule.addChild(classSelector);
	}

	@Override
	public void enterIdSelector(ICSSParser.IdSelectorContext ctx) {
		IdSelector idSelector = new IdSelector(ctx.getText());
		currentContainer.push(idSelector);
	}

	@Override
	public void exitIdSelector(ICSSParser.IdSelectorContext ctx) {
		IdSelector idSelector = (IdSelector) currentContainer.pop();
		Stylerule stylerule = (Stylerule) currentContainer.peek();
		stylerule.addChild(idSelector);
	}

	@Override
	public void enterTagSelector(ICSSParser.TagSelectorContext ctx) {
		TagSelector tagSelector = new TagSelector(ctx.getText());
		currentContainer.push(tagSelector);
	}

	@Override
	public void exitTagSelector(ICSSParser.TagSelectorContext ctx) {
		TagSelector tagSelector = (TagSelector) currentContainer.pop();
		Stylerule stylerule = (Stylerule) currentContainer.peek();
		stylerule.addChild(tagSelector);
	}

	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		currentContainer.push(new Declaration());
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration declaration = (Declaration) currentContainer.pop();
		Stylerule stylerule = (Stylerule) currentContainer.peek();
		stylerule.addChild(declaration);
	}

	@Override
	public void enterLiteral(ICSSParser.LiteralContext ctx) {
		String text = ctx.getText();
		if (text.startsWith("#")) {
			currentContainer.push(new ColorLiteral(text));
		} else if (text.endsWith("px")) {
			currentContainer.push(new PixelLiteral(text));
		} else if (text.endsWith("%")) {
			currentContainer.push(new PercentageLiteral(text));
		} else if (text.endsWith("em")) {
			currentContainer.push(new ScalarLiteral(text));
		} else if (text.equalsIgnoreCase("true") || text.equalsIgnoreCase("false")) {
			currentContainer.push(new BoolLiteral(text));
		} else {
			throw new RuntimeException("Unknown literal type: " + text);
		}
	}

	@Override
	public void exitLiteral(ICSSParser.LiteralContext ctx) {
		Literal literal = (Literal) currentContainer.pop();
		Declaration declaration = (Declaration) currentContainer.peek(); // variableAssignment cannot be cast to declaration
		declaration.addChild(literal);
	}

	@Override
	public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		currentContainer.push(new VariableAssignment());
	}

	@Override
	public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		VariableAssignment variableAssignment = (VariableAssignment) currentContainer.pop();
		Stylesheet stylesheet = (Stylesheet) currentContainer.peek();
		stylesheet.addChild(variableAssignment);
	}
}