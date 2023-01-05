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

import net.sakura.interpreter.execution.ExecutionContext;
import net.sakura.interpreter.execution.Value;
import net.sakura.interpreter.lexer.Token;

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
