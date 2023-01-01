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

import java.util.ArrayList;
import java.util.List;

/**
 * An instance of this class performs lexical analysis on a given string input
 * and creates tokens from it.
 */
public class Lexer {

    private final String input;

    private final List<Token> tokens = new ArrayList<>();
    private int current = -1;

    /**
     * Create a new instance which will perform lexical analysis on a string.
     *
     * @param input The input on which to perform the analysis.
     */
    public Lexer(String input) {
        this.input = input;
    }

    private static boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if the character can be used in an identifier.
     *
     * @param c The character to check.
     * @return True if the character can be used in an identifier.
     */
    private static boolean isIdentifierChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    /**
     * Analyze the lexer text.
     */
    public List<Token> analyze() {
        final PeekableScanner scanner = new PeekableScanner(input);
        int currentPos = 0;
        int startPos = -1;

        TokenType currentType = null;
        StringBuilder currentValue = new StringBuilder();

        while (scanner.hasNext()) {
            String thisCharStr = scanner.next();
            if (thisCharStr == null)
                break;

            if ((currentValue.length() == 0) && thisCharStr.isEmpty())
                continue;

            char thisChar = thisCharStr.charAt(0);

            String next = scanner.peek();
            Character nextChar = next == null ? null : next.charAt(0);

            if (currentType == null) {

                if (thisChar == '\n' || thisChar == ';') {
                    TokenType type = thisChar == '\n' ? TokenType.EOL : TokenType.SEMI;
                    tokens.add(new Token(type, currentPos, thisCharStr));
                    currentPos++;
                    continue;
                } else if (thisCharStr.isBlank()) {
                    currentPos++;
                    continue;
                }

                startPos = currentPos;
                if (thisChar == '$')
                    currentType = TokenType.VARIABLE;
                else if (thisChar == '%')
                    currentType = TokenType.CONST_VAR;
                else if (thisChar == '@')
                    currentType = TokenType.ENV_VARIABLE;
                else if (thisChar == '=') {
                    if (nextChar == null)
                        throw new RuntimeException("Dangling equals");

                    if (nextChar == '=') {
                        tokens.add(new Token(TokenType.DOUBLE_EQUALS, currentPos, "=="));

                        // We need to increment by two, so we leave this here (similar for LTE and GTE)
                        scanner.next();
                        currentPos++;
                    } else
                        tokens.add(new Token(TokenType.EQUALS, currentPos, "="));
                } else if (thisChar == '<') {
                    if (nextChar == null)
                        throw new RuntimeException("Dangling less than");

                    if (nextChar == '=') {
                        tokens.add(new Token(TokenType.LTE, currentPos, "<="));

                        scanner.next();
                        currentPos++;
                    } else
                        tokens.add(new Token(TokenType.LT, currentPos, "<"));
                } else if (thisChar == '>') {
                    if (nextChar == null)
                        throw new RuntimeException("Dangling greater than");

                    if (nextChar == '=') {
                        tokens.add(new Token(TokenType.GTE, currentPos, ">="));

                        scanner.next();
                        currentPos++;
                    } else
                        tokens.add(new Token(TokenType.GT, currentPos, ">"));
                } else if (thisChar == '"')
                    currentType = TokenType.QUOTE;
                else if (thisChar == '/')
                    tokens.add(new Token(TokenType.SLASH, currentPos, "/"));
                else if (thisChar == '\\')
                    tokens.add(new Token(TokenType.BACKSLASH, currentPos, "\\"));
                else if (thisChar == '&')
                    tokens.add(new Token(TokenType.AND, currentPos, "&"));
                else if (thisChar == '|')
                    tokens.add(new Token(TokenType.OR, currentPos, "|"));
                else if (thisChar == '!')
                    tokens.add(new Token(TokenType.NOT, currentPos, "!"));
                else if (thisChar == '+')
                    tokens.add(new Token(TokenType.PLUS, currentPos, "+"));
                else if (thisChar == '-')
                    tokens.add(new Token(TokenType.MINUS, currentPos, "-"));
                else if (thisChar == '*')
                    tokens.add(new Token(TokenType.MULTIPLY, currentPos, "*"));
                else if (thisChar == '(')
                    tokens.add(new Token(TokenType.OPEN_PARENTHESES, currentPos, "("));
                else if (thisChar == ')')
                    tokens.add(new Token(TokenType.CLOSE_PARENTHESES, currentPos, ")"));
                else if (thisChar == '{')
                    tokens.add(new Token(TokenType.OPEN_BRACE, currentPos, "{"));
                else if (thisChar == '}')
                    tokens.add(new Token(TokenType.CLOSE_BRACE, currentPos, "}"));
                else if (thisChar == ',')
                    tokens.add(new Token(TokenType.COMMA, currentPos, ","));
                else {
                    currentType = TokenType.SYMBOL;
                    currentValue.append(thisChar);

                    // Current value must be one character long
                    if (next == null || !isIdentifierChar(nextChar)) {
                        if (isNumeric(thisCharStr))
                            currentType = TokenType.NUM_LITERAL;
                        else if (!isIdentifierChar(thisChar))
                            throw new RuntimeException("Invalid character");

                        tokens.add(new Token(currentType, currentPos, thisCharStr));
                        currentType = null;
                        currentValue = new StringBuilder();
                    }
                }
            } else {
                if (currentType != TokenType.QUOTE && (thisCharStr.isBlank() || (next != null && !isIdentifierChar(nextChar)) || !scanner.hasNext())) {

                    if ((!scanner.hasNext() || (next != null && !isIdentifierChar(nextChar))) && !thisCharStr.isBlank())
                        currentValue.append(thisChar);

                    String value = currentValue.toString();
                    switch (value) {
                        case "if" -> {
                            currentType = TokenType.IF;
                            if (tokens.get(tokens.size() - 1).type() == TokenType.ELSE)
                                currentType = TokenType.ELIF;
                        }
                        case "else" -> currentType = TokenType.ELSE;
                        case "TRUE" -> currentType = TokenType.TRUE;
                        case "FALSE" -> currentType = TokenType.FALSE;
                        case "while" -> currentType = TokenType.WHILE;
                        case "func" -> currentType = TokenType.FUNC;
                        case "in" -> currentType = TokenType.IN;
                        case "for" -> currentType = TokenType.FOR;
                        case "return" -> currentType = TokenType.RETURN;
                        default -> {
                            if (isNumeric(value))
                                currentType = TokenType.NUM_LITERAL;
                        }
                    }

                    tokens.add(new Token(currentType, startPos, value));
                    currentType = null;
                    currentValue = new StringBuilder();
                } else {

                    // We can use whatever char and just skip the check for the first value
                    final char lastVal = currentValue.length() > 1 ? currentValue.charAt(currentValue.length() - 1) : 'a';

                    // If we're currently looking at a quote (make sure we check for escaped endings)
                    if (thisChar == '"' && lastVal != '\\') {
                        currentType = null;
                        tokens.add(new Token(TokenType.QUOTE, startPos, currentValue.toString()));
                        currentValue = new StringBuilder();
                    } else
                        currentValue.append(thisChar);
                }
            }
            currentPos++;
        }

        // End of file should be the index of the character after the last character in the file
        tokens.add(new Token(TokenType.EOF, currentPos, "<EOF>"));

        // Group FUNC then SYMBOL into one token
        for (int i = 0; i < tokens.size(); i++) {
            Token thisToken = tokens.get(i);
            if (thisToken.type() == TokenType.EOF)
                break;
            else if (thisToken.type() != TokenType.FUNC)
                continue;

            Token nextToken = tokens.get(i + 1);
            if (nextToken.type() != TokenType.SYMBOL)
                throw new RuntimeException("Invalid function declaration");

            Token newToken = new Token(TokenType.FUNC, thisToken.position(), nextToken.value());
            tokens.set(i, newToken);

            // This is ok, because we'll break at EOF anyway
            tokens.remove(++i);
        }

        return simplify(tokens, true);
    }

