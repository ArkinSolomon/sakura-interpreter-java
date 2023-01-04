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
     * Stop execution of this parser.
     */
    void stop() {
        stop = true;
    }

    /**
     * Continue execution of the parser.
     */
    void unstop() {
        stop = false;
    }

    /**
     * Create the tree and check the top level and if-statements of the tree for any break or continue statements.
     *
     * @return The nodes from the created tree.
     */
    public List<Node> parse() {
        return parse(true);
    }

    /**
     * Create the tree.
     *
     * @param checkTopLevel True if to check for break or continue statements in the top level and if-statements of the tree.
     * @return The nodes from the created tree.
     */
    List<Node> parse(boolean checkTopLevel) {

        Node root = null;
        Node currentNode = null;

        while (true) {
            Token token = tokenStorage.consume();
            if (token.isOfType(TokenType.EOF)) {
                if (root != null)
                    expressions.add(root);
                break;
            } else if (token.isOfType(TokenType.SEMI)) {
                if (root != null) {
                    expressions.add(root);
                    root = null;
                    currentNode = null;
                }
                continue;
            } else if (token.isOfType(TokenType.EOL)) {
                if (root != null && root.isCompletelyFull()) {
                    expressions.add(root);
                    root = null;
                    currentNode = null;
                }
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
                    if (tokenStorage.lastToken() == null || tokenStorage.lastToken().isOperator())
                        yield new PositiveOperator(token);
                    yield new AdditionOperator(token);
                }
                case MINUS -> {
                    if (tokenStorage.lastToken() == null || tokenStorage.lastToken().isOperator())
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
                default -> {
                    String message = switch (type) {
                        case CLOSE_PARENTHESIS ->
                                "Do you have a matching opening parenthesis?";
                        default -> null;
                    };
                    throw new UnexpectedTokenException(token, message);

                }
            };

            // Do not add expressions to tree
            if (newNode instanceof Expression) {
                if (root != null)
                    throw new RuntimeException("Function definitions or statements can not be part of other expressions");

                if (newNode instanceof FunctionDefinition)
                    functions.add((FunctionDefinition) newNode);
                else
                    expressions.add(newNode);

                continue;
            }

            if (root == null)
                root = newNode;
            else {
                Node insertionPoint = currentNode;
                Node replacementChild = null;

                while (insertionPoint != null && insertionPoint.getPrecedence() >= newNode.getPrecedence()) {
                    replacementChild = insertionPoint;
                    insertionPoint = insertionPoint.getParent();
                }

                // Insert at root
                if (insertionPoint == null) {
                    newNode.insertChild(root);
                    root = newNode;
                } else {
                    if (replacementChild != null) {
                        int replacementIndex = insertionPoint.findChild(replacementChild);
                        newNode.insertChild(replacementChild);
                        insertionPoint.setChild(replacementIndex, newNode);
                    } else
                        insertionPoint.insertChild(newNode);
                }
            }
            currentNode = newNode;
        }

        if (checkTopLevel) {
            checkLoopControl(expressions);
            for (FunctionDefinition function : functions)
                checkLoopControl(List.of(function.children));
        }

        return expressions;
    }

    /**
     * Execute every expression.
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
