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

import net.sakura.interpreter.ExecutionContext;
import net.sakura.interpreter.lexer.Lexer;
import net.sakura.interpreter.lexer.Token;
import net.sakura.interpreter.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * Create a new parse tree.
 */
public class Parser {

    private final Lexer lexer;
    private final List<Node> expressions;

    /**
     * Create a parse tree using the lexer
     *
     * @param lexer The lexer that tokenized the input.
     */
    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.expressions = new ArrayList<>();
    }

    /**
     * Create the tree.
     */
    public void parse() {

        Node root = null;
        Node currentNode = null;

        while (true) {
            Token token = lexer.consume();
            if (token.type() == TokenType.EOF && root != null) {
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
                case DOUBLE_EQUALS -> null;
                case LT -> null;
                case LTE -> null;
                case GT -> null;
                case GTE -> null;
                case EQUALS -> new AssignmentOperator(token);
                case AND -> null;
                case OR -> null;
                case NOT -> null;
                case PLUS -> {
                    if (lexer.lastToken().isOperator())
                            yield new PositiveOperator(token);
                    yield new AdditionOperator(token);
                }
                case MINUS -> {
                    if (lexer.lastToken().isOperator())
                        yield new NegativeOperator(token);
                    yield new SubtractionOperator(token);
                }
                case MULTIPLY -> new MultiplicationOperator(token);
                case QUOTE -> new StringLiteral(token);
                case COMMA -> null;
                case VARIABLE -> new Variable(token);
                case CONST_VAR -> new ConstVariable(token);
                case ENV_VARIABLE -> new EnvVariable(token);
                case IF -> null;
                case ELIF -> null;
                case ELSE -> null;
                case WHILE -> null;
                case FOR -> null;
                case IN -> null;
                case RETURN -> null;
                case BACKSLASH -> null;
                case SLASH -> new SlashOperator(token);
                case TRUE -> null;
                case FALSE -> null;
                case FUNC -> null;
                case OPEN_PARENTHESES -> null;
                case CLOSE_PARENTHESES -> null;
                case OPEN_BRACE -> null;
                case CLOSE_BRACE -> null;
                case NUM_LITERAL -> new NumberLiteral(token);
                case SYMBOL -> new Symbol(token);
                default ->
                        throw new IllegalStateException("Unexpected value: " + type);
            };

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
    }

    /**
     * Execute every expression.
     */
    public void execute(ExecutionContext rootContext) {
        for (Node expression : expressions) {
            expression.print();
            expression.evaluate(rootContext);
        }
    }
}