    /**
     * Simplify the tokens into more concise tokens. Do not allow function declarations.
     *
     * @param tokens The tokens to simplify.
     */
    private List<Token> simplify(List<Token> tokens) {
        return simplify(tokens, false);
    }

    /**
     * Simplify the tokens into more concise tokens and split into function declarations.
     *
     * @param tokens The tokens to simplify.
     * @param isRoot True if this is the first time calling this method and is to disable function declarations.
     */
    private List<Token> simplify(List<Token> tokens, boolean isRoot) {
        TokenStorage tokenStorage = new TokenStorage(tokens);

        List<Token> newTokens = new ArrayList<>();
        Token token = tokenStorage.consume();
        while (token != null && token.type() != TokenType.EOF) {

            // Parse a parenthetical expression
            if (token.type() == TokenType.OPEN_PARENTHESES) {
                List<Token> toSimplify = new ArrayList<>();
                final int startPos = token.position();

                token = tokenStorage.consume();
                int depth = 0;
                while (token != null && token.type() != TokenType.EOF) {
                    if (token.type() == TokenType.CLOSE_PARENTHESES && depth == 0) {
                        toSimplify.add(new Token(TokenType.EOF, token.position(), "<CLOSE PARENTHESES>"));

                        List<Token> content = simplify(toSimplify);
                        newTokens.add(new Token(TokenType.PARENTHETICAL_EXPR, startPos, content));
                        break;
                    } else if (token.type() == TokenType.SEMI)
                        throw new RuntimeException("Can not put multiple statements within parentheses, use braces instead");
                    else {
                        if (token.type() == TokenType.OPEN_PARENTHESES)
                            ++depth;
                        else if (token.type() == TokenType.CLOSE_PARENTHESES)
                            --depth;

                        toSimplify.add(token);
                    }

                    token = tokenStorage.consume();
                }
            } else if (token.type() == TokenType.SYMBOL && tokenStorage.peek().type() == TokenType.OPEN_PARENTHESES) {
                String identifier = (String) token.value();
                int callStartPos = token.position();

                tokenStorage.consume();
                token = tokenStorage.consume();

                // Parse arguments
                int depth = 0;
                final List<List<Token>> args = new ArrayList<>();
                List<Token> currentArg = new ArrayList<>();
                int argStartPos = -1;
                while (token.type() != null && token.type() != TokenType.EOF) {
                    if (argStartPos < 0)
                        argStartPos = token.position();

                    if (token.type() == TokenType.COMMA && depth == 0) {
                        currentArg.add(new Token(TokenType.EOF, argStartPos, "<ARG LIST COMMA>"));
                        args.add(simplify(currentArg));
                        currentArg = new ArrayList<>();
                        argStartPos = -1;
                    } else if (token.type() == TokenType.CLOSE_PARENTHESES && depth == 0) {
                        currentArg.add(new Token(TokenType.EOF, argStartPos, "<ARG LIST END>"));
                        args.add(simplify(currentArg));
                        currentArg = new ArrayList<>();
                        break;
                    } else if (token.type() == TokenType.SEMI)
                        throw new RuntimeException("Can not put multiple statements within parentheses, use braces instead");
                    else {
                        if (token.type() == TokenType.OPEN_PARENTHESES)
                            ++depth;
                        else if (token.type() == TokenType.CLOSE_PARENTHESES)
                            --depth;

                        currentArg.add(token);
                    }

                    token = tokenStorage.consume();
                }

                if (currentArg.size() != 0)
                    throw new RuntimeException("Function missing closing parentheses");

                if (args.get(args.size() - 1).size() == 1)
                    throw new RuntimeException("Extra comma in function call");

                FunctionCallData data = new FunctionCallData(identifier, args);
                newTokens.add(new Token(TokenType.FUNC_CALL, callStartPos, data));
            } else

                // Add every other token
                newTokens.add(token);

            if (tokenStorage.peek() != null)
                token = tokenStorage.consume();
        }
        newTokens.add(tokens.get(tokens.size() - 1));
        return newTokens;
    }
}
