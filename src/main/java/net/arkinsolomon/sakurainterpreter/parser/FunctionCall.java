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
import net.arkinsolomon.sakurainterpreter.execution.ExecutionContext;
import net.arkinsolomon.sakurainterpreter.execution.Value;
import net.arkinsolomon.sakurainterpreter.lexer.Token;
import net.arkinsolomon.sakurainterpreter.lexer.TokenStorage;
import net.arkinsolomon.sakurainterpreter.lexer.FunctionCallData;

import java.util.ArrayList;
import java.util.List;

/**
 * Make a call to a function.
 */
final class FunctionCall extends Node {

    final String identifier;

    /**
     * Make a function call using a token.
     *
     * @param token The function call token.
     */
    public FunctionCall(Token token) {
        super(token, ((FunctionCallData) token.value()).args().size());
        var data = (FunctionCallData) token.value();

        identifier = data.identifier();

        List<List<Token>> args = data.args();
        for (int i = 0; i < args.size(); i++) {
            List<Token> tokens = args.get(i);
            var storage = new TokenStorage(tokens);
            var parser = new Parser(storage);

            List<Node> argument = parser.parse(false, false);

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
        if (!ctx.hasIdentifier(identifier))
            throw new SakuraException(token, "Function does not exist");

        List<Value> argValues = new ArrayList<>();
        for (Node child : children)
            argValues.add(child.evaluate(ctx));
        try {
            return ctx.executeFunc(identifier, argValues);
        } catch (SakuraException e) {
            if (!e.isLocationSet())
               throw e.setPosition(token.line(), token.column());
            e.addStackTraceItem(token.line(), token.column(), identifier);
            throw e;
        }
    }

    @Override
    public int getPrecedence() {
        return Precedences.VALUE;
    }
}
