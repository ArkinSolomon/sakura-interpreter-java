/*
 * Copyright (c) 2022. Sakura Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied limitations under the License.
 */

package net.sakura.interpreter.lexer;

public enum TokenType {
    EOF, // End of file
    EOL, // End of line
    SEMI, // A semi colon

    DOUBLE_EQUALS, // == (equality)
    LT, // <
    LTE, // <=
    GT, // >
    GTE, // >=

    EQUALS, // = (assignment)
    AND, // &
    OR, // |
    NOT, // !

    PLUS, // + (addition)
    MINUS, // - (subtraction)
    MULTIPLY, // * (multiplication)
    // Division is handled by slash

    QUOTE, // " (string literal)
    COMMA, // ,

    VARIABLE, // $var
    CONST_VAR, // %var
    ENV_VARIABLE, // @var

    IF, // if (if statement)
    ELIF, // else if (if statement)
    ELSE, // else (if statement)

    WHILE, // while (loop)
    FOR, // for (loop)

    IN, // in
    RETURN, // return

    BACKSLASH, // \ (escaping)
    SLASH, // / (path values or division)

    TRUE, // TRUE
    FALSE, // FALSE

    FUNC, // func

    OPEN_PARENTHESES, // (
    CLOSE_PARENTHESES, // )
    OPEN_BRACE, // {
    CLOSE_BRACE, // }

    NUM_LITERAL, // floats and integers
    SYMBOL // any other text
}
