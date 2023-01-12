/*
 * Copyright (c) 2023. Sakura Contributors.
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
import net.sakura.interpreter.execution.ExecutionContext;
import net.sakura.interpreter.execution.Value;
import net.sakura.interpreter.lexer.DualArgCmdData;
import net.sakura.interpreter.lexer.Token;

import java.util.List;

/**
 * A command with a "TO" in it.
 */
abstract class DualArgCommand extends Node {

    /**
     * Create a new dual argument command from a token.
     *
     * @param token     The token for the command.
     * @param leftType  How to parse the left side of the "TO".
     * @param rightType How to parse the right side of the "TO".
     */
    protected DualArgCommand(Token token, ParseType leftType, ParseType rightType) {
        super(token, 2);

        DualArgCmdData data = (DualArgCmdData) token.value();
        parseChild(0, leftType, data.first());
        parseChild(1, rightType, data.second());
    }

    /**
     * Evaluate the left child of this node.
     *
     * @param ctx The execution context in which to evaluate.
     * @return The value of the left node.
     */
    protected final Value leftValue(ExecutionContext ctx) {
        return getChild(0).evaluate(ctx);
    }

    /**
     * Evaluate the right child of this node.
     *
     * @param ctx The execution context in which to evaluate.
     * @return The value of the right node.
     */
    protected final Value rightValue(ExecutionContext ctx) {
        return getChild(1).evaluate(ctx);
    }

    /**
     * Parse a single child node and set it as a child.
     *
     * @param childIndex The index of the node to set the child as.
     * @param parseType  Which method to use when parsing this child.
     * @param tokens     The tokens to parse into a node.
     */
    private void parseChild(int childIndex, ParseType parseType, List<Token> tokens) {
        Node child;

        if (parseType == ParseType.EXPR) {
            List<Node> children = Parser.parseTokens(tokens);
            if (children.size() < 1)
                throw new SakuraException("File commands require expressions on either side of it's \"TO\".");
            else if (children.size() > 1)
                throw new SakuraException("Can not have multiple statements within a file command.");

            child = children.get(0);
        } else if (parseType == ParseType.PATH)
            child = Parser.parseTokensAsPath(token, tokens);
        else
            throw new IllegalStateException("Unimplemented parse type");

        setChild(childIndex, child);
    }

    @Override
    public void assign(ExecutionContext ctx, Value val) {
        throw new SakuraException("Can not assign to file command");
    }

    @Override
    public final boolean canBeChild() {
        return false;
    }

    @Override
    public int getPrecedence() {
        throw new SakuraException("Can not get the precedence of any dual argument command");
    }

    /**
     * An enumeration for determining parsing as paths or regular parsing (expression parsing).
     */
    protected enum ParseType {
        EXPR,
        PATH
    }
}
