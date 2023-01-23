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

import net.arkinsolomon.sakurainterpreter.execution.Value;
import net.arkinsolomon.sakurainterpreter.lexer.Token;
import net.arkinsolomon.sakurainterpreter.execution.ExecutionContext;

/**
 * An operator for a left and right hand argument.
 */
abstract class Operator extends Node {

    /**
     * Create a new operator with two children.
     *
     * @param token The token of the operator
     */
    protected Operator(Token token) {
        super(token, 2);
    }

    @Override
    public final void assign(ExecutionContext ctx, Value val) {
        throw new UnsupportedOperationException("Can not assign to operator");
    }

    /**
     * Get the left child of the operator.
     *
     * @return The left child of the operator
     */
    protected final Node leftChild() {
        return children[0];
    }

    /**
     * Get the right hand side of the operator.
     *
     * @return The right child of the operator.
     */
    protected final Node rightChild() {
        return children[1];
    }
}
