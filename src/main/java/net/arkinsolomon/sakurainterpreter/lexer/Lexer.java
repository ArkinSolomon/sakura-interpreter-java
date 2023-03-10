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

import com.google.errorprone.annotations.Var;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import net.arkinsolomon.sakurainterpreter.exceptions.FileEmptyException;
import net.arkinsolomon.sakurainterpreter.exceptions.SakuraException;
import net.arkinsolomon.sakurainterpreter.exceptions.UnclosedParenthesisException;
import net.arkinsolomon.sakurainterpreter.exceptions.UnexpectedTokenException;
import org.apache.commons.text.StringEscapeUtils;

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
            throw new FileEmptyException(path.toString());
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
        } catch (NoSuchElementException e) {
            throw new FileEmptyException(null);
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
     * Check for basic multi-statement tokens or commands.
     *
     * @return True if the token is of a type that is determined to be a multi-statement token.
     */
    private static boolean isMultiStatement(Token token) {
        return token.isOfType(TokenType.SEMI, TokenType.RETURN, TokenType.CONTINUE, TokenType.BREAK,
                TokenType.IF, TokenType.ELIF, TokenType.ELSE,
                TokenType.WHILE, TokenType.FOR,
                TokenType.FUNC, TokenType.OPEN_BRACE, TokenType.CLOSE_BRACE,
                TokenType.READ, TokenType.PATH, TokenType.ISFILE, TokenType.ISDIR, TokenType.DELETE, TokenType.MKDIR, TokenType.MKDIRS, TokenType.EXISTS,
                TokenType.WRITE, TokenType.APPEND,
                TokenType.COPY, TokenType.MOVE, TokenType.RENAME);
    }

    /**
     * Check if an identifier is valid.
     *
     * @param identifier The identifier to check.
     * @return True if the identifier is valid.
     */
    private static boolean isValidIdentifier(String identifier) {
        if (identifier == null || identifier.length() == 0)
            return false;
        return identifier.matches("^\\w+$") && !identifier.substring(0, 1).matches("\\d");
    }

    // Note that this function sometimes adds multiple EOF tokens for nested expressions, it's not a problem since we stop when we see the next one so ??\_(???)_/??

    /**
     * Analyze the lexer text.
     */
    public List<Token> analyze() {
        @Var int currentLine = 1;
        @Var int currentCol = 1;

        @Var int startLine = -1;
        @Var int startCol = -1;

        @Var TokenType currentType = null;
        @Var StringBuilder currentValue = new StringBuilder();

        @Var boolean isInComment = false;
        @Var boolean incLine = false;

        while (scanner.hasNext()) {
            String thisCharStr = scanner.next();
            if (thisCharStr == null)
                break;

            if ((currentValue.length() == 0) && thisCharStr.isEmpty())
                continue;

            char thisChar = thisCharStr.charAt(0);
            if (incLine) {
                incLine = false;
                currentLine++;
                currentCol = 1;
            }

            // These following if statements handle comments properly, only continuing if it's a comment, which only occurs on non-newline chars, since the switch statement can handle newlines
            if (thisChar == '\n') {
                if (isInComment)
                    tokens.add(new Token(TokenType.EOL, currentLine, ++currentCol, "\\n"));

                isInComment = false;
                incLine = true;
            }

            if (isInComment)
                continue;

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
                if (thisChar == '$') {
                    if (nextChar != null && nextChar != '(')
                        currentType = TokenType.VARIABLE;
                    else {
                        tokens.add(new Token(TokenType.PATH_OPEN_PARENTHESIS, currentLine, currentCol, "$("));
                        currentCol++;
                        scanner.next();
                    }
                } else if (thisChar == '%')
                    currentType = TokenType.CONST_VAR;
                else if (thisChar == '@')
                    currentType = TokenType.ENV_VARIABLE;
                else if (thisChar == '#')
                    isInComment = true;
                else if (thisChar == '=') {
                    if (nextChar == null)
                        throw new UnexpectedTokenException(currentLine, currentCol, "=");

                    if (nextChar == '=') {
                        tokens.add(new Token(TokenType.DOUBLE_EQUALS, currentLine, currentCol, "=="));

                        // We need to increment by two, so we leave this here (similar for LTE and GTE)
                        scanner.next();
                        currentCol++;
                    } else
                        tokens.add(new Token(TokenType.EQUALS, currentLine, currentCol, "="));
                } else if (thisChar == '<') {
                    if (nextChar == null)
                        throw new UnexpectedTokenException(currentLine, currentCol, "<");

                    if (nextChar == '=') {
                        tokens.add(new Token(TokenType.LTE, currentLine, currentCol, "<="));

                        scanner.next();
                        currentCol++;
                    } else
                        tokens.add(new Token(TokenType.LT, currentLine, currentCol, "<"));
                } else if (thisChar == '>') {
                    if (nextChar == null)
                        throw new UnexpectedTokenException(currentLine, currentCol, ">");

                    if (nextChar == '=') {
                        tokens.add(new Token(TokenType.GTE, currentLine, currentCol, ">="));

                        scanner.next();
                        currentCol++;
                    } else
                        tokens.add(new Token(TokenType.GT, currentLine, currentCol, ">"));
                } else if (thisChar == '!') {
                    if (nextChar == null)
                        throw new UnexpectedTokenException(currentLine, currentCol, "!");

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
                else if (thisChar == '.') {

                    // Allow decimals
                    if (nextChar != null && Character.isDigit(nextChar)) {
                        currentType = TokenType.SYMBOL;
                        currentValue.append(thisChar);
                    } else
                        tokens.add(new Token(TokenType.PERIOD, currentLine, currentCol, "."));
                } else {
                    currentType = TokenType.SYMBOL;
                    currentValue.append(thisChar);

                    // Current value must be one character long
                    if (next == null || !isIdentifierChar(nextChar)) {
                        if (isNumeric(thisCharStr)) {

                            if (nextChar != null && nextChar == '.')
                                continue;
                            else
                                currentType = TokenType.NUM_LITERAL;
                        } else if (!isIdentifierChar(thisChar))
                            throw new UnexpectedTokenException(currentLine, currentCol, "\"%c\"".formatted(thisChar));

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
                    if ((currentType == TokenType.VARIABLE || currentType == TokenType.CONST_VAR || currentType == TokenType.ENV_VARIABLE) && !isValidIdentifier(value))
                        throw new UnexpectedTokenException(startLine, startCol, "The identifier \"%s\" is invalid".formatted(value));

                    // Find keywords
                    switch (value) {
                        case "if" -> {
                            currentType = TokenType.IF;
                            if (tokens.size() > 0 && tokens.get(tokens.size() - 1).isOfType(TokenType.ELSE)) {
                                tokens.remove(tokens.size() - 1);
                                currentType = TokenType.ELIF;
                            }
                        }
                        case "break" -> currentType = TokenType.BREAK;
                        case "continue" -> currentType = TokenType.CONTINUE;
                        case "return" -> currentType = TokenType.RETURN;
                        case "else" -> currentType = TokenType.ELSE;
                        case "while" -> currentType = TokenType.WHILE;
                        case "func" -> currentType = TokenType.FUNC;
                        case "in" -> currentType = TokenType.IN;
                        case "for" -> currentType = TokenType.FOR;
                        case "READ" -> currentType = TokenType.READ;
                        case "PATH" -> currentType = TokenType.PATH;
                        case "ISDIR" -> currentType = TokenType.ISDIR;
                        case "ISFILE" -> currentType = TokenType.ISFILE;
                        case "DELETE" -> currentType = TokenType.DELETE;
                        case "MKDIR" -> currentType = TokenType.MKDIR;
                        case "MKDIRS" -> currentType = TokenType.MKDIRS;
                        case "EXISTS" -> currentType = TokenType.EXISTS;
                        case "TO" -> currentType = TokenType.TO;
                        case "WRITE" -> currentType = TokenType.WRITE;
                        case "APPEND" -> currentType = TokenType.APPEND;
                        case "MOVE" -> currentType = TokenType.MOVE;
                        case "COPY" -> currentType = TokenType.COPY;
                        case "RENAME" -> currentType = TokenType.RENAME;
                        default -> {
                            if (isNumeric(value)){
                                if (Character.isDigit(thisChar) && nextChar != null && nextChar == '.')
                                    continue;
                                currentType = TokenType.NUM_LITERAL;
                            }
                        }
                    }

                    if (currentType == TokenType.SYMBOL && !isValidIdentifier(value))
                        throw new UnexpectedTokenException(startLine, startCol, "The identifier \"%s\" is invalid".formatted(value));

                    var newToken = new Token(currentType, startLine, startCol, value);
                    tokens.add(newToken);
                    currentType = null;
                    currentValue = new StringBuilder();
                } else {

                    // We can use whatever char and just skip the check for the first value if the string's length is 0
                     char lastVal = currentValue.length() > 0 ? currentValue.charAt(currentValue.length() - 1) : 'a';

                    // If we're currently looking at a quote (make sure we check for escaped endings)
                    if (thisChar == '"' && lastVal != '\\') {
                        currentType = null;

                        @Var String quoteValue = currentValue.toString();
                        quoteValue = StringEscapeUtils.unescapeJava(quoteValue);

                        tokens.add(new Token(TokenType.QUOTE, startLine, startCol, quoteValue));
                        currentValue = new StringBuilder();
                    } else if (thisChar == '"') {

                        // Current value size > 0 at this point
                        int lastIndex = currentValue.length() - 1;
                        currentValue.delete(lastIndex, currentValue.length());
                        currentValue.append('"');
                    } else
                        currentValue.append(thisChar);
                }
            }
            currentCol++;
        }

        // End of file should be the index of the character after the last character in the file
        tokens.add(new Token(TokenType.EOF, currentLine, currentCol, "<EOF>"));
        if (currentType == TokenType.QUOTE)
            throw new SakuraException(startLine, startCol, "Unclosed string literal (you are missing a quotation mark).");

        // Group FUNC then SYMBOL into one token and group three periods into an ellipsis
        for (int i = 0; i < tokens.size(); i++) {
            Token thisToken = tokens.get(i);
            if (thisToken.isOfType(TokenType.EOF))
                break;
            else if (thisToken.isOfType(TokenType.PERIOD)) {
                if (tokens.size() <= i + 2)
                    continue;
                Token next = tokens.get(i + 1);
                Token after = tokens.get(i + 2);

                if (next.isOfType(TokenType.PERIOD) && after.isOfType(TokenType.PERIOD)) {
                    tokens.set(i, new Token(TokenType.ELLIPSIS, thisToken.line(), thisToken.column(), "..."));
                    tokens.remove(next);
                    tokens.remove(after);
                }
                continue;
            } else if (!thisToken.isOfType(TokenType.FUNC))
                continue;

            // It's easier just to create a new TokenStore when there's a FUNC token, since we'd have to handle shifting the array
            var ts = new TokenStorage(tokens, i);
            Token nextToken = ts.nextNonEOLToken();

            if (!nextToken.isOfType(TokenType.SYMBOL))
                throw new UnexpectedTokenException(nextToken, "Did you name your function?");

            var newToken = new Token(TokenType.FUNC, thisToken.line(), thisToken.column(), nextToken.value());
            tokens.set(i, newToken);
            tokens.remove(nextToken);
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
     * Simplify the tokens into more concise tokens by merging tokens together.
     *
     * @param tokens The tokens to simplify.
     * @param isRoot True if this is the first time calling this method and is to enable function declarations.
     */
    private List<Token> simplify(List<Token> tokens, boolean isRoot) {
        @Var TokenStorage tokenStorage = new TokenStorage(tokens);

        @Var List<Token> newTokens = new ArrayList<>();
        @Var Token token = tokenStorage.consume();
        while (token != null && !token.isOfType(TokenType.EOF)) {

            // Parse a parenthetical expression
            Token lastNonEolToken = tokenStorage.lastNonEOLToken();
            if (token.isOfType(TokenType.OPEN_PARENTHESIS) && (lastNonEolToken == null || !lastNonEolToken.isOfType(TokenType.FUNC))) {
                List<Token> toSimplify = new ArrayList<>();
                 int startLine = token.line();
                 int startCol = token.column();

                token = tokenStorage.consume();
                @Var int depth = 0;
                while (token != null && !token.isOfType(TokenType.EOF)) {
                    if (token.isOfType(TokenType.CLOSE_PARENTHESIS) && depth == 0) {
                        toSimplify.add(new Token(TokenType.EOF, token.line(), token.column(), "<CLOSE PARENTHESES>"));

                        List<Token> content = simplify(toSimplify);
                        newTokens.add(new Token(TokenType.PARENTHETICAL_EXPR, startLine, startCol, content));
                        break;
                    } else if (isMultiStatement(token))
                        throw new UnexpectedTokenException(token);
                    else {
                        if (token.isOfType(TokenType.OPEN_PARENTHESIS))
                            ++depth;
                        else if (token.isOfType(TokenType.CLOSE_PARENTHESIS))
                            --depth;

                        toSimplify.add(token);
                    }

                    token = tokenStorage.consume();
                }
            } else if (token.isOfType(TokenType.SYMBOL) && tokenStorage.peekNextNonEOLToken() != null && tokenStorage.peekNextNonEOLToken().isOfType(TokenType.OPEN_PARENTHESIS)) {

                // Function calls
                var identifier = (String) token.value();
                int callStartLine = token.line();
                int callStartCol = token.column();

                // Skip over the FUNC and the first parentheses
                tokenStorage.nextNonEOLToken();
                token = tokenStorage.nextNonEOLToken();

                // Parse arguments
                @Var int depth = 0;
                 List<List<Token>> args = new ArrayList<>();
                @Var List<Token> currentArg = new ArrayList<>();

                @Var int argStartLine = -1;
                @Var int argStartCol = -1;

                @Var Token lastComma = null;

                if (!token.isOfType(TokenType.CLOSE_PARENTHESIS)) {
                    while (token != null && !token.isOfType(TokenType.EOF)) {
                        if (argStartLine < 0) {
                            argStartLine = token.line();
                            argStartCol = token.column();
                        }

                        if (token.isOfType(TokenType.COMMA) && depth == 0) {
                            currentArg.add(new Token(TokenType.EOF, argStartLine, argStartCol, "<ARG LIST COMMA>"));
                            args.add(simplify(currentArg));
                            currentArg = new ArrayList<>();
                            argStartLine = -1;
                            lastComma = token;
                        } else if (token.isOfType(TokenType.CLOSE_PARENTHESIS) && depth == 0) {
                            currentArg.add(new Token(TokenType.EOF, argStartLine, argStartCol, "<ARG LIST END>"));
                            args.add(simplify(currentArg));
                            currentArg = new ArrayList<>();
                            break;
                        } else if (isMultiStatement(token))
                            throw new UnexpectedTokenException(token);
                        else {
                            if (token.isOfType(TokenType.OPEN_PARENTHESIS))
                                ++depth;
                            else if (token.isOfType(TokenType.CLOSE_PARENTHESIS))
                                --depth;

                            currentArg.add(token);
                        }

                        token = tokenStorage.nextNonEOLToken();
                    }
                }

                assert token != null;
                if (currentArg.size() != 0 || token.isOfType(TokenType.EOF))
                    throw new UnexpectedTokenException(token, "Function missing closing parentheses");

                if (args.size() > 0 && args.get(args.size() - 1).size() == 1) {
                    assert lastComma != null;
                    throw new UnexpectedTokenException(lastComma, "Extra comma in function call");
                }

                var data = new FunctionCallData(identifier, args);
                newTokens.add(new Token(TokenType.FUNC_CALL, callStartLine, callStartCol, data));
            } else if (token.isOfType(TokenType.FUNC)) {

                //Function definitions
                if (!isRoot)
                    throw new UnexpectedTokenException(token, "Functions must be defined in global scope.");

                var functionIdentifier = (String) token.value();
                int funcDefStartLine = token.line();
                int funcDefStartCol = token.column();

                @Var Token firstDefaultToken = null;
                @Var boolean hasAnyDefault = false;

                // Parse arguments if there are parentheses
                List<FunctionArgData> args = new ArrayList<>();
                if (tokenStorage.peekNextNonEOLToken().isOfType(TokenType.OPEN_PARENTHESIS)) {

                    // Get rid of the first parenthesis and start with the first item in it
                    tokenStorage.nextNonEOLToken();
                    token = tokenStorage.nextNonEOLToken();

                    @Var Token argStartToken = null;
                    @Var String argId = null;
                    @Var boolean isConstant = false;
                    @Var boolean hasDefault = false;
                    @Var List<Token> defaultValue = new ArrayList<>();

                    if (!token.isOfType(TokenType.CLOSE_PARENTHESIS)) {
                        while (token != null && !token.isOfType(TokenType.EOF)) {

                            if (token.isOfType(TokenType.CONST_VAR)) {
                                argStartToken = token;
                                argId = (String) token.value();
                                isConstant = true;
                            } else if (token.isOfType(TokenType.VARIABLE)) {
                                argStartToken = token;
                                argId = (String) token.value();
                            } else if (token.isOfType(TokenType.EQUALS, TokenType.COMMA, TokenType.CLOSE_PARENTHESIS)) {
                                if (token.isOfType(TokenType.EQUALS)) {
                                    hasDefault = true;

                                    if (firstDefaultToken == null)
                                        firstDefaultToken = argStartToken;

                                    token = tokenStorage.consume();
                                    @Var int depth = 0;
                                    int defaultValueLine = token.line();
                                    int defaultValueColumn = token.column();

                                    while (token != null && !token.isOfType(TokenType.EOF)) {

                                        if (token.isOfType(TokenType.COMMA) || (token.isOfType(TokenType.CLOSE_PARENTHESIS) && depth == 0)) {
                                            defaultValue.add(new Token(TokenType.EOF, defaultValueLine, defaultValueColumn, "<ARG DEFAULT VAL END>"));
                                            break;
                                        } else if (isMultiStatement(token))
                                            throw new UnexpectedTokenException(token);
                                        else {
                                            if (token.isOfType(TokenType.OPEN_PARENTHESIS))
                                                ++depth;
                                            else if (token.isOfType(TokenType.CLOSE_PARENTHESIS))
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

                                if (hasDefault && hasAnyDefault)
                                    throw new SakuraException(argStartToken.line(), argStartToken.column(), "Required parameters can not follow optional parameters.");

                                hasAnyDefault = hasDefault;
                                var data = new FunctionArgData(argId, isConstant, false, hasDefault, defaultValue.size() == 0 ? null : simplify(defaultValue));
                                args.add(data);

                                argId = null;
                                isConstant = false;
                                hasDefault = false;
                                defaultValue = new ArrayList<>();

                                assert token != null;
                                if (token.isOfType(TokenType.CLOSE_PARENTHESIS))
                                    break;
                            }

                            if (token.isOfType(TokenType.CONST_VAR, TokenType.VARIABLE)) {
                                if (tokenStorage.lastToken().isOfType(TokenType.ELLIPSIS)) {

                                    if (!tokenStorage.nextNonEOLToken().isOfType(TokenType.CLOSE_PARENTHESIS))
                                        throw new UnexpectedTokenException(tokenStorage.currentToken(), "A closing parenthesis must follow a rest argument.");

                                    if (firstDefaultToken != null)
                                        throw new SakuraException(firstDefaultToken.line(), firstDefaultToken.column(), "Functions with rest arguments can not have arguments with defaults.");

                                    var argData = new FunctionArgData(argId, isConstant, true, false, null);
                                    args.add(argData);
                                    break;
                                }
                            }

                            token = tokenStorage.consume();
                        }
                    }
                }

                // We can set the body of the function to null for now, it'll be fixed later
                var data = new FunctionDefinitionData(functionIdentifier, args, null);
                newTokens.add(new Token(TokenType.FUNC_SIG, funcDefStartLine, funcDefStartCol, data));
            } else if (token.isOfType(TokenType.OPEN_BRACE)) {

                // Parse braces
                int braceStartLine = token.line();
                int braceStartCol = token.column();
                @Var int depth = 0;

                List<Token> body = new ArrayList<>();
                token = tokenStorage.consume();
                while (token != null && !token.isOfType(TokenType.EOF)) {

                    if (token.isOfType(TokenType.CLOSE_BRACE) && depth == 0) {
                        body.add(new Token(TokenType.EOF, token.line(), token.column(), "<CLOSE BRACE>"));
                        break;
                    } else {
                        if (token.isOfType(TokenType.OPEN_BRACE))
                            ++depth;
                        else if (token.isOfType(TokenType.CLOSE_BRACE))
                            --depth;

                        body.add(token);
                    }

                    token = tokenStorage.consume();
                }

                newTokens.add(new Token(TokenType.BRACE, braceStartLine, braceStartCol, simplify(body)));
            } else if (token.isOfType(TokenType.IF) || token.isOfType(TokenType.ELIF) || token.isOfType(TokenType.WHILE)) {

                TokenType statementType = token.type();
                int statementStartLine = token.line();
                int statementStartCol = token.column();

                token = tokenStorage.consume();
                @Var List<Token> condition = new ArrayList<>();
                while (token != null && !token.isOfType(TokenType.EOF) && !token.isOfType(TokenType.OPEN_BRACE)) {
                    condition.add(token);
                    token = tokenStorage.consume();
                }

                assert token != null;
                if (token.isOfType(TokenType.EOF))
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
            } else if (token.isOfType(TokenType.FOR)) {
                int loopStartLine = token.line();
                int loopStartCol = token.column();

                @Var boolean hasWrappingParentheses = false;

                // Get the variable
                token = tokenStorage.consume();
                @Var Token assignee = null;
                @Var boolean isConstVar = false;
                while (assignee == null && !token.isOfType(TokenType.EOF)) {
                    if (token.isOfType(TokenType.VARIABLE, TokenType.CONST_VAR)) {
                        assignee = token;
                        isConstVar = token.isOfType(TokenType.CONST_VAR);
                    } else if (token.isOfType(TokenType.OPEN_PARENTHESIS) && !hasWrappingParentheses)
                        hasWrappingParentheses = true;
                    else if (!token.isOfType(TokenType.EOL)) {
                        if (token.isOfType(TokenType.OPEN_PARENTHESIS))
                            throw new UnexpectedTokenException(token, "Only one set of parentheses can surround a for loop.");
                        throw new UnexpectedTokenException(token);
                    }
                    token = tokenStorage.consume();
                }

                if (assignee == null)
                    throw new UnexpectedTokenException(token);

                // Look for the in token
                while (token.isOfType(TokenType.EOL))
                    token = tokenStorage.consume();

                if (!token.isOfType(TokenType.IN))
                    throw new UnexpectedTokenException(token, "\"in\" must follow variable assignment in for loop.");

                // Consume the IN token
                token = tokenStorage.consume();

                List<Token> iterable = new ArrayList<>();
                while (token != null && !token.isOfType(TokenType.EOF) && !token.isOfType(TokenType.OPEN_BRACE)) {
                    if (isMultiStatement(token))
                        throw new UnexpectedTokenException(token);

                    iterable.add(token);
                    token = tokenStorage.consume();
                }

                if (hasWrappingParentheses && iterable.size() > 0) {
                    @Var int closeParenIndex = iterable.size() - 1;
                    if (iterable.get(closeParenIndex).isOfType(TokenType.CLOSE_PARENTHESIS))
                        iterable.remove(closeParenIndex);
                    else {
                        @Var Token lastToken = iterable.get(closeParenIndex);

                        while (closeParenIndex > 0 && lastToken.isOfType(TokenType.EOL))
                            lastToken = iterable.get(--closeParenIndex);

                        if (!lastToken.isOfType(TokenType.CLOSE_PARENTHESIS))
                            throw new UnclosedParenthesisException(lastToken.line(), lastToken.column());
                        iterable.remove(closeParenIndex);
                    }
                } else if (iterable.size() == 0) {
                    assert token != null;
                    throw new UnexpectedTokenException(token, "Missing iterable value of for loop.");
                }

                assert token != null;
                iterable.add(new Token(TokenType.EOF, token.line(), token.column(), "<ITERABLE END>"));

                var data = new ForLoopData((String) assignee.value(), isConstVar, simplify(iterable), null);
                newTokens.add(new Token(TokenType.FOR_ASSIGN, loopStartLine, loopStartCol, data));

                // We already consumed the brace
                continue;
            } else if (token.isOfType(TokenType.READ, TokenType.PATH, TokenType.ISFILE, TokenType.ISDIR, TokenType.DELETE, TokenType.MKDIR, TokenType.MKDIRS, TokenType.EXISTS, TokenType.TO_PATH, TokenType.COPY, TokenType.MOVE, TokenType.RENAME)) {

                // Parse tokens that expect a path to follow
                TokenType initialTokenType = token.type();
                Token startToken = token;

                // True if this command has a "TO" following it
                 boolean isPathTo = token.isOfType(TokenType.COPY, TokenType.MOVE, TokenType.RENAME);
                token = tokenStorage.consume();

                @Var Token terminator = null;

                List<Token> path = new ArrayList<>();

                while (token != null && !token.isOfType(TokenType.EOF)) {

                    // Replace \$ and \@s with literals
                    if (token.isOfType(TokenType.ENV_VARIABLE)) {
                        if (tokenStorage.lastToken() != null && tokenStorage.lastToken().isOfType(TokenType.BACKSLASH)) {
                            token = new Token(TokenType.PATH_LITERAL, token.line(), token.column(), (token.isOfType(TokenType.ENV_VARIABLE) ? "@" : "$") + token.value());
                            path.remove(path.size() - 1);
                        }
                    } else if (token.isOfType(TokenType.SYMBOL, TokenType.QUOTE)) {

                        // We want to treat quotes as a single path literal
                        if (token.isOfType(TokenType.QUOTE)) {
                            var quoteVal = (String) token.value();
                            if (quoteVal.contains("/"))
                                throw new UnexpectedTokenException(token.line(), token.column() + quoteVal.indexOf("/") + 1, "\"/\"", "Path can not contain slashes within quotes.");
                            else if (quoteVal.contains("\n"))
                                throw new UnexpectedTokenException(token.line(), token.column() + quoteVal.indexOf("\n") + 1, "\"\\n\"", "Path can not contain newlines within quotes.");
                        } else {
                            if (path.size() > 0) {
                                Token last = path.get(path.size() - 1);

                                if (last.isOfType(TokenType.ENV_VARIABLE, TokenType.QUOTE, TokenType.PARENTHETICAL_EXPR))
                                    throw new UnexpectedTokenException(token);
                                else if (last.isOfType(TokenType.SLASH))
                                    token = new Token(TokenType.PATH_LITERAL, token.line(), token.column(), token.value());
                                else {
                                    path.set(path.size() - 1, new Token(TokenType.PATH_LITERAL, last.line(), last.column(), last.value() + (String) token.value()));

                                    // Continue without adding the token
                                    token = tokenStorage.consume();
                                    continue;
                                }
                            }

                            token = new Token(TokenType.PATH_LITERAL, token.line(), token.column(), token.value());
                        }

                        token = new Token(TokenType.PATH_LITERAL, token.line(), token.column(), token.value());
                    } else if (token.isOfType(TokenType.BACKSLASH)) {

                        Token lastToken = tokenStorage.lastToken();
                        if (lastToken != null && lastToken.isOfType(TokenType.BACKSLASH)) {
                            token = new Token(TokenType.PATH_LITERAL, lastToken.line(), lastToken.column(), "\\");
                            path.remove(path.size() - 1);
                        }
                    } else if (token.isOfType(TokenType.PATH_OPEN_PARENTHESIS)) {

                        // Parse a path parentheses (parentheses that start with a $)
                        List<Token> toSimplify = new ArrayList<>();
                         int startLine = token.line();
                         int startCol = token.column();

                        token = tokenStorage.consume();
                        @Var int depth = 0;
                        while (token != null && !token.isOfType(TokenType.EOF)) {
                            if (token.isOfType(TokenType.CLOSE_PARENTHESIS) && depth == 0) {
                                toSimplify.add(new Token(TokenType.EOF, token.line(), token.column(), "<CLOSE PATH PARENTHESIS>"));

                                List<Token> content = simplify(toSimplify);
                                token = new Token(TokenType.PARENTHETICAL_EXPR, startLine, startCol, content);
                                break;
                            } else if (isMultiStatement(token))
                                throw new UnexpectedTokenException(token);
                            else {
                                if (token.isOfType(TokenType.OPEN_PARENTHESIS))
                                    ++depth;
                                else if (token.isOfType(TokenType.CLOSE_PARENTHESIS))
                                    --depth;

                                toSimplify.add(token);
                                token = tokenStorage.consume();
                            }
                        }
                    } else if (token.isOfType(TokenType.EOL, TokenType.SEMI)) {
                        terminator = token;
                        break;
                    } else if (isPathTo && token.isOfType(TokenType.TO))
                        break;
                    else if (!token.isOfType(TokenType.SLASH)) {
                        if (token.isOfType(TokenType.PLUS, TokenType.MINUS, TokenType.MULTIPLY, TokenType.PERIOD)) {
                            if (path.size() > 0) {
                                Token last = path.get(path.size() - 1);

                                if (last.isOfType(TokenType.PATH_LITERAL))
                                    path.set(path.size() - 1, new Token(TokenType.PATH_LITERAL, last.line(), last.column(), last.value() + (String) token.value()));
                                else
                                    path.add(new Token(TokenType.PATH_LITERAL, token.line(), token.column(), token.value()));

                                // The path is not complete
                                token = tokenStorage.consume();
                                continue;
                            }
                        } else
                            throw new UnexpectedTokenException(token, "Unexpected token in path literal.");
                    }

                    path.add(token);
                    token = tokenStorage.consume();
                }

                if (path.size() == 0)
                    throw new SakuraException(startToken.line(), startToken.column(), "File operation commands must be followed by a path.");

                Token last = path.get(path.size() - 1);
                path.add(new Token(TokenType.EOF, last.line(), last.column(), "<FILE I/O CMD END>"));
                newTokens.add(new Token(initialTokenType, startToken.line(), startToken.column(), path));

                if (terminator != null)
                    newTokens.add(terminator);

                if (token == null || token.isOfType(TokenType.TO)) {
                    if (token == null || !isPathTo)
                        throw new UnexpectedTokenException(token);

                    token = new Token(initialTokenType == TokenType.RENAME ? TokenType.TO_STR : TokenType.TO_PATH, token.line(), token.column(), token.value());
                    continue;
                } else if (isPathTo)
                    throw new UnexpectedTokenException(token, "Expected \"TO\" after a \"%s\" token.".formatted(initialTokenType));
            } else if (token.isOfType(TokenType.WRITE, TokenType.APPEND)) {

                // Parse [string] TO [path]
                TokenType initialTokenType = token.type();
                Token startToken = token;
                token = tokenStorage.consume();

                List<Token> firstPart = new ArrayList<>();
                while (token != null && !token.isOfType(TokenType.EOF)) {
                    if (token.isOfType(TokenType.TO)) {
                        firstPart.add(new Token(TokenType.EOF, startToken.line(), startToken.column(), "<CMD STR/TO/PATH STR END>"));
                        break;
                    } else if (isMultiStatement(token))
                        throw new UnexpectedTokenException(token, "A command can not contain multi-statement commands.");
                    else if (token.isOfType(TokenType.EOL))
                        throw new UnexpectedTokenException(token, "Commands must be contained on a single line.");
                    else
                        firstPart.add(token);

                    token = tokenStorage.consume();
                }

                assert token != null;
                if (firstPart.size() == 0)
                    throw new SakuraException("First part of a \"%s\" command can not be empty.".formatted(initialTokenType));
                else if (!firstPart.get(firstPart.size() - 1).isOfType(TokenType.EOF))
                    throw new UnexpectedTokenException(token, "A \"%s\" command requires an \"TO\" token to follow it.".formatted(initialTokenType));

                newTokens.add(new Token(initialTokenType, startToken.line(), startToken.column(), simplify(firstPart)));
                token = new Token(TokenType.TO_PATH, token.line(), token.column(), "TO_PATH");
                continue;
            } else if (token.isOfType(TokenType.TO_STR)) {
                int initialLine = token.line();
                int initialCol = token.column();

                token = tokenStorage.consume();
                List<Token> toSimplify = new ArrayList<>();
                while (token != null && !token.isOfType(TokenType.EOF, TokenType.EOL, TokenType.SEMI)) {
                    if (isMultiStatement(token))
                        throw new UnexpectedTokenException(token);
                    else
                        toSimplify.add(token);

                    token = tokenStorage.consume();
                }

                assert token != null;
                toSimplify.add(new Token(TokenType.EOF, token.line(), token.column(), "<TO_STR EOF>"));
                newTokens.add(new Token(TokenType.TO_STR, initialLine, initialCol, simplify(toSimplify)));

                // Make sure we add the line termination
                newTokens.add(token);
            } else

                // Simply every other token
                newTokens.add(token);

            if (tokenStorage.peek() != null)
                token = tokenStorage.consume();
        }

        // Add the EOF
        Token eofToken = tokens.get(tokens.size() - 1);
        newTokens.add(eofToken);

        // Link FUNC_SIG, IF_COND, FOR_COND, and WHILE_COND to their statements, as well as file commands to their TO
        tokenStorage = new TokenStorage(newTokens);
        newTokens = new ArrayList<>();

        token = tokenStorage.consume();

        while (token != null && !token.isOfType(TokenType.EOF)) {

            if (token.isOfType(TokenType.FUNC_SIG)) {
                if (!isRoot)
                    throw new UnexpectedTokenException(token, "Functions can only be declared in the global scope.");

                var data = (FunctionDefinitionData) token.value();
                int funcSigLine = token.line();
                int funcSigCol = token.column();

                token = tokenStorage.nextNonEOLToken();
                if (!token.isOfType(TokenType.BRACE))
                    throw new UnexpectedTokenException(token, "Function definitions require bodies wrapped in braces.");

                newTokens.add(new Token(TokenType.FUNC_DEF, funcSigLine, funcSigCol, data.addBody(token)));
            } else if (token.isOfType(TokenType.IF_COND, TokenType.WHILE_COND, TokenType.FOR_ASSIGN)) {
                int statementStartLine = token.line();
                int statementStartCol = token.column();

                TokenType statementType = token.type();
                Object statementTokenValue = token.value();

                @Var List<Token> condition = null;
                if (!token.isOfType(TokenType.FOR_ASSIGN))

                    //noinspection unchecked
                    condition = (List<Token>) token.value();

                Token body = tokenStorage.consume();
                if (!body.isOfType(TokenType.BRACE)) {
                    @Var String statementName = "If";
                    if (statementType == TokenType.WHILE_COND)
                        statementName = "While";
                    else if (statementType == TokenType.FOR_ASSIGN)
                        statementName = "For";
                    throw new SakuraException(token.line(), token.column(), "%s statement must be followed by a brace statement.".formatted(statementName));
                }

                if (statementType == TokenType.IF_COND) {
                    ArrayList<List<Token>> conditions = new ArrayList<>();
                    ArrayList<Token> branches = new ArrayList<>();
                    branches.add(body);
                    conditions.add(condition);

                    var data = new IfData(conditions, branches);
                    newTokens.add(new Token(TokenType.IF_STATEMENT, statementStartLine, statementStartCol, data));
                } else if (statementType == TokenType.WHILE_COND) {
                    var data = new WhileData(condition, body);
                    newTokens.add(new Token(TokenType.WHILE_LOOP, statementStartLine, statementStartCol, data));
                } else {
                    ForLoopData data = ((ForLoopData) statementTokenValue).addBody(body);
                    newTokens.add(new Token(TokenType.FOR_LOOP, statementStartLine, statementStartCol, data));
                }
            } else if (token.isOfType(TokenType.ELIF_COND, TokenType.ELSE)) {
                if (newTokens.size() == 0)
                    throw new SakuraException(token.line(), token.column(), "If-statement branch must be preceded by an if-statement");

                @Var int lastTokenIndex = newTokens.size() - 1;
                @Var Token lastToken = newTokens.get(lastTokenIndex);
                while (lastTokenIndex > 0 && lastToken.isOfType(TokenType.EOL))
                    lastToken = newTokens.get(--lastTokenIndex);

                if (!lastToken.isOfType(TokenType.IF_STATEMENT))
                    throw new SakuraException(token.line(), token.column(), "If-statement branch must be preceded by an if-statement");

                var data = (IfData) lastToken.value();

                // If there are more branches than conditions we've already reached the else
                if (data.branches().size() != data.conditions().size()){
                    if (token.isOfType(TokenType.ELIF_COND))
                       throw new UnexpectedTokenException(token, "An else-if block can not follow an else block.");
                    else
                        throw new UnexpectedTokenException(token, "An if-statement can only have one else block.");
                }

                if (token.isOfType(TokenType.ELIF_COND)) {
                    @SuppressWarnings("unchecked")
                    var condition = (List<Token>) token.value();
                    data.conditions().add(condition);
                }

                Token branch = tokenStorage.consume();
                if (!branch.isOfType(TokenType.BRACE)) {
                    if (token.isOfType(TokenType.ELIF_COND))
                        throw new SakuraException(token.line(), token.column(), "Else-if statement must be followed by a brace statement");
                    throw new SakuraException(token.line(), token.column(), "Else statements must be followed by a brace statement");
                }

                data.branches().add(branch);
            } else if (token.isOfType(TokenType.WRITE, TokenType.APPEND, TokenType.MOVE, TokenType.COPY, TokenType.RENAME)) {

                TokenType finalType = switch (token.type()) {
                    case WRITE -> TokenType.WRITE_CMD;
                    case APPEND -> TokenType.APPEND_CMD;
                    case MOVE -> TokenType.MOVE_CMD;
                    case COPY -> TokenType.COPY_CMD;
                    case RENAME -> TokenType.RENAME_CMD;
                    default ->
                            throw new IllegalStateException("Invalid/not implemented file command");
                };

                if (!tokenStorage.hasNext())
                    throw new IllegalStateException("Missing EOF token");

                Token nextToken = tokenStorage.consume();
                if (!nextToken.isOfType(TokenType.TO, TokenType.TO_PATH, TokenType.TO_STR))
                    throw new UnexpectedTokenException(nextToken, "Expected a \"TO\" after a \"%s\" command.".formatted(token.type()));

                @SuppressWarnings("unchecked")
                var data = new DualArgCmdData((List<Token>) token.value(), (List<Token>) nextToken.value());
                newTokens.add(new Token(finalType, token.line(), token.column(), data));
            } else
                newTokens.add(token);

            token = tokenStorage.consume();
        }

        newTokens.add(eofToken);
        return newTokens;
    }
}
