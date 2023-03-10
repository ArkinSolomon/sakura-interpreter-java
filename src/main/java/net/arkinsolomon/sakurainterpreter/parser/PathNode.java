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

import com.google.errorprone.annotations.Var;
import java.io.File;
import net.arkinsolomon.sakurainterpreter.exceptions.SakuraException;
import net.arkinsolomon.sakurainterpreter.exceptions.UnexpectedTokenException;
import net.arkinsolomon.sakurainterpreter.execution.DataType;
import net.arkinsolomon.sakurainterpreter.execution.ExecutionContext;
import net.arkinsolomon.sakurainterpreter.execution.Value;
import net.arkinsolomon.sakurainterpreter.lexer.Token;
import net.arkinsolomon.sakurainterpreter.operations.Operation;

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
        @Var File path = null;
        @Var boolean hasParsedFirstPart = false;
        for (Node child : children) {
            Value childResult = child.evaluate(ctx);
            if (childResult.type() == DataType.STRING) {
                if (!hasParsedFirstPart)
                    throw new SakuraException(child.token.line(), child.token.column(), "First item of a path must evaluate to be of type path");

                var childPath = (String) childResult.value();

                if (childPath.contains("/"))
                    throw new UnexpectedTokenException(child.token.line(), child.token.column() + childPath.indexOf("/"), "\"/\"", "Path values can not contain slashes.");
                else if (childPath.contains("\n"))
                    throw new UnexpectedTokenException(child.token.line(), child.token.column() + childPath.indexOf("\n"), "\"\\n\"", "Path values can not contain newlines.");

                if (path == null)
                    path = new File(ctx.getRootPath(), childPath);
                else
                    path = new File(Operation.getFilePathStr(path), childPath);
            } else if (childResult.type() == DataType.PATH) {
                var childPath = (File) childResult.value();

                if (path == null)
                    path = childPath;
                else
                    path = new File(Operation.getFilePathStr(path), String.valueOf(childPath));
            } else
                throw new SakuraException(token, "Path parts must be strings or paths.");
            hasParsedFirstPart = true;
        }

        if (path == null)
            throw new SakuraException(token, "Path can not be empty.");

        // Ensure that the path is normalized which gets rid of the '..'s and '.'s
        return new Value(DataType.PATH, path.toPath().normalize().toFile(), false);
    }

    @Override
    public void assign(ExecutionContext ctx, Value val) {
        throw new UnsupportedOperationException("Can not assign to path");
    }

    @Override
    public int getPrecedence() {
        return Precedences.VALUE;
    }
}
