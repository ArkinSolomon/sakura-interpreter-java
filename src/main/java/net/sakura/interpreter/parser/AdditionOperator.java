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
 * An addition operator.
 */
final class AdditionOperator extends Operator {

    /**
     * Create an addition operator from a token.
     *
     * @param token The token to create the operator from.
     */
    public AdditionOperator(Token token) {
        super(token);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        if (!isFull())
            throw new RuntimeException("Addition requires both operands");

        Value lhs = leftChild().evaluate(ctx);
        Value rhs = rightChild().evaluate(ctx);

        if (lhs.type() == DataType.STRING) {
            String value = (String) lhs.value();

            value += rhs.toString();
            return new Value(DataType.STRING, value, false);
        } else if (lhs.type() == DataType.NUMBER) {
            if (rhs.type() != DataType.NUMBER)
                throw new RuntimeException("Can not add a non-number to a number");

            return new Value(DataType.NUMBER, (double) lhs.value() + (double) rhs.value(), false);
        }

        throw new RuntimeException("Invalid operand types for addition operator");
    }

    @Override
    public int getPrecedence() {
        return Precedences.ADD_SUB;
    }
}
