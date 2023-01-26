/*
 * Copyright (c) 2023. Arkin Solomon.
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
import net.arkinsolomon.sakurainterpreter.execution.Iterable;
import net.arkinsolomon.sakurainterpreter.execution.StringIterable;
import net.arkinsolomon.sakurainterpreter.lexer.Token;
import net.arkinsolomon.sakurainterpreter.lexer.TokenStorage;
import net.arkinsolomon.sakurainterpreter.lexer.TokenType;
import net.arkinsolomon.sakurainterpreter.execution.DataType;
import net.arkinsolomon.sakurainterpreter.execution.DirectoryIterable;
import net.arkinsolomon.sakurainterpreter.execution.ExecutionContext;
import net.arkinsolomon.sakurainterpreter.execution.ExecutionResult;
import net.arkinsolomon.sakurainterpreter.execution.Value;
import net.arkinsolomon.sakurainterpreter.lexer.ForLoopData;

import java.io.File;
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
        List<Node> iterableNodes = iterableParser.parse(false);

        if (iterableNodes.size() > 1)
            throw new RuntimeException("For loop iterable can only be one expression");
        else if (iterableNodes.size() == 0)
            throw new RuntimeException("For loop iterable not provided");

        setChild(0, iterableNodes.get(0));

        ArrayList<Token> bodyList = new ArrayList<>();
        bodyList.add(data.body());

        @SuppressWarnings("unchecked")
        List<Token> bodyTokens = (List<Token>) data.body().value();
        int eofLine = bodyTokens.get(bodyTokens.size() - 1).line();
        int eofCol = bodyTokens.get(bodyTokens.size() - 1).column();

        bodyList.add(new Token(TokenType.EOF, eofLine, eofCol, "<WHILE BODY END>"));
        TokenStorage branchTokenStorage = new TokenStorage(bodyList);
        List<Node> bodyNodes = new Parser(branchTokenStorage).parse(false);

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
        else if (iterableEvalResult.type() == DataType.PATH)
            loopIterable = new DirectoryIterable((File) iterableEvalResult.value());
        else
            throw new SakuraException(token, "For loops can only loop over iterables, directories, or strings..");

        assert loopIterable != null;

        Value curr = loopIterable.next();
        while (curr != null) {
            ExecutionContext tempCtx = new ExecutionContext(ctx);
            tempCtx.defineIdentifier(identifier, curr.setMutability(isAssigneeConst));

            Value braceReturn = getChild(1).evaluate(tempCtx);
            ExecutionResult result = (ExecutionResult) braceReturn.value();
            if (result.earlyReturnType() != EarlyReturnType.NONE) {
                if (result.earlyReturnType() == EarlyReturnType.CONTINUE) {
                    curr = loopIterable.next();
                    continue;
                } else if (result.earlyReturnType() == EarlyReturnType.BREAK)
                    return Value.NULL;
                else
                    parent.stop();
                return braceReturn;
            }

            curr = loopIterable.next();
        }

        return Value.NULL;
    }
}
