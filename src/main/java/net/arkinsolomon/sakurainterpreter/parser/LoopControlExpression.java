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

import net.arkinsolomon.sakurainterpreter.execution.ExecutionContext;
import net.arkinsolomon.sakurainterpreter.execution.Value;
import net.arkinsolomon.sakurainterpreter.lexer.Token;
import net.arkinsolomon.sakurainterpreter.lexer.TokenType;

/**
 * A break expression to break out of a loop or brace statement.
 */
final class LoopControlExpression extends Expression {

    private final EarlyReturnType returnType;

    /**
     * Create a new break statement as a child.
     *
     * @param token The break statement token.
     */
    public LoopControlExpression(Token token) {
        super(token, 0);
        returnType = token.isOfType(TokenType.BREAK) ? EarlyReturnType.BREAK : EarlyReturnType.CONTINUE;
    }

    /**
     * Get the type of this node's return.
     *
     * @return The type of this node's return.
     */
    public EarlyReturnType getReturnType () {
        return returnType;
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        return Value.NULL;
    }
}
