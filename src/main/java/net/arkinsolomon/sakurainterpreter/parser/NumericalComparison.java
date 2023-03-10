/*
 * Copyright (c) 2023 Arkin Solomon.
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

import net.arkinsolomon.sakurainterpreter.exceptions.SakuraException;
import net.arkinsolomon.sakurainterpreter.execution.DataType;
import net.arkinsolomon.sakurainterpreter.execution.ExecutionContext;
import net.arkinsolomon.sakurainterpreter.execution.Value;
import net.arkinsolomon.sakurainterpreter.lexer.Token;

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
            throw new SakuraException(token, "Less-than operator requires both arguments");

        Value lhs = leftChild().evaluate(ctx);
        Value rhs = rightChild().evaluate(ctx);

        if (lhs.type() != DataType.NUMBER || rhs.type() != DataType.NUMBER)
            throw new SakuraException(token, "The numerical operators (>, >=, <, <=) can only compare numbers.");

        var leftValue = (double) lhs.value();
        var rightValue = (double) rhs.value();

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
