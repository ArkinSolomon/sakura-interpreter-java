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

import net.sakura.interpreter.execution.ExecutionContext;
import net.sakura.interpreter.execution.Value;
import net.sakura.interpreter.lexer.Token;

/**
 * Base class for all prefix operators.
 */
abstract class PrefixOperator extends Node {

    /**
     * Create a new operator with a token.
     *
     * @param token The token of the operator.
     */
    protected PrefixOperator(Token token) {
        super(token, 1);
    }

    @Override
    public final void assign(ExecutionContext ctx, Value val) {
        throw new UnsupportedOperationException("Can not assign to prefix operator");
    }

    @Override
    public final int getPrecedence() {
        return Precedences.PREFIX;
    }

    /**
     * Get the child of this node.
     */
    public Node getChild() {
        return getChild(0);
    }
}
