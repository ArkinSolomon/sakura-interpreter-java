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

import net.sakura.interpreter.DataType;
import net.sakura.interpreter.ExecutionContext;
import net.sakura.interpreter.Value;
import net.sakura.interpreter.lexer.Token;

/**
 * A slash operator, which can be a path or division.
 */
public class SlashOperator extends Operator {

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
        } else {
            return new Value(DataType.STRING, "NOT IMPLEMENTED", false);
        }
    }

    @Override
    public int getPrecedence() {
        return 35;
    }
}
