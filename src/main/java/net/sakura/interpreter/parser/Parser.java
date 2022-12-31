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
import net.sakura.interpreter.lexer.Token;
import net.sakura.interpreter.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * Create a new parse tree.
 */
public class Parser {

    private final Token[] tokens;

    private List<Node> sentences;

    /**
     * Create a parse tree from a list of tokens.
     *
     * @param tokens The tokens to create the tree from.
     */
    public Parser(Token[] tokens) {
        this.tokens = tokens;
        this.sentences = new ArrayList<>();
    }

    /**
     * Create the tree.
     */
    public void createTree() {

        Node currentSentence = null;

        for (Token token : tokens) {
            TokenType type = token.type();

            Node newNode = switch (type) {
                case EOF, EOS -> null;
                case DOUBLE_EQUALS -> null;
                case LT -> null;
                case LTE -> null;
                case GT -> null;
                case GTE -> null;
                case EQUALS -> new AssignmentOperator(token);
                case AND -> null;
                case OR -> null;
                case NOT -> null;
                case PLUS -> null;
                case MINUS -> null;
                case MULTIPLY -> null;
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
                case QUESTION -> null;
                case TRUE -> null;
                case FALSE -> null;
                case FUNC -> null;
                case OPEN_PARENTHESES -> null;
                case CLOSE_PARENTHESES -> null;
                case OPEN_BRACE -> null;
                case CLOSE_BRACE -> null;
                case NUM_LITERAL -> new NumberLiteral(token);
                case SYMBOL -> new Symbol(token);
            };

            // Node is null if it's an end of sentence or end of file
            if (newNode == null) {
                if (currentSentence != null) {
                    sentences.add(currentSentence);
                    currentSentence = null;
                }
            } else {
                if (currentSentence == null)
                    currentSentence = newNode;
                else if (newNode instanceof Operator) {
                    if (!(currentSentence instanceof Operator)){
                        newNode.insertChild(currentSentence);
                        currentSentence = newNode;
                    } else {
                        Node lhs = currentSentence.getChild(1);
                        currentSentence.setChild(1, newNode);
                        newNode.insertChild(lhs);
                        currentSentence.print();
                    }
                } else
                    currentSentence.insertChild(newNode);
            }
        }
    }


    /**
     * Execute every sentence.
     */
    public void execute(ExecutionContext rootContext) {
        for (Node sentence : sentences) {
            sentence.print();
            sentence.evaluate(rootContext);
        }
    }
}
