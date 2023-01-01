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

import net.sakura.interpreter.execution.DataType;
import net.sakura.interpreter.execution.ExecutionContext;
import net.sakura.interpreter.execution.Value;
import net.sakura.interpreter.lexer.FunctionCallData;
import net.sakura.interpreter.lexer.Token;
import net.sakura.interpreter.lexer.TokenStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * Make a call to a function.
 */
public class FunctionCall extends Node {

    String identifier;

    /**
     * Make a function call using a token.
     *
     * @param token The function call token.
     */
    public FunctionCall(Token token) {
        super(token, ((FunctionCallData) token.value()).args().size());
        FunctionCallData data = (FunctionCallData) token.value();

        identifier = data.identifier();

        List<List<Token>> args = data.args();
        for (int i = 0; i < args.size(); i++) {
            List<Token> tokens = args.get(i);
            TokenStorage storage = new TokenStorage(tokens);
            Parser parser = new Parser(storage);

            List<Node> argument = parser.parse();

            if (argument.size() != 1)
                throw new RuntimeException("Function arguments can only be one expression");

            setChild(i, argument.get(0));
        }
    }

    @Override
    public void assign(ExecutionContext ctx, Value val) {
        throw new RuntimeException("Function call can not be assigned to");
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        if  (!ctx.hasIdentifier(identifier))
            throw new RuntimeException("Function does not exist");

        Value val = ctx.getIdentifier(identifier);
        if (val.type() != DataType.FUNCTION)
            throw new RuntimeException("Values of type %s are not callable".formatted(val.type()));

        List<Value> argValues = new ArrayList<>();
        for (Node child : children)
            argValues.add(child.evaluate(ctx));

        return ctx.executeFunc(identifier, argValues);
    }

    @Override
    public int getPrecedence() {
        return Precedences.VALUE;
    }
}
