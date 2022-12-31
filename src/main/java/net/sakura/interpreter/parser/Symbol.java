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
import net.sakura.interpreter.Value;
import net.sakura.interpreter.lexer.Token;

/**
 * A symbol node.
 */
public class Symbol extends Node {

    private final String identifier;

    /**
     * Create a symbol using the token.
     */
    public Symbol(Token token){
        super(token, 0);
        identifier = token.value();
    }

    @Override
    public void assign(ExecutionContext ctx, Value val) {
        boolean hasId = ctx.hasIdentifier(identifier);

        if (hasId && !ctx.getIdentifier(identifier).isMutable())
            throw new UnsupportedOperationException("Can not assign to immutable variable");
        ctx.assignIdentifier(identifier, val);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        boolean hasId = ctx.hasIdentifier(identifier);

        return hasId ? ctx.getIdentifier(identifier) : Value.NULL;
    }
}
