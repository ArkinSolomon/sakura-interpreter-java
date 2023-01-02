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
import net.sakura.interpreter.execution.Value;
import net.sakura.interpreter.lexer.Token;

/**
 * Operator for boolean inversion.
 */
final class NotOperator extends PrefixOperator{

    /**
     * Create a new not prefix operator using a token.
     *
     * @param token The prefix operator token.
     */
    public NotOperator(Token token) {
        super(token);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        if (!isFull())
            throw new RuntimeException("Boolean inversion requires a value to follow");

        Value childValue = getChild().evaluate(ctx);
        if (childValue.type() != DataType.BOOLEAN)
            throw new RuntimeException("Can not invert a non-boolean value");

        if ((boolean) childValue.value())
            return Value.FALSE;
        return Value.TRUE;
    }
}
