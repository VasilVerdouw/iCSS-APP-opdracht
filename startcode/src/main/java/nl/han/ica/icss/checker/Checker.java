package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.Stylesheet;
import nl.han.ica.icss.ast.VariableAssignment;
import nl.han.ica.icss.ast.VariableReference;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;
import nl.han.ica.icss.ast.Declaration;
import nl.han.ica.icss.ast.ElseClause;
import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.IfClause;
import nl.han.ica.icss.ast.Operation;

import java.util.HashMap;

public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        // Root should always be stylesheet
        variableTypes = new HANLinkedList<>();
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

        // Make variable type available in current scope
        variableTypes.getFirst().put(variableAssignment.name.name, checkExpressionType(variableAssignment.expression));
    }

    private void checkStyleRule(ASTNode node) {
        Stylerule stylerule = (Stylerule) node;

        // Add new scope
        variableTypes.addFirst(new HashMap<>());

        // Check all children
        for (ASTNode child : stylerule.body) {
            if (child instanceof Declaration) {
                checkDeclaration(child);
            } else if (child instanceof IfClause) {
                checkIfClause(child);
            } else if (child instanceof VariableAssignment) {
                checkVariableAssignment(child);
            } else if (child instanceof Stylerule) {
                child.setError("Nesting of style rules is not supported");
            } else {
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
        ExpressionType expressionType = checkExpressionType(declaration.expression);
        if (expressionType != ExpressionType.UNDEFINED) {
            // Check if expression type is correct for property name

            // NOTE: An array of allowed expressions per property name could be better for
            // future extensions. I've chosen NOT to do this because currently only 4
            // properties are supported. And this would make the code more complex, more
            // difficult to read and probably have a small performance impact.

            // CH04: Make sure that the expression type matches the property name.
            switch (declaration.property.name) {
                case "background-color":
                    if (expressionType != ExpressionType.COLOR) {
                        node.setError("Only color expressions are allowed for background-color");
                    }
                    break;
                case "width":
                    if (expressionType != ExpressionType.PIXEL && expressionType != ExpressionType.PERCENTAGE) {
                        node.setError("Only pixel and percentage expressions are allowed for width");
                    }
                    break;
                case "color":
                    if (expressionType != ExpressionType.COLOR) {
                        node.setError("Only color expressions are allowed for color");
                    }
                    break;
                case "height":
                    if (expressionType != ExpressionType.PIXEL) {
                        node.setError("Only pixel expressions are allowed for height");
                    }
                    break;
                default:
                    node.setError("Unknown property name, only background-color, width, color and height are allowed");
            }
        }
    }

    private ExpressionType checkExpressionType(Expression expression) {
        if (expression instanceof VariableReference) {
            return checkVariableReferenceType(expression);
        } else if (expression instanceof Operation) {
            return checkOperationType(expression);
        } else if (expression instanceof ColorLiteral) {
            return ExpressionType.COLOR;
        } else if (expression instanceof PixelLiteral) {
            return ExpressionType.PIXEL;
        } else if (expression instanceof PercentageLiteral) {
            return ExpressionType.PERCENTAGE;
        } else if (expression instanceof ScalarLiteral) {
            return ExpressionType.SCALAR;
        } else if (expression instanceof BoolLiteral) {
            return ExpressionType.BOOL;
        }

        expression.setError("Unknown expression type");
        return ExpressionType.UNDEFINED;
    }

    private ExpressionType checkVariableReferenceType(ASTNode node) {
        VariableReference variableReference = (VariableReference) node;

        // Return type if found in scope
        for (HashMap<String, ExpressionType> scope : variableTypes) {
            if (scope.containsKey(variableReference.name)) {
                return scope.get(variableReference.name);
            }
        }

        // CH01: Variable is not defined.
        // CH06: Variable can't be used outside of scope.
        variableReference.setError("Variable" + variableReference.name + " is not defined in current scope.");
        return ExpressionType.UNDEFINED;
    }

    private void checkIfClause(ASTNode node) {
        IfClause ifClause = (IfClause) node;

        // Add new scope
        variableTypes.addFirst(new HashMap<>());

        // Check all children
        for (ASTNode child : ifClause.getChildren()) {
            // CH05: If clause can only have boolean variable references or boolean literals
            if (child instanceof VariableReference) {
                if (checkVariableReferenceType((Expression) child) != ExpressionType.BOOL) {
                    child.setError("If clause can only contain boolean expressions");
                }
            } else if (child instanceof BoolLiteral) {
                // Nothing to check
            } else if (child instanceof VariableAssignment) {
                checkVariableAssignment(child);
            } else if (child instanceof Declaration) {
                checkDeclaration(child);
            } else if (child instanceof IfClause) {
                checkIfClause(child);
            } else if (child instanceof ElseClause) {
                checkElseClause(child);
            } else {
                child.setError(
                        "If clause can only contain boolean expressions, variable assignments, declarations, if- and else clauses");
            }
        }

        // Remove scope
        variableTypes.removeFirst();
    }

    private void checkElseClause(ASTNode node) {
        ElseClause elseClause = (ElseClause) node;

        // Add new scope
        variableTypes.addFirst(new HashMap<>());

        // Check all children
        for (ASTNode child : elseClause.getChildren()) {
            if (child instanceof VariableAssignment) {
                checkVariableAssignment(child);
            } else if (child instanceof Declaration) {
                checkDeclaration(child);
            } else if (child instanceof IfClause) {
                checkIfClause(child);
            } else {
                child.setError(
                        "Else clause can only contain variable assignments, declarations and if clauses");
            }
        }

        // Remove scope
        variableTypes.removeFirst();
    }

    private ExpressionType checkOperationType(ASTNode node) {
        Operation operation = (Operation) node;

        // Check all children
        // CH03: Operations can't contain color literals.
        for (ASTNode child : operation.getChildren()) {
            if (child instanceof ColorLiteral) {
                child.setError("Color literals are not allowed in operations");
                return ExpressionType.UNDEFINED;
            } else if (child instanceof BoolLiteral) {
                child.setError("Boolean literals are not allowed in operations");
                return ExpressionType.UNDEFINED;
            }
        }

        if (operation instanceof AddOperation) {
            return checkAddOperationType(node);
        } else if (operation instanceof SubtractOperation) {
            return checkSubtractOperationType(node);
        } else if (operation instanceof MultiplyOperation) {
            return checkMultiplyOperationType(node);
        } else {
            operation.setError("Unknown operation type");
            return ExpressionType.UNDEFINED;
        }
    }

    private ExpressionType checkAddOperationType(ASTNode node) {
        AddOperation addOperation = (AddOperation) node;
        ExpressionType leftType = checkExpressionType(addOperation.lhs);
        ExpressionType rightType = checkExpressionType(addOperation.rhs);

        // CH02: Add operation can only be used with expressions of the same type.
        if (leftType == rightType) {
            return leftType;
        } else {
            addOperation.setError("Add operation can only be used with expressions of the same type");
            return ExpressionType.UNDEFINED;
        }
    }

    private ExpressionType checkSubtractOperationType(ASTNode node) {
        SubtractOperation subtractOperation = (SubtractOperation) node;
        ExpressionType leftType = checkExpressionType(subtractOperation.lhs);
        ExpressionType rightType = checkExpressionType(subtractOperation.rhs);

        // CH02: Subtract operation can only be used with expressions of the same type.
        if (leftType == rightType) {
            return leftType;
        } else {
            subtractOperation.setError("Subtract operation can only be used with expressions of the same type");
            return ExpressionType.UNDEFINED;
        }
    }

    private ExpressionType checkMultiplyOperationType(ASTNode node) {
        MultiplyOperation multiplyOperation = (MultiplyOperation) node;
        ExpressionType leftType = checkExpressionType(multiplyOperation.lhs);
        ExpressionType rightType = checkExpressionType(multiplyOperation.rhs);

        // CH02: Multiply operation can only be used with a scalar and a non-scalar
        // expression or scalar and scalar.
        if (leftType != ExpressionType.SCALAR && rightType != ExpressionType.SCALAR) {
            multiplyOperation.setError(
                    "Multiply operation can only be used with a scalar and a non-scalar expression or scalar and scalar");
            return ExpressionType.UNDEFINED;
        }

        if (leftType == ExpressionType.SCALAR) {
            return rightType;
        } else {
            return leftType;
        }
    }

}