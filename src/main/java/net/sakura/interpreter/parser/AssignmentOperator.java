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

import net.sakura.interpreter.ExecutionContext;
import net.sakura.interpreter.Value;
import net.sakura.interpreter.lexer.Token;

/**
 * The assignment operator.
 */
public class AssignmentOperator extends Operator {

    /**
     * Create a new node with a specific amount of children.
     *
     * @param token The token of the operator
     */
    public AssignmentOperator(Token token) {
        super(token);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        Value assignmentValue = rightChild().evaluate(ctx);
        leftChild().assign(ctx, new Value(assignmentValue.type(), assignmentValue.value(), true));
        return Value.NULL;
    }
}
