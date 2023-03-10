/*
 * Copyright (c) 2022-2023 Arkin Solomon.
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
 * The assignment operator.
 */
final class AssignmentOperator extends Operator {

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

        leftChild().assign(ctx, assignmentValue.setMutability(true));

        // Assignment operators return the value of assignment
        return new Value(assignmentValue.type(), assignmentValue.value(), false);
    }

    @Override
    public int getPrecedence() {
        return Precedences.ASSIGNMENT;
    }
}
