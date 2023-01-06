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
import net.sakura.interpreter.exceptions.UnexpectedTokenException;
import net.sakura.interpreter.execution.DataType;
import net.sakura.interpreter.execution.ExecutionContext;
import net.sakura.interpreter.execution.Value;
import net.sakura.interpreter.lexer.Token;

import java.io.File;

/**
 * A node which represents a path.
 */
final class PathNode extends Node {

    /**
     * The token that created the path node.
     *
     * @param token The token that created the path node.
     */
    public PathNode(Token token) {
        super(token, 0);
    }

    /**
     * Add a child to the path.
     *
     * @param newChild The child to add to the path.
     */
    public void addChild(Node newChild) {
        resize(childCount + 1);
        insertChild(newChild);
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        File path = null;
        boolean hasParsedFirstPart = false;
        for (Node child : children) {
            Value childResult = child.evaluate(ctx);
            if (childResult.type() == DataType.STRING) {
                if (!hasParsedFirstPart)
                    throw new SakuraException(child.token.line(), child.token.column(), "First item of a path must evaluate to be of type path");

                String childPath = (String) childResult.value();

                if (childPath.contains("/"))
                    throw new UnexpectedTokenException(child.token.line(), child.token.column() + childPath.indexOf("/"), "\"/\"", "Path values can not contain slashes.");
                else if (childPath.contains("\n"))
                    throw new UnexpectedTokenException(child.token.line(), child.token.column() + childPath.indexOf("\n"), "\"\\n\"", "Path values can not contain newlines.");

                if (path == null)
                    path = new File(ctx.getRootPath(), childPath);
                else
                    path = new File(path.getAbsolutePath(), childPath);
            } else if (childResult.type() == DataType.PATH) {
                File childPath = (File) childResult.value();

                if (path == null)
                    path = childPath;
                else
                    path = new File(path.getAbsolutePath(), String.valueOf(childPath));
            } else
                throw new RuntimeException("Path parts must be strings or paths");
            hasParsedFirstPart = true;
        }
        return new Value(DataType.PATH, path, false);
    }

    @Override
    public void assign(ExecutionContext ctx, Value val) {
        throw new UnsupportedOperationException("Can not assign to path");
    }

    @Override
    public int getPrecedence() {
        throw new UnsupportedOperationException("Can not get the precedence of a path");
    }
}
