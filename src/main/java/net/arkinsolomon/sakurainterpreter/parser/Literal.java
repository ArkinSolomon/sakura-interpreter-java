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

import net.arkinsolomon.sakurainterpreter.execution.ExecutionContext;
import net.arkinsolomon.sakurainterpreter.execution.Value;
import net.arkinsolomon.sakurainterpreter.lexer.Token;

/**
 * A number or string literal.
 */
abstract class Literal extends Node {

    /**
     * Create a new literal from the given token.
     *
     * @param token The token to create the literal from.
     */
    protected Literal(Token token){
        super(token, 0);
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
