/*
 * Copyright (c) 2022-2023. Arkin Solomon.
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
 * A string in quotes.
 */
final class StringLiteral extends Literal {

    /**
     * Create a new string from a quote token.
     *
     * @param token The quote token.
     */
    public StringLiteral(Token token) {
        super(token);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        return new Value(DataType.STRING, token.value(), false);
    }
}
