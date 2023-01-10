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
 * The root path relative to the execution context.
 */
final class RootPath extends Literal{

    /**
     * Create a new node with the token that created this root path.
     *
     * @param token The token that created this root path.
     */
    public RootPath(Token token) {
        super(token);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        return new Value(DataType.PATH, ctx.getRootPath(), false);
    }
}