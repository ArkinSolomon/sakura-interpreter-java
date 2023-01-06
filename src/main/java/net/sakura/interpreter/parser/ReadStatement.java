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
import net.sakura.interpreter.lexer.TokenStorage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

/**
 * A node for reading entire files.
 */
final class ReadStatement extends Node {

    /**
     * Create a new read statement from a token.
     *
     * @param token The token for the read statement.
     */
    public ReadStatement(Token token) {
        super(token, 1);

        @SuppressWarnings("unchecked")
        List<Token> tokens = (List<Token>) token.value();
        TokenStorage ts = new TokenStorage(tokens);
        Parser parser = new Parser(ts);
        Node child = parser.parseAsPath(token);
        insertChild(child);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        try {
            Value path = getChild(0).evaluate(ctx);
            String fileContents = FileUtils.readFileToString((File) path.value(), "utf-8");
            return new Value(DataType.STRING, fileContents, false);
        } catch (Throwable e) {
            throw new SakuraException("Error reading file", e);
        }
    }

    /**
     * Assign a value to this node.
     *
     * @param ctx The context in which to assign.
     * @param val The value which to assign.
     */
    @Override
    public void assign(ExecutionContext ctx, Value val) {
        throw new UnsupportedOperationException("Can not assign to read statement");
    }

    /**
     * The precedence of the node.
     *
     * @return The precedence of the node.
     */
    @Override
    public int getPrecedence() {
        return Precedences.VALUE;
    }
}
