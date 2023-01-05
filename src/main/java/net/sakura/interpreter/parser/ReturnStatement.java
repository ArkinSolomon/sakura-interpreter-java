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

import net.sakura.interpreter.execution.ExecutionContext;
import net.sakura.interpreter.execution.Value;
import net.sakura.interpreter.lexer.Token;

/**
 * A return statement.
 */
final class ReturnStatement extends Node {

    /**
     * Create a return statement from a token.
     *
     * @param token The return token.
     */
    public ReturnStatement(Token token) {
        super(token, 1);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        if (getChild(0) == null)
            return Value.NULL;
        return getChild(0).evaluate(ctx).setMutability(false);
    }

    @Override
    public void assign(ExecutionContext ctx, Value val) {
        throw new UnsupportedOperationException("Can not assign to return statement");
    }

    @Override
    public int getPrecedence() {
        return Precedences.RETURN;
    }

    @Override
    public boolean canBeChild() {
        return false;
    }
}
