/*
 * Copyright (c) 2022-2023. Sakura Interpreter Java Contributors.
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
 * A number literal.
 */
final class NumberLiteral extends Literal {

    /**
     * Create a new literal from a token.
     *
     * @param token The token to create the literal from.
     */
    public NumberLiteral(Token token){
        super(token);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        return new Value(DataType.NUMBER, Double.parseDouble((String) token.value()), false);
    }
}
