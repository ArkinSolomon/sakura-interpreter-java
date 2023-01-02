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
 * A type of operator that compares two numbers.
 */
final class NumericalComparison extends Operator {

    /**
     * Create a new less-than operator from a token.
     *
     * @param token The token to create the operator from.
     */
    public NumericalComparison(Token token) {
        super(token);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        if (!isFull())
            throw new UnsupportedOperationException("Less-than operator requires both arguments");

        Value lhs = leftChild().evaluate(ctx);
        Value rhs = rightChild().evaluate(ctx);

        if (lhs.type() != DataType.NUMBER || rhs.type() != DataType.NUMBER)
            throw new UnsupportedOperationException("The numerical operators (>, >=, <, <=) can only compare numbers");

        double leftValue = (double) lhs.value();
        double rightValue = (double) rhs.value();

        boolean value = switch ((String) token.value()){
            case ">" -> leftValue > rightValue;
            case ">=" -> leftValue >= rightValue;
            case "<" -> leftValue < rightValue;
            case "<=" -> leftValue <= rightValue;
            default -> throw new IllegalStateException("Invalid value \"%s\" in numerical operator");
        };
        return new Value(DataType.BOOLEAN, value, false);
    }

    @Override
    public int getPrecedence() {
        return Precedences.COMPARISON;
    }
}
