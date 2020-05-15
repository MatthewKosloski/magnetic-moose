package me.mtk.magneticmoose;

public class AstPrinter implements Expr.Visitor<String>
{

    public String print(Expr expr)
    {
        return expr.accept(this);
    }

    @Override 
    public String visitLiteralExpr(Expr.Literal expr)
    {
        return expr.value + "";
    }

    @Override
    public String visitArithmeticExpr(Expr.Arithmetic expr)
    {
        return parenthesize(expr.operator.lexeme, expr.first, expr.second);
    }

    private String parenthesize(String name, Expr... exprs)
    {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs)
        {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }
    
    public static void main(String[] args)
    {
        Token operator = new Token(TokenType.PLUS, "+", null, 1, 1);
        Expr operand1 = new Expr.Literal(2);
        Expr operand2 = new Expr.Literal(5);
        Expr expression = new Expr.Arithmetic(operator, operand1, operand2); 
        
        // (+ 2 5)
        System.out.println(new AstPrinter().print(expression));
    }

}