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
import net.sakura.interpreter.operations.Operation;

import java.io.File;

/**
 * A command to determine if a file exists.
 */
final class ExistsCommand extends SinglePathCommand {

    /**
     * Create a new command from a token.
     *
     * @param token This command's token.
     */
    public ExistsCommand(Token token){
        super(token);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        File path = getPath(ctx);

        if (!ctx.getOperationConfig().isValidReadPath(path))
            throw new SakuraException("Insufficient permission to determine if \"%s\" exists.".formatted(Operation.getFilePathStr(path)));

        return new Value(DataType.BOOLEAN, path.exists(), false);
    }
}
