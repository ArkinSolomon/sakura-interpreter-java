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

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An instance of this class performs lexical analysis on a given string input
 * and creates tokens from it.
 */
public final class Lexer {

    private final List<Token> tokens = new ArrayList<>();
    private final PeekableScanner scanner;

    /**
     * Create a new instance which will perform lexical analysis on a file.
     *
     * @param path The file on which to perform the analysis.
     */
    public Lexer(Path path) throws IOException {
        try {
            scanner = new PeekableScanner(path);
        } catch (NoSuchElementException e) {
            throw new RuntimeException("File is empty", e);
        }
    }

    /**
     * Create a new instance which will perform lexical analysis on a string.
     *
     * @param input The string on which to perform the analysis.
     */
    public Lexer(String input) {
        try {
            scanner = new PeekableScanner(input);
        }catch (NoSuchElementException e){
            throw new RuntimeException("Input is empty", e);
        }
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
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isIdentifierChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    /**
     * Analyze the lexer text.
     */
    public List<Token> analyze() {
        int currentLine = 1;
        int currentCol = 1;

        int startLine = -1;
        int startCol = -1;

        TokenType currentType = null;
        StringBuilder currentValue = new StringBuilder();

        boolean incLine = false;

        while (scanner.hasNext()) {
            String thisCharStr = scanner.next();
            if (thisCharStr == null)
                break;

            if ((currentValue.length() == 0) && thisCharStr.isEmpty())
                continue;

            char thisChar = thisCharStr.charAt(0);
            if (incLine){
                incLine = false;
                currentLine++;
                currentCol = 1;
            }

            if (thisChar == '\n')
                incLine = true;


            String next = scanner.peek();
            Character nextChar = next == null ? null : next.charAt(0);

            if (currentType == null) {

                if (thisChar == '\n' || thisChar == ';') {
                    TokenType type = thisChar == '\n' ? TokenType.EOL : TokenType.SEMI;
                    tokens.add(new Token(type, currentLine, currentCol, thisCharStr));
                    currentCol++;
                    continue;
                } else if (thisCharStr.isBlank()) {
                    currentCol++;
                    continue;
                }

                startLine = currentLine;
                startCol = currentCol;
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
                        tokens.add(new Token(TokenType.DOUBLE_EQUALS, currentLine, currentCol, "=="));

                        // We need to increment by two, so we leave this here (similar for LTE and GTE)
                        scanner.next();
                        currentCol++;
                    } else
                        tokens.add(new Token(TokenType.EQUALS, currentLine, currentCol, "="));
                } else if (thisChar == '<') {
                    if (nextChar == null)
                        throw new RuntimeException("Dangling less than");

                    if (nextChar == '=') {
                        tokens.add(new Token(TokenType.LTE, currentLine, currentCol, "<="));

                        scanner.next();
                        currentCol++;
                    } else
                        tokens.add(new Token(TokenType.LT, currentLine, currentCol, "<"));
                } else if (thisChar == '>') {
                    if (nextChar == null)
                        throw new RuntimeException("Dangling greater than");

                    if (nextChar == '=') {
                        tokens.add(new Token(TokenType.GTE, currentLine, currentCol, ">="));

                        scanner.next();
                        currentCol++;
                    } else
                        tokens.add(new Token(TokenType.GT, currentLine, currentCol, ">"));
                } else if (thisChar == '!') {
                    if (nextChar == null)
                        throw new RuntimeException("Dangling exclamation point ");

                    if (nextChar == '=') {
                        tokens.add(new Token(TokenType.NOT_EQUALS, currentLine, currentCol, "!="));

                        scanner.next();
                        currentCol++;
                    } else
                        tokens.add(new Token(TokenType.NOT, currentLine, currentCol, "!"));
                } else if (thisChar == '"')
                    currentType = TokenType.QUOTE;
                else if (thisChar == '/')
                    tokens.add(new Token(TokenType.SLASH, currentLine, currentCol, "/"));
                else if (thisChar == '\\') // I left this for spaces maybe?
                    tokens.add(new Token(TokenType.BACKSLASH, currentLine, currentCol, "\\"));
                else if (thisChar == '&')
                    tokens.add(new Token(TokenType.AND, currentLine, currentCol, "&"));
                else if (thisChar == '|')
                    tokens.add(new Token(TokenType.OR, currentLine, currentCol, "|"));
                else if (thisChar == '+')
                    tokens.add(new Token(TokenType.PLUS, currentLine, currentCol, "+"));
                else if (thisChar == '-')
                    tokens.add(new Token(TokenType.MINUS, currentLine, currentCol, "-"));
                else if (thisChar == '*')
                    tokens.add(new Token(TokenType.MULTIPLY, currentLine, currentCol, "*"));
                else if (thisChar == '(')
                    tokens.add(new Token(TokenType.OPEN_PARENTHESIS, currentLine, currentCol, "("));
                else if (thisChar == ')')
                    tokens.add(new Token(TokenType.CLOSE_PARENTHESIS, currentLine, currentCol, ")"));
                else if (thisChar == '{')
                    tokens.add(new Token(TokenType.OPEN_BRACE, currentLine, currentCol, "{"));
                else if (thisChar == '}')
                    tokens.add(new Token(TokenType.CLOSE_BRACE, currentLine, currentCol, "}"));
                else if (thisChar == ',')
                    tokens.add(new Token(TokenType.COMMA, currentLine, currentCol, ","));
                else {
                    currentType = TokenType.SYMBOL;
                    currentValue.append(thisChar);

                    // Current value must be one character long
                    if (next == null || !isIdentifierChar(nextChar)) {
                        if (isNumeric(thisCharStr))
                            currentType = TokenType.NUM_LITERAL;
                        else if (!isIdentifierChar(thisChar))
                            throw new RuntimeException("Invalid character");

                        tokens.add(new Token(currentType, currentLine, currentCol, thisCharStr));
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
                            if (tokens.size() > 0 && tokens.get(tokens.size() - 1).type() == TokenType.ELSE) {
                                tokens.remove(tokens.size() - 1);
                                currentType = TokenType.ELIF;
                            }
                        }
                        case "else" -> currentType = TokenType.ELSE;
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

                    tokens.add(new Token(currentType, startLine, startCol, value));
                    currentType = null;
                    currentValue = new StringBuilder();
                } else {

                    // We can use whatever char and just skip the check for the first value
                    final char lastVal = currentValue.length() > 1 ? currentValue.charAt(currentValue.length() - 1) : 'a';

                    // If we're currently looking at a quote (make sure we check for escaped endings)
                    if (thisChar == '"' && lastVal != '\\') {
                        currentType = null;
                        tokens.add(new Token(TokenType.QUOTE, startLine, startCol, currentValue.toString()));
                        currentValue = new StringBuilder();
                    } else
                        currentValue.append(thisChar);
                }
            }
            currentCol++;
        }

