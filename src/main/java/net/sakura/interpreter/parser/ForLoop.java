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
import net.sakura.interpreter.execution.ExecutionResult;
import net.sakura.interpreter.execution.Iterable;
import net.sakura.interpreter.execution.StringIterable;
import net.sakura.interpreter.execution.Value;
import net.sakura.interpreter.lexer.ForLoopData;
import net.sakura.interpreter.lexer.Token;
import net.sakura.interpreter.lexer.TokenStorage;
import net.sakura.interpreter.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * A node which represents a for loop.
 */
final class ForLoop extends Expression {

    private final Parser parent;
    private final boolean isAssigneeConst;
    private final String identifier;

    /**
     * Create a for loop from a token.
     *
     * @param token The token to create the for loop from.
     */
    public ForLoop(Token token, Parser parent) {
        super(token, 2);
        this.parent = parent;

        ForLoopData data = (ForLoopData) token.value();
        isAssigneeConst = data.isConstant();
        identifier = data.loopVar();

        TokenStorage iterableTS = new TokenStorage(data.iterable());
        Parser iterableParser = new Parser(iterableTS);
        List<Node> iterableNodes = iterableParser.parse();
        if (iterableNodes.size() != 1)
            throw new RuntimeException("For loop iterable can only be one expression");
        setChild(0, iterableNodes.get(0));

        ArrayList<Token> bodyList = new ArrayList<>();
        bodyList.add(data.body());

        @SuppressWarnings("unchecked")
        List<Token> bodyTokens = (List<Token>) data.body().value();
        int eofPos = bodyTokens.get(bodyTokens.size() - 1).position();

        bodyList.add(new Token(TokenType.EOF, eofPos, "<WHILE BODY END>"));
        TokenStorage branchTokenStorage = new TokenStorage(bodyList);
        List<Node> bodyNodes = new Parser(branchTokenStorage).parse();

        if (bodyNodes.size() != 1)
            throw new RuntimeException("The body of a while loop must be wrapped in braces");

        setChild(1, bodyNodes.get(0));
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        Iterable loopIterable;
        Value iterableEvalResult = getChild(0).evaluate(ctx);
        if (iterableEvalResult.type() == DataType.STRING)
            loopIterable = new StringIterable((String) iterableEvalResult.value());
        else if (iterableEvalResult.type() == DataType.ITERABLE)
            loopIterable = (Iterable) iterableEvalResult.value();
        else
            throw new RuntimeException("For loops can only loop over iterables");

        Value curr = loopIterable.next();
        while (curr != null) {
            ExecutionContext tempCtx = new ExecutionContext(ctx);
            tempCtx.defineIdentifier(identifier, curr.setMutability(isAssigneeConst));

            Value braceReturn = getChild(1).evaluate(tempCtx);
            ExecutionResult result = (ExecutionResult) braceReturn.value();
            if (result.earlyReturn()){
                parent.stop();
                return result.returnValue();
            }

            curr = loopIterable.next();
        }

        return Value.NULL;
    }
}