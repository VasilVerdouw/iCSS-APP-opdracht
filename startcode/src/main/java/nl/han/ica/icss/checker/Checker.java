package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.Stylesheet;
import nl.han.ica.icss.ast.VariableAssignment;
import nl.han.ica.icss.ast.VariableReference;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.TagSelector;
import nl.han.ica.icss.ast.types.ExpressionType;
import nl.han.ica.icss.ast.Declaration;
import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.IfClause;
import nl.han.ica.icss.ast.Selector;
import nl.han.ica.icss.ast.Literal;
import nl.han.ica.icss.ast.Operation;

import java.util.HashMap;

public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public Checker() {
        variableTypes = new HANLinkedList<>();
    }

    public void check(AST ast) {
        // Root should always be stylesheet
        checkStylesheet(ast.root);
    }

    private void checkStylesheet(ASTNode node) {
        Stylesheet stylesheet = (Stylesheet) node;

        // Add new scope
        variableTypes.addFirst(new HashMap<>());

        // Check all children
        for (ASTNode child : stylesheet.getChildren()) {
            if (child instanceof VariableAssignment) {
                checkVariableAssignment(child);
            } else if (child instanceof Stylerule) {
                checkStyleRule(child);
            } else {
                child.setError("Stylesheet can only contain variable assignments and style rules on root level");
            }
        }

        // Remove scope
        variableTypes.removeFirst();
    }

    private void checkVariableAssignment(ASTNode node) {
        VariableAssignment variableAssignment = (VariableAssignment) node;

        // Nothing to check??????
    }

    private void checkStyleRule(ASTNode node) {
        Stylerule stylerule = (Stylerule) node;

        // Add new scope
        variableTypes.addFirst(new HashMap<>());

        // Check all children
        for (ASTNode child : stylerule.getChildren()) {
            if (child instanceof Declaration) {
                checkDeclaration(child);
            } else if (child instanceof IfClause) {
                checkIfClause(child);
            } else if (child instanceof VariableAssignment) {
                checkVariableAssignment(child);
            } else if (child instanceof Stylerule) {
                child.setError("Nesting of style rules is not supported");
            } else if (!(child instanceof Selector)) {
                child.setError("Style rule can only contain declarations, if clauses and variable assignments");
            }
        }

        // Remove scope
        variableTypes.removeFirst();
    }

    private void checkDeclaration(ASTNode node) {
        Declaration declaration = (Declaration) node;

        // Declaration always has propertyName and expression.
        // propertyName SHOULD always be correct in this phase.
        // expression should be checked.
        checkExpression(declaration.expression);
    }

    private void checkExpression(ASTNode node) {
        Expression expression = (Expression) node;

        // Check all children
        for (ASTNode child : expression.getChildren()) {
            if (child instanceof Operation) {
                checkOperation(child);
            } else if (child instanceof Literal) {

            } else {
                child.setError(
                        "How did we get here? Expression can only contain operations and literals");
            }
        }

    }

    private void checkIfClause(ASTNode node) {
        IfClause ifClause = (IfClause) node;

        // Add new scope
        variableTypes.addFirst(new HashMap<>());
    }

    private void checkOperation(ASTNode node) {
        Operation operation = (Operation) node;

        if (operation instanceof AddOperation) {
            checkAddOperation(node);
        } else if (operation instanceof SubtractOperation) {
            checkSubtractOperation(node);
        } else if (operation instanceof MultiplyOperation) {
            checkMultiplyOperation(node);
        } else {
            operation.setError("Unknown operation type");
        }
    }

    private void checkAddOperation(ASTNode node) {
        AddOperation addOperation = (AddOperation) node;

    }

}