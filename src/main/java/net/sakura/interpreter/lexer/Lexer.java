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

    String input;

    /**
     * Create a new instance which will perform lexical analysis on a string.
     *
     * @param input The input on which to perform the analysis.
     */
    public Lexer(String input) {
        this.input = input;
    }

    /**
     * Analyze the lexer text.
     */
    public List<Token> analyze() {
        List<Token> tokens = new ArrayList<>();

        final PeekableScanner scanner = new PeekableScanner(input);
        //        scanner.useDelimiter("");
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
                if (thisCharStr.isBlank() || thisChar == ';') {
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
                }else if (thisChar == '>') {
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
                else if (thisChar == '?')
                    tokens.add(new Token(TokenType.QUESTION, currentPos, "?"));
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
                    currentType = TokenType.LITERAL;
                    currentValue.append(thisChar);
                }
            } else {
                if ((currentType != TokenType.QUOTE && thisCharStr.isBlank()) || !scanner.hasNext() || (next !=null && !Character.isLetterOrDigit(nextChar))) {

                    if ((!scanner.hasNext()|| (next !=null && !Character.isLetterOrDigit(nextChar))) && !thisCharStr.isBlank() )
                        currentValue.append(thisChar);

                    String value = currentValue.toString();
                    switch (value) {
                        case "NULL" -> currentType = TokenType.NULL;
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
                    }

                    tokens.add(new Token(currentType, startPos, value));
                    currentType = null;
                    currentValue = new StringBuilder();
                } else {
                    if (thisChar == '"') {
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
        return tokens;
    }
}
