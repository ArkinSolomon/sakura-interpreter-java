/*
 * Copyright (c) 2023. Arkin Solomon.
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
import net.arkinsolomon.sakurainterpreter.operations.Operation;
import net.arkinsolomon.sakurainterpreter.operations.RenameOperation;

import java.io.File;

/**
 * A command to rename a file to another name.
 */
final class RenameCommand extends DualArgCommand {

    /**
     * Create a new operation to rename a file.
     *
     * @param token The token that triggered this operation.
     */
    public RenameCommand(Token token){
        super(token, ParseType.PATH, ParseType.EXPR);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        Value left = leftValue(ctx);
        Value right = rightValue(ctx);

        if (left.type() != DataType.PATH)
            throw new SakuraException(getChild(0).token, "The left hand of a \"TO\" following a \"RENAME\" must be a path.");
        else if (right.type() != DataType.STRING)
            throw new SakuraException(getChild(1).token, "The right hand of a \"TO\" following a \"RENAME\" must be a string.");

        File original = (File) left.value();
        String newName = (String) right.value();

        if (newName.isBlank())
            throw new SakuraException(token, "Can not rename a file to an empty name. If you are trying to delete the file, use \"DELETE\" instead.");
        else if (newName.contains("/"))
            throw new SakuraException(token, "A file's new name can not contain a slash. If you are trying to move the file to a different location, use \"MOVE\" instead.");
        else if (newName.contains("\n"))
            throw new SakuraException(token, "A file's new name can not contain newline characters.");

        try {
            Operation renameOp = new RenameOperation(ctx, original, newName);
            ctx.getFileTracker().runOperation(renameOp);
        } catch (SakuraException e) {
            throw e.setPosition(token);
        }

        return Value.NULL;
    }
}
