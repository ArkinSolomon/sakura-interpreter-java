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

import net.arkinsolomon.sakurainterpreter.exceptions.SakuraException;
import net.arkinsolomon.sakurainterpreter.execution.DataType;
import net.arkinsolomon.sakurainterpreter.execution.ExecutionContext;
import net.arkinsolomon.sakurainterpreter.execution.Value;
import net.arkinsolomon.sakurainterpreter.lexer.Token;

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

        if (lhs.type() == DataType.STRING || rhs.type() == DataType.STRING) {
            String lVal = lhs.value().toString();
            String rVal = rhs.value().toString();

            return new Value(DataType.STRING, lVal + rVal, false);
        } else if (lhs.type() == DataType.NUMBER && rhs.type() == DataType.NUMBER)
            return new Value(DataType.NUMBER, (double) lhs.value() + (double) rhs.value(), false);

        throw new SakuraException(token, "Invalid operands for \"+\" operator. Adding \"%s\" of type \"%s\" to \"%s\" of type \"%s\".".formatted(lhs.toString(), lhs.type(), rhs.toString(), rhs.type()));
    }

    @Override
    public int getPrecedence() {
        return Precedences.ADD_SUB;
    }
}
