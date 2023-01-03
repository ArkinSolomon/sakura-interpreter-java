/*
 * Copyright (c) 2022-2023. Sakura Contributors.
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

/**
 * A single token created by the lexer.
 *
 * @param type     The type of the token.
 * @param line The line of the token.
 * @param column The column of the text.
 * @param value    The text value of the token.
 */
public record Token(TokenType type, int line, int column, Object value) {

    /**
     * Get the string representation of this token.
     *
     * @return The string representation of this token.
     */
    @Override
    public String toString() {
        return "[%d:%d] %s: %s".formatted(line, column, type, value.toString().replaceAll("\n", "\\\\n"));
    }

    /**
     * Determine if this token is an operator.
     *
     * @return True if this token is an operator.
     */
    public boolean isOperator() {
        return switch (type) {
            case PLUS, MINUS, MULTIPLY, SLASH, EQUALS, DOUBLE_EQUALS, GT, LT, GTE, LTE, NOT, AND, OR ->
                    true;
            default -> false;
        };
    }
}
