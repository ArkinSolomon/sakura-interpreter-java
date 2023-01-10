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
import net.sakura.interpreter.execution.ExecutionContext;
import net.sakura.interpreter.execution.Value;
import net.sakura.interpreter.lexer.Token;
import net.sakura.interpreter.operations.MkdirOperation;
import net.sakura.interpreter.operations.Operation;

import java.io.File;

/**
 * A command to make a directory (not recursively).
 */
final class MkdirCommand extends SinglePathCommand {

    /**
     * Create a new command using a token.
     *
     * @param token The token that created this command.
     */
    public MkdirCommand(Token token) {
        super(token);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        try {
            File path = getPath(ctx);

            Operation mkdirOp = new MkdirOperation(path, false);
            ctx.getFileTracker().runOperation(mkdirOp);
            return Value.NULL;
        } catch (SakuraException e){
            throw e.setPosition(token.line(), token.column());
        }
    }

    @Override
    public boolean canBeChild() {
        return false;
    }
}
