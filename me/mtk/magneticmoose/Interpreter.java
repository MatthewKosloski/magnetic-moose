package me.mtk.magneticmoose;

// The Interpreter is the part of the interpreter that, well,
// interprets the source code. It takes in an abstract syntax
// tree (AST) as input and outputs a string representing
// the output of the program.
public class Interpreter implements Expr.Visitor<Object>
{

    /**
     * Interprets the source program by walking, or traversing,
     * the given AST in post-order. 
     * 
     * @param expression An AST representing the source program.
     * @return A string represnting the output of the program.
     */
    public String interpret(Expr expression)
    {
        try
        {
            Object value = evaluate(expression);
            return stringify(value);
        }
        catch (RuntimeError err)
        {
            MagneticMoose.runtimeError(err);
        }
        return null;
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr)
    {
        return expr.value;
    }

    @Override
    public Object visitArithmeticExpr(Expr.Arithmetic expr)
    {
        Token operator = expr.operator;
        Object first = evaluate(expr.first);
        Object second = evaluate(expr.second);

        validateNumberOperands(operator, first, second);

        switch (operator.type)
        {
            case PLUS:
                return (double) first + (double) second;
            case MINUS:
                return (double) first - (double) second;
            case STAR:
                return (double) first * (double) second;
            case SLASH:
                return (double) first / (double) second;
        }

        return null;
    }

    /*
     * Calls the appropriate visitor method that corresponds
     * to the expression, thereby evaluating the expression.
     * 
     * @param expr An expression.
     * @return The value of the expression.
     */
    private Object evaluate(Expr expr)
    {
        return expr.accept(this);
    }

    /*
     * Checks the type of the operands of the binary expression,
     * ensuring that they are numbers.
     * 
     * @param operator The operator of the binary expression.
     * @param first The first operand of the binary expression.
     * @param second The second operand of the binary expression.
     */
    private void validateNumberOperands(Token operator, 
        Object first, Object second)
    {
        if (first instanceof Double && second instanceof Double) return;
        throw new RuntimeError(operator, "Binary operators must be numbers.");
    }

    /*
     * Converts a value of an expression to a string.
     * @param obj An object representing the evaluated value.
     * @return A string of the value.
     */
    private String stringify(Object obj)
    {
        if (obj instanceof Double)
        {
            String text = obj.toString();
            if (text.endsWith(".0"))
            {
                // Integer, so remove the trailing 0
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return obj.toString();
    }
}