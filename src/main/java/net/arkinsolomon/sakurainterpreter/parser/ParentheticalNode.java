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

package net.arkinsolomon.sakurainterpreter.parser;

import net.arkinsolomon.sakurainterpreter.exceptions.SakuraException;
import net.arkinsolomon.sakurainterpreter.execution.ExecutionContext;
import net.arkinsolomon.sakurainterpreter.execution.Value;
import net.arkinsolomon.sakurainterpreter.lexer.Token;
import net.arkinsolomon.sakurainterpreter.lexer.TokenStorage;

import java.util.List;

/**
 * A node for parenthetical statements.
 */
final class ParentheticalNode extends Node {

    /**
     * Create a new node with the parenthetical token.
     */
    public ParentheticalNode(Token token) {
        super(token, 1);

        @SuppressWarnings("unchecked")
        var content = new TokenStorage((List<Token>) token.value());
        var parser = new Parser(content);
        List<Node> childExpressions = parser.parse();
        if (childExpressions.size() > 1)
            throw new SakuraException(token.line(), token.column(), "Parentheses can not have more than one expression.");
        else if (childExpressions.size() < 1)
            throw new SakuraException(token.line(), token.column(), "Unexpected empty parentheses.");
        setChild(0, childExpressions.get(0));
    }

    @Override
    public void assign(ExecutionContext ctx, Value val) {
        throw new RuntimeException("Can not assign to parenthetical expression");
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        return getChild(0).evaluate(ctx);
    }

    @Override
    public int getPrecedence() {
        return Precedences.PARENTHETICAL;
    }
}
