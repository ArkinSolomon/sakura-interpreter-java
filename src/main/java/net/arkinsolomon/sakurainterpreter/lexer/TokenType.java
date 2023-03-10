/*
 * Copyright (c) 2022-2023 Arkin Solomon.
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

package net.arkinsolomon.sakurainterpreter.lexer;

public enum TokenType {
    EOF, // End of file
    EOL, // End of line
    SEMI, // A semi colon

    DOUBLE_EQUALS, // == (equality)
    NOT_EQUALS, // !=
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

    IF, // if
    IF_COND, // if ...
    ELIF, // else if
    ELIF_COND, // elif
    ELSE, // else
    IF_STATEMENT, // The fully parsed statement

    WHILE, // while
    WHILE_COND, // while ...
    WHILE_LOOP, // The whole parsed and tokenized loop

    FOR, // for
    IN, // in
    FOR_ASSIGN, // for ... in ...
    FOR_LOOP, // The whole parsed and tokenized loop

    RETURN, // return
    BREAK, // break
    CONTINUE, // continue

    BACKSLASH, // \ (escaping)
    SLASH, // / (path values or division)

    FUNC, // func
    FUNC_SIG, // The signature of the function
    FUNC_DEF, // Function definition, a FUNC token followed by a BRACE token
    FUNC_CALL, // Function call, a symbol followed by parentheses

    PATH_OPEN_PARENTHESIS, // $(
    OPEN_PARENTHESIS, // (
    CLOSE_PARENTHESIS, // )
    PARENTHETICAL_EXPR, // Full expression wrapped in parentheses

    OPEN_BRACE, // {
    CLOSE_BRACE, // }
    BRACE, // A series of expressions wrapped in braces.

    PERIOD, // .
    ELLIPSIS, // Three periods in a row

    PATH, // PATH (file command)
    READ, // READ (file command)
    ISDIR, // ISDIR (file command)
    ISFILE, // ISFILE (file command)
    DELETE, // DELETE (delete command)
    MKDIR, // MKDIR (non-recursively create a directory)
    MKDIRS, // MKDIRS (recursively create directories)
    EXISTS, // EXISTS (determine if a file or directory exists)

    TO, // TO (token between arguments in commands)
    TO_PATH, // A "TO" which is to be followed by a path
    TO_STR, // A "TO" which is to be followed by a string

    WRITE, // WRITE (overwrite a file with the new content)
    WRITE_CMD, // A full write command
    APPEND, // APPEND (add data to the end of a file)
    APPEND_CMD, // A full append command

    COPY, // COPY (copy a file or directory from one location to another)
    COPY_CMD, // A full copy command
    MOVE, // MOVE (move a file or directory from one location to another)
    MOVE_CMD, // A full move command

    RENAME, // RENAME (rename a file or directory)
    RENAME_CMD,

    PATH_LITERAL,
    NUM_LITERAL, // floats and integers
    SYMBOL // any other text
}
