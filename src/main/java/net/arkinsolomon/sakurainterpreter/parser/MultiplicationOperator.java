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

import net.arkinsolomon.sakurainterpreter.execution.DataType;
import net.arkinsolomon.sakurainterpreter.execution.ExecutionContext;
import net.arkinsolomon.sakurainterpreter.execution.Value;
import net.arkinsolomon.sakurainterpreter.lexer.Token;

/**
 * A node for multiplying.
 */
final class MultiplicationOperator extends Operator {

    /**
     * Create a new node from a token.
     */
    public MultiplicationOperator(Token token) {
        super(token);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        if (!isFull())
            throw new RuntimeException("Multiplication requires both operands");

        Value leftVal = leftChild().evaluate(ctx);
        Value rightVal = rightChild().evaluate(ctx);

        if (rightVal.type() != DataType.NUMBER)
            throw new RuntimeException("Right side of multiplication operator must be a number");

        var rhs = (double) rightVal.value();
        if (leftVal.type() == DataType.STRING) {
            var value = (String) leftVal.value();
            return new Value(DataType.STRING, value.repeat((int) Math.floor(rhs)), false);
        } else if (leftVal.type() == DataType.NUMBER) {
            var lhs = (double) leftVal.value();
            double val = lhs * rhs;
            return new Value(DataType.NUMBER, val, false);
        }

        throw new RuntimeException("Invalid operand types for multiplication operator");
    }

    @Override
    public int getPrecedence() {
        return Precedences.MULTIPLY_SLASH;
    }
}
