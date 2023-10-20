package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.Stylesheet;
import nl.han.ica.icss.ast.VariableAssignment;
import nl.han.ica.icss.ast.types.ExpressionType;
import nl.han.ica.icss.ast.Declaration;
import nl.han.ica.icss.ast.IfClause;

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
            } else {
                child.setError("Style rule can only contain declarations and if clauses");
            }
        }
    }

}