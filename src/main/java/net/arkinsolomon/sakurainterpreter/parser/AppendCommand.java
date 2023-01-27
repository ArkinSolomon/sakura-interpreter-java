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
import net.arkinsolomon.sakurainterpreter.execution.DataType;
import net.arkinsolomon.sakurainterpreter.execution.ExecutionContext;
import net.arkinsolomon.sakurainterpreter.execution.Value;
import net.arkinsolomon.sakurainterpreter.operations.AppendOperation;
import net.arkinsolomon.sakurainterpreter.operations.Operation;

import java.io.File;

/**
 * A command for appending text to a file.
 */
final class AppendCommand extends DualArgCommand {

    /**
     * Create a new command using a token.
     *
     * @param token The token to create the command.
     */
    public AppendCommand(Token token) {
        super(token, ParseType.EXPR, ParseType.PATH);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        Value left = leftValue(ctx);
        Value right = rightValue(ctx);

        if (left.type() == DataType.NULL)
            throw new SakuraException(token.line(), token.column(), "Can not write \"NULL\" to a file.");
        else if (right.type() != DataType.PATH)
            throw new SakuraException(token.line(), token.column(), "The right of a \"TO\" following an \"APPEND\" must be of type path.");

        String writeData = left.toString();
        File writeFile = (File) right.value();

        try {
            Operation appendOp = new AppendOperation(ctx, writeFile, writeData);
            ctx.getFileTracker().runOperation(appendOp);
        } catch (SakuraException e) {
            throw e.setPosition(token.line(), token.column());
        }

        return Value.NULL;
    }
}
