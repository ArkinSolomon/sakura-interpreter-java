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

import java.io.File;
import java.util.List;

/**
 * A node for commands that use only a single path.
 */
abstract class SinglePathCommand extends Node {

    /**
     * Create a new command using a token.
     *
     * @param token The token for creating this command, whose value is {@code List<Token>}
     */
    public SinglePathCommand(Token token){
        super(token, 1);

        @SuppressWarnings("unchecked")
        List<Token> pathTokens = (List<Token>) token.value();
        insertChild(Parser.parseTokensAsPath(token, pathTokens));
    }

    @Override
    public final void assign(ExecutionContext ctx, Value val) {
        throw new UnsupportedOperationException("Can not assign to commands");
    }

    /**
     * Evaluate and get the path to the file.
     *
     * @param ctx The execution context in which to evaluate the path.
     * @return The file that the child evaluates to.
     */
    protected final File getPath(ExecutionContext ctx) {
        Value val = getChild(0).evaluate(ctx);
        if (val.type() != DataType.PATH)
            throw new SakuraException("The argument to a command must be a path type.");

        return (File) val.value();
    }

    @Override
    public int getPrecedence() {
        return Precedences.VALUE;
    }
}
