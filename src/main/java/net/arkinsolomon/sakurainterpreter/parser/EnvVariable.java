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
 * An environment variable.
 */
final class EnvVariable extends Node {

    private final String identifier;

    /**
     * Create a new environment variable node using a token.
     */
    public EnvVariable(Token token){
        super(token, 0);
        identifier = "@" + token.value();
    }

    @Override
    public int getPrecedence() {
        return Precedences.VALUE;
    }

    @Override
    public void assign(ExecutionContext ctx, Value val) {
        throw new UnsupportedOperationException("Can not assign to environment variable");
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        return ctx.getIdentifier(identifier);
    }
}
