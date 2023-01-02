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

import java.util.Objects;

/**
 * An operator to determine equality between two values.
 */
final class EqualityOperator extends Operator {

    /**
     * Create a new operator from a token.
     *
     * @param token The token for the operator.
     */
    public EqualityOperator(Token token) {
        super(token);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        if (!isFull())
            throw new UnsupportedOperationException("Equality operator requires both arguments");

        Value lhs = leftChild().evaluate(ctx);
        Value rhs = rightChild().evaluate(ctx);

        Object leftValue = lhs.type() == DataType.NULL ? null : lhs.value();
        Object rightValue = rhs.type() == DataType.NULL ? null : rhs.value();

        if (lhs.type() == rhs.type() && leftValue == null || Objects.equals(leftValue, rightValue))
            return Value.TRUE;
        else return Value.FALSE;
    }

    @Override
    public int getPrecedence() {
        return Precedences.COMPARISON;
    }
}
