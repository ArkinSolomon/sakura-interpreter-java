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

package net.sakura.interpreter.parser;

import net.sakura.interpreter.execution.ExecutionContext;
import net.sakura.interpreter.execution.ExecutionResult;
import net.sakura.interpreter.execution.Value;
import net.sakura.interpreter.lexer.Token;
import net.sakura.interpreter.lexer.TokenStorage;
import net.sakura.interpreter.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * Create a new parse tree.
 */
public final class Parser {

    private final TokenStorage tokenStorage;
    private final List<Node> expressions = new ArrayList<>();
    private final List<FunctionDefinition> functions = new ArrayList<>();

    private boolean stop = false;

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
     * Create the tree.
     */
    public List<Node> parse() {

        Node root = null;
        Node currentNode = null;

        while (true) {
            Token token = tokenStorage.consume();
            if (token.type() == TokenType.EOF) {
                if (root != null)
                    expressions.add(root);
                break;
            } else if (token.type() == TokenType.SEMI) {
                if (root != null) {
                    expressions.add(root);
                    root = null;
                    currentNode = null;
                }
                continue;
            } else if (token.type() == TokenType.EOL) {
                if (root != null && root.isCompletelyFull()) {
                    expressions.add(root);
                    root = null;
                    currentNode = null;
                }
                continue;
            }

            TokenType type = token.type();
            Node newNode = switch (type) {
                case DOUBLE_EQUALS -> new EqualityOperator(token);
                case LT, LTE, GT, GTE -> new NumericalComparison(token);
                case EQUALS -> new AssignmentOperator(token);
                case AND -> null;
                case OR -> null;
                case NOT -> null;
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
                case FOR -> null;
                case IN -> null;
                case RETURN -> new ReturnStatement(token);
                case BACKSLASH -> null;
                case SLASH -> new SlashOperator(token);
                case TRUE -> new TrueLiteral(token);
                case FALSE -> new FalseLiteral(token);
                case FUNC_DEF -> new FunctionDefinition(token);
                case FUNC_CALL -> new FunctionCall(token);
                case BRACE -> new BraceExpression(token, this);
                case PARENTHETICAL_EXPR -> new ParentheticalNode(token);
                case NUM_LITERAL -> new NumberLiteral(token);
                case SYMBOL -> new Symbol(token);
                default ->
                        throw new IllegalStateException("Unexpected value: " + type);
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

                assert newNode != null;
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
        return expressions;
    }

    /**
     * Execute every expression.
     */
    public ExecutionResult execute(ExecutionContext ctx) {

        // Register functions if we're executing the root context
        if (ctx.getRoot() == ctx) {
            for (FunctionDefinition function : functions)
                function.register(ctx);
        }

        for (Node expression : expressions) {
//            expression.print();
            Value value = expression.evaluate(ctx);
            if (expression instanceof Expression && stop || expression instanceof ReturnStatement)
                return new ExecutionResult(true, value);
        }
        return new ExecutionResult(false, Value.NULL);
    }
}
