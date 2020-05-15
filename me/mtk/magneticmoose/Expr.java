package me.mtk.magneticmoose;

abstract class Expr 
{
	interface Visitor<T>
	{
		T visitLiteralExpr(Literal expr);
		T visitArithmeticExpr(Arithmetic expr);
	}

	abstract <T> T accept(Visitor<T> visitor);

	static class Literal extends Expr
	{
		final double value;

		public Literal(double value)
		{
			this.value = value;
		}

		@Override
		public <T> T accept(Visitor<T> visitor)
		{
			return visitor.visitLiteralExpr(this);
		}
	}

	static class Arithmetic extends Expr
	{
		final Token operator;
		final Expr first;
		final Expr second;

		public Arithmetic(Token operator, Expr first, Expr second)
		{
			this.operator = operator;
			this.first = first;
			this.second = second;
		}

		@Override
		public <T> T accept(Visitor<T> visitor)
		{
			return visitor.visitArithmeticExpr(this);
		}
	}
}