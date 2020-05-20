# magnetic-moose

In my quest to understand how interpeters for high-level programming languages work, I decided to make my own functional programming language called Torrey.

Instead of building it all in one go (which would be too overwhelming), I decided to break it up into iterations.  Each iteration will be an independent interpreter that is more complex than the last.

Magnetic Moose is the first iteration of the Torrey interpreter.  The interpreter only interprets fully-parenthesized arithmetic expressions with a variable number of operands.

## Grammar

`Parser.java` implements the following grammar that describes the language's syntax:

```
program   -> binary ;
binary    -> "(" ("+" | "-" | "*" | "/") unary (" " unary)+ ")" ;
unary     -> ("+" | "-")? (binary | number) ;
number    -> [0-9]+ "." [0-9]+ | [0-9]+ ;
```

## Code Example

As demonstrated below, the language supports inline-comments using `//` or C-style block comments using `/* */`.

```
// This is an inline-comment

(+ 4 4)
(+ 1 2 3)

// These
// are
// multiple
// inline comments!
(+ 5 (* 2 2.25) 6)

/* This is an inline C-style comment block */

(* -(+ +2 -3) -2 +(* 9 -3))

/*
    This is a multi-line
    C-style comment block :)
*/ (/ 47 7)

/*
    ...and another one
*/
(* (- (* 10.5 5) 10) 2)
```

## Error Reporting

The goal of error handling is to inform the programmer of any syntax, parsing, or runtime errors.  Error messages provide what was expected and what was actually present. They also point to where the error occurred in the source program.

Example error messages include:

```
program.in:1:9: ParseError: Expected ")" after expression but got "&" instead
        (+ 2 344&)
                ^
```

```
program.in:1:1: ParseError: Expected an expression starting with "(" but got "%" instead
        %+ 2 3)
        ^
```

```
program.in:1:7: ParseError: Expected either a number or "(" to come after the unary operator but got "-" instead
        (+ 2 --(* 1 3))
              ^
```

```
program.in:1:2: RuntimeError: Cannot divide by 0.
        (/ 999 (+ 5 -5))
         ^
```