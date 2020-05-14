# magnetic-moose

In my quest to understand how interpeters for high-level programming languages work, I decided to make my own functional programming language called Torrey.

Instead of building it all in one go (which would be too overwhelming), I decided to break it up into iterations.  Each iteration will be an independent interpreter that is more complex than the last.

Magnetic Moose is the first iteration of the Torrey interpreter.  The interpreter only interprets fully-parenthesized arithmetic expressions such as:

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

(* (+ 2 3) 2)

/*
    This is a multi-line
    C-style comment block :)
*/ (/ 47 7)

/*
    ...and another one
*/
(* (- (* 10.5 5) 10) 2)
```

The grammar is:

```
program      -> expression ;
expression   -> "(" operator operand (" " operand)+ ")" ;
operator     -> "+" | "-" | "*" | "/" ;
operand      -> number | expression ;
number       -> [0-9]+ "." [0-9]+ | [0-9]+;
```