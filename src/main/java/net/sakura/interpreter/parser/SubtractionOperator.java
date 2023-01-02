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
 * An operator for the minus sign.
 */
final class SubtractionOperator extends Operator {

    /**
     * Create a new minus operator with a token.
     *
     * @param token The token to create the operator with.
     */
    public SubtractionOperator(Token token) {
        super(token);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        if (!isFull())
            throw new RuntimeException("Subtraction requires both operands");

        Value leftValue = leftChild().evaluate(ctx);
        Value rightValue = rightChild().evaluate(ctx);

        if (leftValue.type() != DataType.NUMBER || rightValue.type() != DataType.NUMBER)
            throw new RuntimeException("Invalid operand types for subtraction operator");

        double lhs = (double) leftValue.value();
        double rhs = (double) rightValue.value();
        return new Value(DataType.NUMBER, lhs - rhs, false);
    }

    @Override
    public int getPrecedence() {
        return Precedences.ADD_SUB;
    }
}
