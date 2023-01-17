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

package net.sakura.interpreter.parser;

import net.sakura.interpreter.exceptions.SakuraException;
import net.sakura.interpreter.exceptions.UnexpectedTokenException;
import net.sakura.interpreter.execution.DataType;
import net.sakura.interpreter.execution.ExecutionContext;
import net.sakura.interpreter.execution.ExecutionResult;
import net.sakura.interpreter.execution.Value;
import net.sakura.interpreter.lexer.Token;
import net.sakura.interpreter.lexer.TokenStorage;
import net.sakura.interpreter.lexer.TokenType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Create a new parse tree.
 */
public final class Parser {

    private final TokenStorage tokenStorage;
    private final List<Node> expressions = new ArrayList<>();
    private final List<FunctionDefinition> functions = new ArrayList<>();

    private boolean stop = false;
    private boolean registered;

    /**
     * Create a parse tree using the tokens from the lexer.
     *
     * @param tokenStorage The tokens from the lexer.
     */
    public Parser(TokenStorage tokenStorage) {
        this.tokenStorage = tokenStorage;
    }

    /**
     * Helper function to parse tokens directly as a path.
     *
     * @param trigger    The node that triggered the parsing.
     * @param pathTokens The nodes to parse.
     * @return The tokens parsed as a path.
     */
    public static PathNode parseTokensAsPath(Token trigger, List<Token> pathTokens) {
        TokenStorage ts = new TokenStorage(pathTokens);

        Parser pathParser = new Parser(ts);
        return pathParser.parseAsPath(trigger);
    }

    /**
     * Helper function to parse a group of tokens into nodes.
     *
     * @param tokens The tokens to parse.
     * @return The tokens parsed into nodes.
     */
    public static List<Node> parseTokens(List<Token> tokens) {
        TokenStorage ts = new TokenStorage(tokens);
        Parser parser = new Parser(ts);
        return parser.parse();
    }

    /**
     * Stop execution of this parser.
     */
    void stop() {
        stop = true;
    }

    /**
     * Continue execution of the parser.
     */
    void resume() {
        stop = false;
    }

    /**
     * Create the tree and check the top level and if-statements of the tree for any break or continue statements, and without checking for standalone variables.
     *
     * @return The nodes from the created tree.
     */
    public List<Node> parse() {
        return parse(true);
    }

    /**
     * Create the tree, without checking for standalone variables.
     *
     * @param checkTopLevel True if to check for break or continue statements in the top level and if-statements of the tree.
     * @return The nodes from the created tree.
     */
    List<Node> parse(boolean checkTopLevel) {
        return parse(checkTopLevel, false);
    }

