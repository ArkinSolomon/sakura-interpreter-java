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
import net.sakura.interpreter.execution.Value;
import net.sakura.interpreter.lexer.Token;

/**
 * A number or string literal.
 */
class Literal extends Node {

    /**
     * Create a new literal from the given token.
     *
     * @param token The token to create the literal from.
     */
    protected Literal(Token token){
        super(token, 0);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        throw new UnsupportedOperationException("Evaluate called on literal base");
    }

    @Override
    public final void assign(ExecutionContext ctx, Value val) {
        throw new UnsupportedOperationException("Can not assign to literal");
    }

    @Override
    public final int getPrecedence() {
        return Precedences.VALUE;
    }
}
