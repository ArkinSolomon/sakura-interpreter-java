/*
 * Copyright (c) 2023. Sakura Interpreter Java Contributors.
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
 * An operator to determine equality between two values.
 */
class EqualityOperator extends Operator {

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

        if (lhs.type() != rhs.type())
            return Value.FALSE;

        boolean isEqual = switch (lhs.type()) {
            case STRING, PATH -> lhs.value().equals(rhs.value());
            case NUMBER ->
                    Math.abs((double) lhs.value() - (double) rhs.value()) < 1e-12;
            case BOOLEAN -> (boolean) lhs.value() == (boolean) rhs.value();
            case NULL -> true;
            case FUNCTION, ITERABLE -> lhs.value() == rhs.value();
            default ->
                    throw new SakuraException(token.line(), token.column(), "Invalid comparison between operands both of type \"%s\".".formatted(lhs.type()));
        };

        return new Value(DataType.BOOLEAN, isEqual, false);
    }

    @Override
    public int getPrecedence() {
        return Precedences.EQUALITY;
    }
}
