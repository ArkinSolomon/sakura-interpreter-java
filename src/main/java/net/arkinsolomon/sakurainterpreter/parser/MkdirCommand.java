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
import net.arkinsolomon.sakurainterpreter.execution.ExecutionContext;
import net.arkinsolomon.sakurainterpreter.execution.Value;
import net.arkinsolomon.sakurainterpreter.operations.MkdirOperation;
import net.arkinsolomon.sakurainterpreter.operations.Operation;

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

            Operation mkdirOp = new MkdirOperation(ctx, path, false);
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