        // End of file should be the index of the character after the last character in the file
        tokens.add(new Token(TokenType.EOF, currentLine, currentCol, "<EOF>"));

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

            Token newToken = new Token(TokenType.FUNC, thisToken.line(), thisToken.column(), nextToken.value());
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

    // Note that this function sometimes adds multiple EOF tokens for nested expressions, it's not a problem since we stop when we see the next one so ¯\_(ツ)_/¯

    /**
     * Simplify the tokens into more concise tokens by merging tokens together.
     *
     * @param tokens The tokens to simplify.
     * @param isRoot True if this is the first time calling this method and is to enable function declarations.
     */
    private List<Token> simplify(List<Token> tokens, boolean isRoot) {
        TokenStorage tokenStorage = new TokenStorage(tokens);

        List<Token> newTokens = new ArrayList<>();
        Token token = tokenStorage.consume();
        while (token != null && token.type() != TokenType.EOF) {

            // Parse a parenthetical expression
            if (token.type() == TokenType.OPEN_PARENTHESIS) {
                List<Token> toSimplify = new ArrayList<>();
                final int startLine = token.line();
                final int startCol = token.column();

                token = tokenStorage.consume();
                int depth = 0;
                while (token != null && token.type() != TokenType.EOF) {
                    if (token.type() == TokenType.CLOSE_PARENTHESIS && depth == 0) {
                        toSimplify.add(new Token(TokenType.EOF, token.line(), token.column(), "<CLOSE PARENTHESES>"));

                        List<Token> content = simplify(toSimplify);
                        newTokens.add(new Token(TokenType.PARENTHETICAL_EXPR, startLine, startCol, content));
                        break;
                    } else if (token.type() == TokenType.SEMI)
                        throw new RuntimeException("Can not put multiple statements within parentheses, use braces instead");
                    else if (token.type() == TokenType.RETURN)
                        throw new RuntimeException("Can not place return statement in parentheses");
                    else {
                        if (token.type() == TokenType.OPEN_PARENTHESIS)
                            ++depth;
                        else if (token.type() == TokenType.CLOSE_PARENTHESIS)
                            --depth;

                        toSimplify.add(token);
                    }

                    token = tokenStorage.consume();
                }
            } else if (token.type() == TokenType.SYMBOL && tokenStorage.peek().type() == TokenType.OPEN_PARENTHESIS) {

                // Function calls
                String identifier = (String) token.value();
                int callStartLine = token.line();
                int callStartCol = token.column();

                // Skip over the parenthesis and start with the first token of the list
                tokenStorage.consume();
                token = tokenStorage.consume();

                // Parse arguments
                int depth = 0;
                final List<List<Token>> args = new ArrayList<>();
                List<Token> currentArg = new ArrayList<>();

                int argStartLine = -1;
                int argStartCol = -1;

                if (token.type() != TokenType.CLOSE_PARENTHESIS) {
                    while (token != null && token.type() != TokenType.EOF) {
                        if (argStartLine < 0){
                            argStartLine = token.line();
                            argStartCol = token.column();
                        }

                        if (token.type() == TokenType.COMMA && depth == 0) {
                            currentArg.add(new Token(TokenType.EOF, argStartLine, argStartCol, "<ARG LIST COMMA>"));
                            args.add(simplify(currentArg));
                            currentArg = new ArrayList<>();
                            argStartLine = -1;
                        } else if (token.type() == TokenType.CLOSE_PARENTHESIS && depth == 0) {
                            currentArg.add(new Token(TokenType.EOF, argStartLine, argStartCol, "<ARG LIST END>"));
                            args.add(simplify(currentArg));
                            currentArg = new ArrayList<>();
                            break;
                        } else if (token.type() == TokenType.SEMI)
                            throw new RuntimeException("Can not put multiple statements within parentheses");
                        else {
                            if (token.type() == TokenType.OPEN_PARENTHESIS)
                                ++depth;
                            else if (token.type() == TokenType.CLOSE_PARENTHESIS)
                                --depth;

                            currentArg.add(token);
                        }

                        token = tokenStorage.consume();
                    }
                }

                if (currentArg.size() != 0)
                    throw new RuntimeException("Function missing closing parentheses");

                if (args.size() > 0 && args.get(args.size() - 1).size() == 1)
                    throw new RuntimeException("Extra comma in function call");

                FunctionCallData data = new FunctionCallData(identifier, args);
                newTokens.add(new Token(TokenType.FUNC_CALL, callStartLine, callStartCol, data));
            } else if (token.type() == TokenType.FUNC) {

                //Function definitions
                if (!isRoot)
                    throw new RuntimeException("Functions must be declared in global scope");

                String functionIdentifier = (String) token.value();
                int funcDefStartLine = token.line();
                int funcDefStartCol = token.column();

                // Parse arguments if there are parentheses
                List<FunctionArgData> args = new ArrayList<>();
                if (tokenStorage.peek().type() == TokenType.OPEN_PARENTHESIS) {

                    // Get rid of the first parenthesis and start with the first item in it
                    tokenStorage.consume();
                    token = tokenStorage.consume();

                    int argStartPos = -1;

                    String argId = null;
                    boolean isConstant = false;
                    boolean hasDefault = false;
                    List<Token> defaultValue = new ArrayList<>();

                    if (token.type() != TokenType.CLOSE_PARENTHESIS) {
                        while (token != null && token.type() != TokenType.EOF) {
                            if (argStartPos < 0)
                                argStartPos = token.line();

                            if (token.type() == TokenType.CONST_VAR) {
                                argId = (String) token.value();
                                isConstant = true;
                            } else if (token.type() == TokenType.VARIABLE)
                                argId = (String) token.value();
                            else if (token.type() == TokenType.EQUALS || token.type() == TokenType.COMMA || token.type() == TokenType.CLOSE_PARENTHESIS) {
                                if (token.type() == TokenType.EQUALS) {
                                    hasDefault = true;

                                    token = tokenStorage.consume();
                                    int depth = 0;
                                    int defaultValueLine = token.line();
                                    int defaultValueColumn = token.column();

                                    while (token != null && token.type() != TokenType.EOF) {

                                        if (token.type() == TokenType.COMMA || (token.type() == TokenType.CLOSE_PARENTHESIS && depth == 0)) {
                                            defaultValue.add(new Token(TokenType.EOF, defaultValueLine, defaultValueColumn, "<ARG DEFAULT VAL END>"));
                                            break;
                                        } else if (token.type() == TokenType.SEMI)
                                            throw new RuntimeException("Unexpected semi-colon in argument list");
                                        else {
                                            if (token.type() == TokenType.OPEN_PARENTHESIS)
                                                ++depth;
                                            else if (token.type() == TokenType.CLOSE_PARENTHESIS)
                                                --depth;

                                            defaultValue.add(token);
                                        }

                                        token = tokenStorage.consume();
                                    }
                                }

                                if (argId == null)
                                    throw new RuntimeException("Function argument list invalid");

                                if (hasDefault && defaultValue.size() <= 1)
                                    throw new RuntimeException("Unexpected equals sign");

                                FunctionArgData data = new FunctionArgData(argId, isConstant, hasDefault, defaultValue.size() == 0 ? null : simplify(defaultValue));
                                args.add(data);

                                argId = null;
                                isConstant = false;
                                hasDefault = false;
                                defaultValue = new ArrayList<>();

                                assert token != null;
                                if (token.type() == TokenType.CLOSE_PARENTHESIS)
                                    break;
                            }

                            token = tokenStorage.consume();
                        }
                    }
                }

                // We can set the body of the function to null for now, it'll be fixed later
                FunctionDefinitionData data = new FunctionDefinitionData(functionIdentifier, args, null);
                newTokens.add(new Token(TokenType.FUNC_SIG, funcDefStartLine, funcDefStartCol, data));
            } else if (token.type() == TokenType.OPEN_BRACE) {

                // Parse braces
                int braceStartLine = token.line();
                int braceStartCol = token.column();
                int depth = 0;

                List<Token> body = new ArrayList<>();
                token = tokenStorage.consume();
                while (token != null && token.type() != TokenType.EOF) {

                    if (token.type() == TokenType.CLOSE_BRACE && depth == 0) {
                        body.add(new Token(TokenType.EOF, token.line(), token.column(), "<CLOSE BRACE>"));
                        break;
                    } else {
                        if (token.type() == TokenType.OPEN_BRACE)
                            ++depth;
                        else if (token.type() == TokenType.CLOSE_BRACE)
                            --depth;

                        body.add(token);
                    }

                    token = tokenStorage.consume();
                }

                newTokens.add(new Token(TokenType.BRACE, braceStartLine, braceStartCol, simplify(body)));
            } else if (token.type() == TokenType.IF || token.type() == TokenType.ELIF || token.type() == TokenType.WHILE) {

                TokenType statementType = token.type();
                int statementStartLine = token.line();
                int statementStartCol = token.column();

                token = tokenStorage.consume();
                List<Token> condition = new ArrayList<>();
                while (token != null && token.type() != TokenType.EOF && token.type() != TokenType.OPEN_BRACE) {
                    condition.add(token);
                    token = tokenStorage.consume();
                }

                assert token != null;
                if (token.type() == TokenType.EOF)
                    throw new RuntimeException("Unexpected end of file while parsing conditional statement");

                if (condition.size() == 0) {
                    String name = switch (statementType) {
                        case IF -> "If";
                        case ELIF -> "Else-if";
                        case WHILE -> "While";
                        default ->
                                throw new IllegalStateException("Invalid statement type: " + statementType);
                    };
                    throw new RuntimeException("%s statement requires condition".formatted(name));
                }

                condition.add(new Token(TokenType.EOF, token.line(), token.column(), "<COND END>"));
                condition = simplify(condition);

                TokenType conditionalType = switch (statementType) {
                    case IF -> TokenType.IF_COND;
                    case ELIF -> TokenType.ELIF_COND;
                    case WHILE -> TokenType.WHILE_COND;
                    default ->
                            throw new IllegalStateException("Invalid statement type: " + statementType);
                };
                newTokens.add(new Token(conditionalType, statementStartLine, statementStartCol, condition));

                // We already consumed the brace, so parse the brace statement
                continue;
            } else if (token.type() == TokenType.FOR) {
                int loopStartLine = token.line();
                int loopStartCol = token.column();

                // Get the variable
                token = tokenStorage.consume();
                Token assignee = null;
                boolean isConstVar = false;
                while (assignee == null && token != null && token.type() != TokenType.EOF) {
                    if (token.type() == TokenType.VARIABLE || token.type() == TokenType.CONST_VAR) {
                        assignee = token;
                        isConstVar = token.type() == TokenType.CONST_VAR;
                    }
                    token = tokenStorage.consume();
                }

                if (assignee == null)
                    throw new RuntimeException("Unexpected end of file");

                // Look for the in token
                if (token.type() != TokenType.IN)
                    throw new RuntimeException("In statement must follow variable assignment in for loop");

                // Consume the IN token
                token = tokenStorage.consume();

                List<Token> iterable = new ArrayList<>();
                while (token != null && token.type() != TokenType.EOF && token.type() != TokenType.OPEN_BRACE) {
                    iterable.add(token);
                    token = tokenStorage.consume();
                }

                assert token != null;
                iterable.add(new Token(TokenType.EOF, token.line(), token.column(), "<ITERABLE END>"));

                ForLoopData data = new ForLoopData((String) assignee.value(), isConstVar, simplify(iterable), null);
                newTokens.add(new Token(TokenType.FOR_ASSIGN, loopStartLine, loopStartCol, data));

                // We already consumed the brace
                continue;
            } else

                // Simply every other token
                newTokens.add(token);

            if (tokenStorage.peek() != null)
                token = tokenStorage.consume();
        }

        // Add the EOF
        Token eofToken = tokens.get(tokens.size() - 1);
        newTokens.add(eofToken);

        // Link FUNC_SIG, IF_COND, FOR_COND, and WHILE_COND to their statements
        tokenStorage = new TokenStorage(newTokens);
        newTokens = new ArrayList<>();

        token = tokenStorage.consume();

        while (token != null && token.type() != TokenType.EOF) {

            if (token.type() == TokenType.FUNC_SIG) {
                if (!isRoot)
                    throw new RuntimeException("Functions can only be declared in the global scope");

                if (tokenStorage.peek().type() != TokenType.BRACE)
                    throw new RuntimeException("Function signature not followed by brace");

                FunctionDefinitionData data = (FunctionDefinitionData) token.value();
                newTokens.add(new Token(TokenType.FUNC_DEF, token.line(), token.column(), data.addBody(tokenStorage.consume())));
            } else if (token.type() == TokenType.IF_COND || token.type() == TokenType.WHILE_COND || token.type() == TokenType.FOR_ASSIGN) {
                int statementStartLine = token.line();
                int statementStartCol = token.column();

                TokenType statementType = token.type();
                Object statementTokenValue = token.value();

                List<Token> condition = null;
                if (token.type() != TokenType.FOR_ASSIGN)

                    //noinspection unchecked
                    condition = (List<Token>) token.value();

                tokenStorage.printTokens();
                Token body = tokenStorage.consume();
                if (body.type() != TokenType.BRACE) {
                    String statementName = "If";
                    if (statementType == TokenType.WHILE_COND)
                        statementName = "While";
                    else if (statementType == TokenType.FOR_ASSIGN)
                        statementName = "For";
                    throw new RuntimeException("%s statement must be followed by a brace statement".formatted(statementName));
                }

                if (statementType == TokenType.IF_COND) {
                    ArrayList<List<Token>> conditions = new ArrayList<>();
                    ArrayList<Token> branches = new ArrayList<>();
                    branches.add(body);
                    conditions.add(condition);

                    IfData data = new IfData(conditions, branches);
                    newTokens.add(new Token(TokenType.IF_STATEMENT, statementStartLine, statementStartCol, data));
                } else if (statementType == TokenType.WHILE_COND) {
                    WhileData data = new WhileData(condition, body);
                    newTokens.add(new Token(TokenType.WHILE_LOOP, statementStartLine, statementStartCol, data));
                } else {
                    ForLoopData data = ((ForLoopData) statementTokenValue).addBody(body);
                    newTokens.add(new Token(TokenType.FOR_LOOP, statementStartLine, statementStartCol, data));
                }
            } else if (token.type() == TokenType.ELIF_COND || token.type() == TokenType.ELSE) {
                if (newTokens.size() == 0)
                    throw new RuntimeException("If-statement branch must be preceded by an if-statement");
                Token lastToken = newTokens.get(newTokens.size() - 1);
                if (lastToken.type() != TokenType.IF_STATEMENT)
                    throw new RuntimeException("If-statement branch must be preceded by an if-statement");

                IfData data = (IfData) lastToken.value();
                if (token.type() == TokenType.ELIF_COND) {
                    @SuppressWarnings("unchecked")
                    List<Token> condition = (List<Token>) token.value();
                    data.conditions().add(condition);
                }

                Token branch = tokenStorage.consume();
                if (branch.type() != TokenType.BRACE)
                    throw new RuntimeException("Else-if or else statements must be followed by a brace statement");

                data.branches().add(branch);
            } else
                newTokens.add(token);

            token = tokenStorage.consume();
        }

        newTokens.add(eofToken);

        return newTokens;
    }
}
