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

import net.sakura.interpreter.exceptions.SakuraException;
import net.sakura.interpreter.execution.DataType;
import net.sakura.interpreter.execution.ExecutionContext;
import net.sakura.interpreter.execution.Value;
import net.sakura.interpreter.lexer.Token;
import net.sakura.interpreter.operations.CopyOperation;
import net.sakura.interpreter.operations.Operation;

import java.io.File;

/**
 * A command to copy a file or directory from one location to another.
 */
final class CopyCommand extends DualArgCommand {

    public CopyCommand(Token token){
        super(token, ParseType.PATH, ParseType.PATH);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        Value left = leftValue(ctx);
        Value right = rightValue(ctx);

        if (left.type() != DataType.PATH || right.type() != DataType.PATH)
            throw new SakuraException(token, "Both the left and right hand sides of a \"TO\" following a \"COPY\" must both be paths.");

        File source = (File) left.value();
        File dest = (File) right.value();

        try {
            Operation copyOp = new CopyOperation(source, dest);
            ctx.getFileTracker().runOperation(copyOp);
        } catch (SakuraException e) {
            throw e.setPosition(token);
        }

        return Value.NULL;
    }
}
