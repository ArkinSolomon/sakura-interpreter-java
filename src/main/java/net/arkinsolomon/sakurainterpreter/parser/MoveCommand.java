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

import net.arkinsolomon.sakurainterpreter.exceptions.SakuraException;
import net.arkinsolomon.sakurainterpreter.lexer.Token;
import net.arkinsolomon.sakurainterpreter.operations.MoveOperation;
import net.arkinsolomon.sakurainterpreter.execution.DataType;
import net.arkinsolomon.sakurainterpreter.execution.ExecutionContext;
import net.arkinsolomon.sakurainterpreter.execution.Value;
import net.arkinsolomon.sakurainterpreter.operations.Operation;

import java.io.File;

/**
 * A command for moving one file or directory to another location.
 */
final class MoveCommand extends DualArgCommand {

    /**
     * Create a new move command using a token.
     *
     * @param token The token to create the command.
     */
    public MoveCommand(Token token) {
        super(token, ParseType.PATH, ParseType.PATH);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        Value left = leftValue(ctx);
        Value right = rightValue(ctx);

        if (left.type() != DataType.PATH || right.type() != DataType.PATH)
            throw new SakuraException(token, "Both the left and right hand sides of a \"TO\" following a \"MOVE\" must both be paths.");

        File source = (File) left.value();
        File dest = (File) right.value();

        try {
            Operation moveOp = new MoveOperation(ctx, source, dest);
            ctx.getFileTracker().runOperation(moveOp);
        } catch (SakuraException e) {
            throw e.setPosition(token);
        }

        return Value.NULL;
    }
}
