/*
 * Copyright (c) 2023. Sakura Interpreter Java Contributors.
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
import net.arkinsolomon.sakurainterpreter.execution.DataType;
import net.arkinsolomon.sakurainterpreter.execution.ExecutionContext;
import net.arkinsolomon.sakurainterpreter.execution.Value;
import net.arkinsolomon.sakurainterpreter.lexer.Token;
import net.arkinsolomon.sakurainterpreter.operations.Operation;

import java.io.File;

/**
 * A command for determining if a path is a file.
 */
final class IsFileCommand extends SinglePathCommand {

    /**
     * Create a new node with the token that triggered this node's creation.
     *
     * @param token The token that created this node.
     */
    public IsFileCommand(Token token){
        super(token);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        File file = getPath(ctx);

        if (!ctx.getOperationConfig().isValidReadPath(file))
            throw new SakuraException("Insufficient permission to determine if \"%s\" is a file or directory.".formatted(Operation.getFilePathStr(file)));

        return new Value(DataType.BOOLEAN, file.isFile(), false);
    }
}
