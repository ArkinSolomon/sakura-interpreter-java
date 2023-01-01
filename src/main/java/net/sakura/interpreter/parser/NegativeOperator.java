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

import net.sakura.interpreter.execution.DataType;
import net.sakura.interpreter.execution.ExecutionContext;
import net.sakura.interpreter.execution.Value;
import net.sakura.interpreter.lexer.Token;

/**
 * Create a new positive operator.
 */
public class NegativeOperator extends PrefixOperator {

    /**
     * Create a new operator from a token.
     *
     * @param token The token to create the operator from.
     */
    public NegativeOperator(Token token) {
        super(token);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        if (!isFull())
            throw new RuntimeException("Negative operator has no operand");

        Value val = getChild().evaluate(ctx);
        if (val.type() != DataType.NUMBER)
            throw new RuntimeException("Negative operator can only operate on a number");
        return new Value(DataType.NUMBER, -1 * ((double) val.value()), false);
    }
}
