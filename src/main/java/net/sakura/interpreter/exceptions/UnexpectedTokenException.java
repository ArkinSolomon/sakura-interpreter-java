/*
 * Copyright (c) 2023. Sakura Contributors.
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

package net.sakura.interpreter.exceptions;

import net.sakura.interpreter.lexer.FunctionCallData;
import net.sakura.interpreter.lexer.Token;
import net.sakura.interpreter.lexer.TokenType;

/**
 * Create a new exception saying that a token was not expected.
 */
public class UnexpectedTokenException extends SakuraException {

    /**
     * Create a new exception saying a token was unexpected using the unexpected token.
     *
     * @param token The token that was unexpected.
     */
    public UnexpectedTokenException(Token token) {
        this(token, null);
    }

    /**
     * Create a new exception saying a token was unexpected using the unexpected token, as well as providing additional supporting details.
     *
     * @param token The token that was unexpected.
     * @param msg   Additional details about why the token was unexpected.
     */
    public UnexpectedTokenException(Token token, String msg) {
        super(token.line(), token.column(), "Unexpected token: " + tokenChar(token) + "." + (msg == null ? "" : " " + msg));
    }

    /**
     * Get the token character for a token, or what should be displayed in the quotes after "Unexpected token".
     *
     * @return The token character.
     */
    private static String tokenChar(Token token) {
        String tokenStr = "\"";
        tokenStr += switch (token.type()) {
            case SEMI -> ";";
            case DOUBLE_EQUALS -> "==";
            case NOT_EQUALS -> "!=";
            case LT -> "<";
            case LTE -> "<=";
            case GT -> ">";
            case GTE -> ">=";
            case EQUALS -> "=";
            case AND -> "&";
            case OR -> "|";
            case NOT -> "!";
            case PLUS -> "+";
            case MINUS -> "-";
            case MULTIPLY -> "*";
            case QUOTE -> "\"";
            case SYMBOL, VARIABLE, CONST_VAR, ENV_VARIABLE ->
                    (String) token.value();
            case COMMA -> ",";
            case IF -> "if";
            case ELIF -> "else if";
            case ELSE -> "else";
            case WHILE -> "while";
            case FOR -> "for";
            case IN -> "in";
            case RETURN -> "return";
            case BREAK -> "break";
            case CONTINUE -> "continue";
            case BACKSLASH -> "\\";
            case SLASH -> "/";
            case FUNC -> "func";
            case FUNC_CALL -> ((FunctionCallData) token.value()).identifier();
            case OPEN_PARENTHESIS -> "(";
            case CLOSE_PARENTHESIS -> ")";
            case OPEN_BRACE -> "{";
            case CLOSE_BRACE -> "}";
            case NUM_LITERAL -> token.value().toString();
            default -> token.type().toString();
        };
        tokenStr += "\"";

        if (token.isOfType(TokenType.EOF))
            tokenStr = "<end of file>";
        else if (token.isOfType(TokenType.EOL))
            tokenStr = "<end of line>";

        return tokenStr;
    }
}