    /**
     * Create the tree.
     *
     * @param checkTopLevel      True if to check for break or continue statements in the top level and if-statements of the tree.
     * @param checkForStandalone True if to check for literals without operators.
     * @return The nodes from the created tree.
     */
    List<Node> parse(boolean checkTopLevel, boolean checkForStandalone) {

        Node root = null;
        Node currentNode = null;

        boolean expectNewLine = false;
        Token exprStartToken = null;

        while (true) {
            Token token = tokenStorage.consume();

            if (exprStartToken == null)
                exprStartToken = token;

            if (token.isOfType(TokenType.EOF)) {
                if (root != null) {
                    //                    if (expectNewLine)
                    //                        throw new UnexpectedTokenException(exprStartToken, "Can not have multiple expressions on a single line.");
                    // Return statements have optional children so add a return statement, even if it's not full
                    if (!root.isCompletelyFull() && !(root instanceof ReturnStatement))
                        throw new UnexpectedTokenException(exprStartToken);
                    expressions.add(root);
                }
                break;
            } else if (token.isOfType(TokenType.SEMI)) {
                if (root != null) {
                    if (expectNewLine)
                        throw new UnexpectedTokenException(exprStartToken, "Can not have multiple expressions on a single line.");
                    expressions.add(root);
                    root = null;
                    exprStartToken = null;
                    currentNode = null;
                }
                expectNewLine = false;
                continue;
            } else if (token.isOfType(TokenType.EOL)) {
                //                if (root != null && root.isCompletelyFull()) {
                //                    expressions.add(root);
                //                    root = null;
                //                    currentNode = null;
                //                    exprStartToken = null;
                //                }
                expectNewLine = false;
                continue;
            }

            TokenType type = token.type();

            Node newNode = switch (type) {
                case EQUALS -> new AssignmentOperator(token);
                case NOT_EQUALS -> new NotEqualsOperator(token);
                case DOUBLE_EQUALS -> new EqualityOperator(token);
                case LT, LTE, GT, GTE -> new NumericalComparison(token);
                case AND, OR -> new BinaryBooleanOperator(token);
                case NOT -> new NotOperator(token);
                case PLUS -> {
                    if (tokenStorage.lastNonEOLToken() == null || tokenStorage.lastNonEOLToken().isOperator())
                        yield new PositiveOperator(token);
                    yield new AdditionOperator(token);
                }
                case MINUS -> {
                    if (tokenStorage.lastNonEOLToken() == null || tokenStorage.lastNonEOLToken().isOperator() || tokenStorage.lastNonEOLToken().isOfType(TokenType.FUNC_DEF, TokenType.IF_STATEMENT, TokenType.WHILE_LOOP, TokenType.FOR_LOOP))
                        yield new NegativeOperator(token);
                    yield new SubtractionOperator(token);
                }
                case MULTIPLY -> new MultiplicationOperator(token);
                case QUOTE -> new StringLiteral(token);
                case VARIABLE -> new Variable(token);
                case CONST_VAR -> new ConstVariable(token);
                case ENV_VARIABLE -> new EnvVariable(token);
                case IF_STATEMENT -> new IfStatement(token, this);
                case WHILE_LOOP -> new WhileLoop(token, this);
                case FOR_LOOP -> new ForLoop(token, this);
                case RETURN -> new ReturnStatement(token);
                case BREAK, CONTINUE -> new LoopControlExpression(token);
                case SLASH -> new SlashOperator(token);
                case FUNC_DEF -> new FunctionDefinition(token);
                case FUNC_CALL -> new FunctionCall(token);
                case BRACE -> new BraceExpression(token, this);
                case PARENTHETICAL_EXPR -> new ParentheticalNode(token);
                case NUM_LITERAL -> new NumberLiteral(token);
                case SYMBOL -> new Symbol(token);
                case READ -> new ReadCommand(token);
                case PATH -> {
                    @SuppressWarnings("unchecked")
                    List<Token> pathTokens = (List<Token>) token.value();
                    yield parseTokensAsPath(token, pathTokens);
                }
                case ISDIR -> new IsDirCommand(token);
                case ISFILE -> new IsFileCommand(token);
                case DELETE -> new DeleteCommand(token);
                case MKDIR -> new MkdirCommand(token);
                case MKDIRS -> new MkdirsCommand(token);
                case EXISTS -> new ExistsCommand(token);
                case WRITE_CMD -> new WriteCommand(token);
                case APPEND_CMD -> new AppendCommand(token);
                case MOVE_CMD -> new MoveCommand(token);
                case COPY_CMD -> new CopyCommand(token);
                case RENAME_CMD -> new RenameCommand(token);
                default -> {
                    String message = switch (type) {
                        case CLOSE_PARENTHESIS ->
                                "Do you have a matching opening parenthesis?";
                        case CLOSE_BRACE ->
                                "Do you have a matching opening brace?";
                        default -> null;
                    };
                    throw new UnexpectedTokenException(token, message);
                }
            };

            // Do not add anything that is not allowed to be a child
            if (!newNode.canBeChild()) {
                if (root != null) {
                    if (root.isCompletelyFull()) {
                        expressions.add(root);
                        root = null;
                    } else
                        throw new UnexpectedTokenException(token, "Statements can not be part of other expressions.");
                }

                if (newNode instanceof Expression || newNode instanceof DualArgCommand) {
                    if (newNode instanceof FunctionDefinition)
                        functions.add((FunctionDefinition) newNode);
                    else {
                        if (expectNewLine)
                            throw new UnexpectedTokenException(token, "Can not have multiple expressions on a single line.");
                        expressions.add(newNode);
                    }

                    // We always expect an EOL to follow a statement
                    expectNewLine = true;
                    exprStartToken = null;
                } else {
                    root = newNode;
                    currentNode = newNode;
                    exprStartToken = token;
                }

                continue;
            }

            if (root == null) {
                root = newNode;
                if (expectNewLine)
                    throw new UnexpectedTokenException(exprStartToken, "Can not have multiple expressions on a single line.");
                exprStartToken = token;
            } else {
                Node insertionPoint = currentNode;
                Node replacementChild = null;

                // Climb the tree until we find somewhere to insert
                while (insertionPoint != null && insertionPoint.getPrecedence() >= newNode.getPrecedence()) {
                    replacementChild = insertionPoint;
                    insertionPoint = insertionPoint.getParent();
                }

                // Insert as root
                if (insertionPoint == null) {
                    if (!newNode.isFull())
                        newNode.insertChild(root);
                    else {
                        if (expectNewLine)
                            throw new UnexpectedTokenException(token, "Can not have multiple expressions on a single line.");
                        expressions.add(root);
                        expectNewLine = true;
                    }
                    exprStartToken = token;
                    root = newNode;
                } else {
                    if (replacementChild != null) {
                        int replacementIndex = insertionPoint.findChild(replacementChild);

                        if (!newNode.isFull()) {
                            newNode.insertChild(replacementChild);
                            insertionPoint.setChild(replacementIndex, newNode);
                        } else {
                            if (expectNewLine)
                                throw new UnexpectedTokenException(exprStartToken, "Can not have multiple expressions on a single line.");
                            expressions.add(root);
                            exprStartToken = token;
                            root = newNode;
                            expectNewLine = true;
                        }
                    } else
                        insertionPoint.insertChild(newNode);
                }
            }
            currentNode = newNode;
        }

        if (checkForStandalone) {
            for (Node expr : expressions) {
                if (expr instanceof Literal)
                    throw new UnexpectedTokenException(expr.getToken(), "Stand-alone literals are not allowed.");
            }
        }

        if (checkTopLevel) {
            checkLoopControl(expressions);
            for (FunctionDefinition function : functions)
                checkLoopControl(List.of(function.children));
        }

        return expressions;
    }

