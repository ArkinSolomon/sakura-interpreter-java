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
 * An operator for boolean ands or ors.
 */
final class BinaryBooleanOperator extends Operator {

    /**
     * Create a new operator using a token.
     *
     * @param token The token of the operator
     */
    public BinaryBooleanOperator(Token token) {
        super(token);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        if (!isFull())
            throw new RuntimeException("Binary boolean operators requires both operands");

        Value lhs = leftChild().evaluate(ctx);
        Value rhs = rightChild().evaluate(ctx);

        boolean isOr = Objects.equals(token.value(), "|");

        if (lhs.type() != DataType.BOOLEAN || rhs.type() != DataType.BOOLEAN)
            throw new RuntimeException("Can not %s non-boolean operators".formatted(isOr ? "OR" : "AND"));

        boolean leftValue = (boolean) lhs.value();
        boolean rightValue = (boolean) rhs.value();
        boolean returnValue = isOr ? leftValue || rightValue : leftValue && rightValue;

        return new Value(DataType.BOOLEAN, returnValue, false);
    }

    @Override
    public int getPrecedence() {
        return token.value() == "|" ?  Precedences.BINARY_OR : Precedences.BINARY_AND;
    }
}
