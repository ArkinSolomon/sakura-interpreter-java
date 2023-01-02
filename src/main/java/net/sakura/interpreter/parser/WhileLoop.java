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
import net.sakura.interpreter.execution.Value;
import net.sakura.interpreter.lexer.Token;
import net.sakura.interpreter.lexer.TokenStorage;
import net.sakura.interpreter.lexer.TokenType;
import net.sakura.interpreter.lexer.WhileData;

import java.util.ArrayList;
import java.util.List;

/**
 * An expression for a while loop.
 */
public class WhileLoop extends Expression{

    private final Parser parent;

    /**
     * Create a new while loop from a token.
     *
     * @param token The token to create the if statement from.
     * @param parent The parser that created this expression.
     */
    public WhileLoop(Token token, Parser parent) {
        super(token, 2);
        this.parent = parent;

        WhileData data = (WhileData) token.value();

        TokenStorage conditionTS = new TokenStorage(data.condition());
        Parser conditionParser = new Parser(conditionTS);
        List<Node> parsedConditions = conditionParser.parse();
        if (parsedConditions.size() != 1)
            throw new RuntimeException("While loop conditions can only be one statement");
        setChild(0, parsedConditions.get(0));

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
        while (evalCondition(ctx)) {
            ExecutionContext tempCtx = new ExecutionContext(ctx);

            Value braceReturn = getChild(1).evaluate(tempCtx);
            ExecutionResult result = (ExecutionResult) braceReturn.value();
            if (result.earlyReturn()){
                parent.stop();
                return result.returnValue();
            }
        }
        return Value.NULL;
    }

    /**
     * Compute the value of the while loop condition. {@link RuntimeException} is thrown if the condition evaluation is not a boolean.
     *
     * @return True if the loop should continue, or false if it should not.
     */
    private boolean evalCondition(ExecutionContext ctx) {
        Value result = getChild(0).evaluate(ctx);
        if (result.type() != DataType.BOOLEAN)
            throw new RuntimeException("While loop condition must be a boolean");
        return (boolean) result.value();
    }
}