    /**
     * Parse the tokens provided in the constructor as a path.
     *
     * @param trigger The token that triggered the creation of the path. A read/write/delete/etc. token.
     * @return The tokens parsed as a path.
     */
    public PathNode parseAsPath(Token trigger) {
        if (!tokenStorage.hasNext())
            throw new SakuraException("Can not parse empty path.");

        PathNode node = new PathNode(trigger);

        Token token = tokenStorage.consume();

        // Determine what the path is relative too (the path root)
        if (token.isOfType(TokenType.SLASH))
            node.addChild(new RootPath(token));
        else if (token.isOfType(TokenType.ENV_VARIABLE, TokenType.PARENTHETICAL_EXPR)) {
            node.addChild(token.isOfType(TokenType.ENV_VARIABLE) ? new EnvVariable(token) : new ParentheticalNode(token));
            tokenStorage.consume();
        } else
            throw new UnexpectedTokenException(token, "Unexpected start of path, path must start with an expression of type \"Path\" or a slash to start from root. Did you mean to wrap your token in \"$()\"");

        if (tokenStorage.hasNext())
            token = tokenStorage.consume();
        else
            return node;
        while (token != null && !token.isOfType(TokenType.EOF)) {
            Node newNode = switch (token.type()) {
                case PATH_LITERAL -> new PathLiteral(token);
                case ENV_VARIABLE -> new EnvVariable(token);
                case PARENTHETICAL_EXPR -> new ParentheticalNode(token);
                default ->
                        throw new UnexpectedTokenException(token, "Invalid token in path literal.");
            };
            node.addChild(newNode);

            Token nextToken = tokenStorage.peek();
            if (!nextToken.isOfType(TokenType.SLASH, TokenType.EOF))
                throw new UnexpectedTokenException(nextToken, "Path parts must be separated by forward slashes.");

            token = tokenStorage.consume();
            if (token.isOfType(TokenType.EOF))
                break;
            token = tokenStorage.consume();
        }

        return node;
    }

    /**
     * Execute every expression, return the value as the result.
     *
     * @param ctx The context in which to execute the expressions.
     */
    public ExecutionResult execute(ExecutionContext ctx) {

        // Register functions if we're executing the root context
        final boolean isRoot = ctx.getRoot() == ctx;
        if (isRoot && !registered) {
            registered = true;
            for (FunctionDefinition function : functions)
                function.register(ctx);
        }

        for (Node expression : expressions) {
            //            expression.print();
            ExecutionResult braceReturnResult = null;
            Value value = expression.evaluate(ctx);
            if (value != null && value.type() == DataType.__BRACE_RETURN) {
                braceReturnResult = (ExecutionResult) value.value();
                value = braceReturnResult.returnValue();
            }

            // If the expression was a brace we want to handle it differently
            if (expression instanceof Expression && stop || expression instanceof ReturnStatement || expression instanceof LoopControlExpression) {
                EarlyReturnType type = EarlyReturnType.RETURN;

                if (braceReturnResult != null) {
                    TokenType returnerType = braceReturnResult.returner().type();
                    if (returnerType == TokenType.BREAK || returnerType == TokenType.CONTINUE)
                        type = returnerType == TokenType.BREAK ? EarlyReturnType.BREAK : EarlyReturnType.CONTINUE;
                }

                return new ExecutionResult(type, value, expression.getToken());
            }
        }
        return new ExecutionResult(EarlyReturnType.NONE, Value.NULL, null);
    }

    /**
     * Check for any breaks or continues in the top level of the list of nodes, as well as any if-statements.
     *
     * @param nodes The nodes to check.
     */
    private void checkLoopControl(List<Node> nodes) {
        for (Node node : nodes) {
            if (node instanceof NoOpExpression)
                continue;

            Token token = node.getToken();
            if (token.isOfType(TokenType.CONTINUE) || token.isOfType(TokenType.BREAK))
                throw new SakuraException(token.line(), token.column(), "A \"%s\" statement can only be within a loop.".formatted(token.isOfType(TokenType.CONTINUE) ? "continue" : "break"));
            else if (token.isOfType(TokenType.IF_STATEMENT) || token.isOfType(TokenType.BRACE)) {
                for (int i = 0; i < node.childCount; i++) {
                    Node child = node.getChild(i);
                    checkLoopControl(Collections.singletonList(child));
                }
            }
        }
    }
}
