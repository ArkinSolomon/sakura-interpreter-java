/*
 * Copyright (c) 2022-2023. Sakura Contributors.
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

import net.sakura.interpreter.exceptions.SakuraException;
import net.sakura.interpreter.execution.DataType;
import net.sakura.interpreter.execution.ExecutionContext;
import net.sakura.interpreter.execution.Value;
import net.sakura.interpreter.lexer.Token;

/**
 * A slash operator, which can be a path or division.
 */
final class SlashOperator extends Operator {

    /**
     * Create a new slash operator with a token.
     */
    public SlashOperator(Token token) {
        super(token);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        Value leftVal = leftChild().evaluate(ctx);
        Value rightVal = rightChild().evaluate(ctx);

        if (leftVal.type() == DataType.NUMBER && rightVal.type() == DataType.NUMBER) {
            double val = ((double) leftVal.value()) / ((double) rightVal.value());
            return new Value(DataType.NUMBER, val, false);
        }

        throw new SakuraException(token, "Invalid operands for \"/\" operator. Adding \"%s\" of type \"%s\" to \"%s\" of type \"%s\". If you meant to create a path, make sure you prefix it with \"PATH\".".formatted(leftVal.toString(), leftVal.type(), rightVal.toString(), rightVal.type()));
    }

    @Override
    public int getPrecedence() {
        return Precedences.MULTIPLY_SLASH;
    }
}
