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

import net.sakura.interpreter.execution.DataType;
import net.sakura.interpreter.execution.ExecutionContext;
import net.sakura.interpreter.execution.ExecutionResult;
import net.sakura.interpreter.execution.Value;
import net.sakura.interpreter.lexer.Token;
import net.sakura.interpreter.lexer.TokenStorage;

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
        this.body.parse(false, true);
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
