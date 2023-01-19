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
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * A node for reading entire files.
 */
final class ReadCommand extends SinglePathCommand {

    /**
     * Create a new read statement from a token.
     *
     * @param token The token for the read statement.
     */
    public ReadCommand(Token token) {
        super(token);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        File fileToRead = getPath(ctx);

        if (!ctx.getOperationConfig().isValidReadPath(fileToRead))
            throw new SakuraException(token, "No read permissions for file \"%s\".".formatted(Operation.getFilePathStr(fileToRead)));

        try {
            String fileContents = FileUtils.readFileToString(fileToRead, "utf-8");
            return new Value(DataType.STRING, fileContents, false);
        }catch (SakuraException e) {
            throw e.setPosition(token);
        } catch (Throwable e) {
            throw new SakuraException(token, "Error reading file.", e);
        }
    }
}
