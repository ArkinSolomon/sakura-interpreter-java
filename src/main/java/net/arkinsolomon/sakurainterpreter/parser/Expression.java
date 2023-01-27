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

import net.arkinsolomon.sakurainterpreter.execution.ExecutionContext;
import net.arkinsolomon.sakurainterpreter.execution.Value;
import net.arkinsolomon.sakurainterpreter.lexer.Token;

/**
 * A single expression. Nodes that shouldn't be children or have children added.
 */
abstract class Expression extends Node {

    /**
     * Create a new expression from a token with a certain amount of children.
     *
     * @param token      The token for the expression.
     * @param childCount The number of children for the expression.
     */
    protected Expression(Token token, int childCount) {
        super(token, childCount);
    }

    @Override
    public final void assign(ExecutionContext ctx, Value val) {
        throw new RuntimeException("Can not assign to an expression");
    }

    @Override
    public final int getPrecedence() {
        throw new RuntimeException("Can not get precedence of expression");
    }

    @Override
    public final boolean canBeChild() {
        return false;
    }
}
