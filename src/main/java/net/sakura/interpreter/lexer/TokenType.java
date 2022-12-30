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

    NULL, // Null value

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

    QUOTE, // "
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
    QUESTION, // ? (optional variables)

    TRUE, // TRUE
    FALSE, // FALSE

    FUNC, // func

    OPEN_PARENTHESES, // (
    CLOSE_PARENTHESES, // )
    OPEN_BRACE, // {
    CLOSE_BRACE, // }

    NUM_LITERAL, // floats and integers
    LITERAL // any other text
}
