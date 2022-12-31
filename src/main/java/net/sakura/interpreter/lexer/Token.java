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

/**
 * A single token created by the lexer.
 *
 * @param type The type of the token.
 * @param tokenPos The position of the token.
 * @param value The text value of the token.
 */
public record Token(TokenType type, int tokenPos, String value) {

    /**
     * Get the string representation of this token.
     *
     * @return The string representation of this token.
     */
    @Override
    public String toString() {
        return "[%d] %s: %s".formatted(tokenPos, type, value.replaceAll("\n", "\\\\n"));
    }

    public double[] getPrecedence() {
        return switch (type){
            case EQUALS -> new double[]{6, 6};
            case PLUS, MINUS -> new double[]{8, 8.5};
            case MULTIPLY, SLASH -> new double[]{12, 12.5};
            default -> new double[]{1, 1};
        };
    }

    public boolean isOperator() {
        double[] precedence = getPrecedence();
        return precedence[0] + precedence[1] > 2;
    }
}
