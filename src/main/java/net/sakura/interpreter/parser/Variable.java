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
 * A node used by variables.
 */
public class Variable extends Node {

    protected String identifier;

    /**
     * Create a new variable node.
     */
    public Variable(Token token) {
        super(token, 0);
        identifier = token.value();
    }

    @Override
    public final int getPrecedence() {
        return 100;
    }

    @Override
    public final Value evaluate(ExecutionContext ctx) {
        throw new UnsupportedOperationException("Can not evaluate a variable");
    }

    @Override
    public void assign(ExecutionContext ctx, Value val) {
        ctx.assignIdentifier(identifier, val);
    }
}
