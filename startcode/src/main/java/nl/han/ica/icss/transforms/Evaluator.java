package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javafx.css.Style;

public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new HANLinkedList<>();
        evaluateStylesheet(ast.root); // Again, root should always be a stylesheet.
    }

    private void evaluateStylesheet(ASTNode node) {
        // Add new scope
        variableValues.addFirst(new HashMap<>());

        ArrayList<ASTNode> nodesToRemove = new ArrayList<>();
        for (ASTNode child : node.getChildren()) {
            if (child instanceof Stylerule) {
                evaluateStylerule(child);
            } else if (child instanceof VariableAssignment) {
                evaluateVariableAssignment(child);
                nodesToRemove.add(child);
            }
        }

        // Remove variable assignments because they aren't needed anymore
        for (ASTNode child : nodesToRemove) {
            node.removeChild(child);
        }

        // Remove scope
        variableValues.removeFirst();
    }

    private void evaluateStylerule(ASTNode node) {
        // Add new scope
        variableValues.addFirst(new HashMap<>());

        Stylerule stylerule = (Stylerule) node;
        evaluateStyleruleBody(stylerule.body);

        // Remove scope
        variableValues.removeFirst();
    }

    private void evaluateStyleruleBody(ArrayList<ASTNode> nodes) {
        ArrayList<ASTNode> nodesToRemove = new ArrayList<>();
        ArrayList<ASTNode> nodesToAdd = new ArrayList<>();
        for (ASTNode child : nodes) {
            if (child instanceof Declaration) {
                evaluateDeclaration(child);
            } else if (child instanceof IfClause) {
                nodesToAdd.addAll(evaluateIfClause(child));
                nodesToRemove.add(child);
            } else if (child instanceof VariableAssignment) {
                evaluateVariableAssignment(child);
                nodesToRemove.add(child);
            }
        }

        // Remove if clauses and variable assignments because they aren't needed anymore
        // This is done AFTER the evaluation because concurrent modification of the list
        // is not allowed and will throw an exception. This is possible because nodes is
        // a reference.
        for (ASTNode node : nodesToRemove) {
            nodes.remove(node);
        }

        // Add if clause bodies to stylerule body
        for (ASTNode node : nodesToAdd) {
            nodes.add(node);
        }
    }

    private void evaluateDeclaration(ASTNode node) {
        Declaration declaration = (Declaration) node;
        declaration.expression = evaluateExpression(declaration.expression);
    }

    private ArrayList<ASTNode> evaluateIfClause(ASTNode node) {
        IfClause ifClause = (IfClause) node;

        // Evaluate condition
        boolean ifClauseIsTrue = ((BoolLiteral) evaluateExpression(ifClause.conditionalExpression)).value;

        if (ifClauseIsTrue) {
            // Evaluate body of if clause
            evaluateStyleruleBody(ifClause.body);
            return ifClause.body;
        } else {
            if (ifClause.elseClause == null)
                return new ArrayList<>();
            // Evaluate body of else clause
            evaluateStyleruleBody(ifClause.elseClause.body);
            return ifClause.elseClause.body;
        }

    }

    private void evaluateVariableAssignment(ASTNode node) {
        VariableAssignment variableAssignment = (VariableAssignment) node;
        // Make value of variable available in current scope
        variableValues.getFirst().put(variableAssignment.name.name, evaluateExpression(variableAssignment.expression));
    }

    private Literal evaluateExpression(ASTNode node) {
        if (node instanceof VariableReference) {
            return evaluateVariableReference(node);
        } else if (node instanceof Operation) {
            return evaluateOperation(node);
        } else if (node instanceof Literal) {
            return (Literal) node;
        } else {
            return null;
        }
    }

    private Literal evaluateVariableReference(ASTNode node) {
        VariableReference variableReference = (VariableReference) node;

        // Find variable value
        for (HashMap<String, Literal> scope : variableValues) {
            if (scope.containsKey(variableReference.name)) {
                return scope.get(variableReference.name);
            }
        }

        return null;
    }

    private Literal evaluateOperation(ASTNode node) {
        Operation operation = (Operation) node;
        Literal left = evaluateExpression(operation.lhs);
        Literal right = evaluateExpression(operation.rhs);

        if (node instanceof AddOperation) {
            return evaluateAddOperation(left, right);
        } else if (node instanceof SubtractOperation) {
            return evaluateSubtractOperation(left, right);
        } else if (node instanceof MultiplyOperation) {
            return evaluateMultiplyOperation(left, right);
        } else {
            return null;
        }
    }

    private Literal evaluateAddOperation(Literal left, Literal right) {
        if (left instanceof PixelLiteral && right instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) left).value + ((PixelLiteral) right).value);
        } else if (left instanceof PercentageLiteral && right instanceof PercentageLiteral) {
            return new PercentageLiteral(((PercentageLiteral) left).value + ((PercentageLiteral) right).value);
        } else if (left instanceof ScalarLiteral && right instanceof ScalarLiteral) {
            return new ScalarLiteral(((ScalarLiteral) left).value + ((ScalarLiteral) right).value);
        } else {
            return null;
        }
    }

    private Literal evaluateSubtractOperation(Literal left, Literal right) {
        if (left instanceof PixelLiteral && right instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) left).value - ((PixelLiteral) right).value);
        } else if (left instanceof PercentageLiteral && right instanceof PercentageLiteral) {
            return new PercentageLiteral(((PercentageLiteral) left).value - ((PercentageLiteral) right).value);
        } else if (left instanceof ScalarLiteral && right instanceof ScalarLiteral) {
            return new ScalarLiteral(((ScalarLiteral) left).value - ((ScalarLiteral) right).value);
        } else {
            return null;
        }
    }

    private Literal evaluateMultiplyOperation(Literal left, Literal right) {
        if (left instanceof PixelLiteral && right instanceof ScalarLiteral) {
            return new PixelLiteral(((PixelLiteral) left).value * ((ScalarLiteral) right).value);
        } else if (left instanceof PercentageLiteral && right instanceof ScalarLiteral) {
            return new PercentageLiteral(((PercentageLiteral) left).value * ((ScalarLiteral) right).value);
        } else if (left instanceof ScalarLiteral && right instanceof ScalarLiteral) {
            return new ScalarLiteral(((ScalarLiteral) left).value * ((ScalarLiteral) right).value);
        } else {
            return null;
        }
    }
}
