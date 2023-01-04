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

import java.util.List;

/**
 * Allow tokens to be removed from storage one at a time.
 */
public class TokenStorage {

    private final List<Token> tokens;
    private int current;

    /**
     * Create a new token storage with a copy of some tokens.
     *
     * @param tokens The tokens to store.
     */
    public TokenStorage(List<Token> tokens) {
        this(tokens, -1);
    }

    /**
     * Create a new token storage with a copy of some tokens, at a certain index.
     *
     * @param tokens The tokens to store.
     * @param index  The index of the current token.
     */
    public TokenStorage(List<Token> tokens, int index) {
        this.tokens = tokens.stream().toList();
        current = index;
    }

    /**
     * Consume and move to the next token.
     *
     * @return The next token.
     */
    public Token consume() {
        ++current;
        return tokens.get(current);
    }

    /**
     * Get the next token without moving forward.
     *
     * @return The next token.
     */
    public Token peek() {
        if (current == tokens.size() - 1)
            return null;
        return tokens.get(current + 1);
    }

    /**
     * Check if there is another token after the current.
     *
     * @return True if there is another token.
     */
    public boolean hasNext() {
        return current >= 0 && current < tokens.size() - 1;
    }


    /**
     * Get the last token.
     */
    public Token lastToken() {
        if (current > 0) return tokens.get(current - 1);
        return null;
    }

    /**
     * Print all tokens.
     */
    public void printTokens() {
        for (Token token : tokens)
            System.out.println(token);
    }

    /**
     * Get the current token.
     *
     * @return The current token.
     */
    public Token currentToken() {
        if (current < 0 || current >= tokens.size())
            return null;
        return tokens.get(current);
    }

    /**
     * Move the current pointer past the current token to the next token that is not an end of line token.
     *
     * @return The next token that is not an end of line token.
     */
    public Token nextNonEOLToken() {
        if (!hasNext())
            return null;
        Token token = consume();
        while (token != null && token.isOfType(TokenType.EOL))
            token = consume();
        return token;
    }

    /**
     * Get the next token that is not an end of line, without moving the pointer.
     *
     * @return The next token that is not an end of line token.
     */
    public Token peekNextNonEOLToken() {
        for (int i = current + 1; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            if (!token.isOfType(TokenType.EOL))
                return token;
        }
        return null;
    }

    /**
     * Get the last token that is not an end of line token, without moving the current pointer.
     *
     * @return The last token that is not an end of line token.
     */
    public Token lastNonEOLToken() {
        int startIndex = current - 1;
        if (startIndex < 0)
            return null;
        else if (current > tokens.size())
            startIndex = tokens.size() - 1;

        for (int i = startIndex; i >= 0; i--) {
            Token token = tokens.get(i);
            if (!token.isOfType(TokenType.EOL))
                return token;
        }
        return null;
    }
}
