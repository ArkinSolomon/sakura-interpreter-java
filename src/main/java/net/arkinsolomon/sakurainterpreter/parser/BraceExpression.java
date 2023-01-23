/*
 * Copyright (c) 2023. Arkin Solomon.
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

import net.arkinsolomon.sakurainterpreter.execution.DataType;
import net.arkinsolomon.sakurainterpreter.execution.ExecutionContext;
import net.arkinsolomon.sakurainterpreter.execution.ExecutionResult;
import net.arkinsolomon.sakurainterpreter.execution.Value;
import net.arkinsolomon.sakurainterpreter.lexer.Token;
import net.arkinsolomon.sakurainterpreter.lexer.TokenStorage;

import java.util.List;

/**
 * An expression wrapped in braces.
 */
final class BraceExpression extends Expression {

    private final Parser parent;
    private final Parser body;

    /**
     * Create a new brace expression from a token.
     *
     * @param token The token of the brace expression.
     */
    @SuppressWarnings("unchecked")

    public BraceExpression(Token token, Parser parent) {
        super(token, 0);
        this.parent = parent;

        List<Token> body = (List<Token>) token.value();
        TokenStorage ts = new TokenStorage(body);
        this.body = new Parser(ts);
        List<Node> nodes = this.body.parse(false, true);

        // Note that the children don't really do anything, but it's here to show up on the tree
        children = nodes.toArray(Node[]::new);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        ExecutionContext tempCtx = new ExecutionContext(ctx);
        body.resume();
        ExecutionResult result = body.execute(tempCtx);
        if (result.earlyReturnType() == EarlyReturnType.RETURN)
            parent.stop();
        return new Value(DataType.__BRACE_RETURN, result, false);
    }
}
