/*
 * Copyright (c) 2022-2023. Arkin Solomon.
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
import net.arkinsolomon.sakurainterpreter.exceptions.UnexpectedTokenException;
import net.arkinsolomon.sakurainterpreter.execution.Value;
import net.arkinsolomon.sakurainterpreter.lexer.Token;
import net.arkinsolomon.sakurainterpreter.execution.ExecutionContext;

/**
 * A node used by variables.
 */
class Variable extends Node {

    protected String identifier;

    /**
     * Create a new variable node.
     */
    public Variable(Token token) {
        super(token, 0);
        identifier = (String) token.value();
    }

    @Override
    public final int getPrecedence() {
        return Precedences.VALUE;
    }

    @Override
    public final Value evaluate(ExecutionContext ctx) {
        throw new UnexpectedTokenException(token.line(), token.column(), "\"$%s\"".formatted(identifier), "Can not evaluate a variable declaration. Did you mean \"%s\"?".formatted(identifier));
    }

    @Override
    public void assign(ExecutionContext ctx, Value val) {
        if (ctx.hasLocalIdentifier(identifier))
            throw new SakuraException(token.line(), token.column(), "Identifier \"%s\" already exists.".formatted(identifier));
        ctx.defineIdentifier(identifier, val.setMutability(true));
    }
}
