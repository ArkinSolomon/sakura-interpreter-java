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
import net.sakura.interpreter.lexer.TokenType;

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
        returnType = token.type() == TokenType.BREAK ? EarlyReturnType.BREAK : EarlyReturnType.CONTINUE;
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
