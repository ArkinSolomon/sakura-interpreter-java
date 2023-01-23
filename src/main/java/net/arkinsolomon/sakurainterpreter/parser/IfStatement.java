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

import net.arkinsolomon.sakurainterpreter.execution.DataType;
import net.arkinsolomon.sakurainterpreter.execution.ExecutionContext;
import net.arkinsolomon.sakurainterpreter.execution.ExecutionResult;
import net.arkinsolomon.sakurainterpreter.execution.Value;
import net.arkinsolomon.sakurainterpreter.lexer.IfData;
import net.arkinsolomon.sakurainterpreter.lexer.Token;
import net.arkinsolomon.sakurainterpreter.lexer.TokenStorage;
import net.arkinsolomon.sakurainterpreter.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * An if-statement node.
 */
final class IfStatement extends Expression {

    private final List<Node> conditions = new ArrayList<>();

    private final Parser parent;

    /**
     * Create a new if statement from a token.
     *
     * @param token  The token to create the if statement from.
     * @param parent The parser that created this expression.
     */
    public IfStatement(Token token, Parser parent) {
        super(token, 0);
        this.parent = parent;

        IfData data = (IfData) token.value();
        int conditionsLen = data.conditions().size();
        resize(conditionsLen + 1);

        for (List<Token> condition : data.conditions()) {
            TokenStorage ts = new TokenStorage(condition);
            Parser parser = new Parser(ts);
            List<Node> conditions = parser.parse();

            if (conditions.size() != 1)
                throw new RuntimeException("If-statements can only have one condition per branch");

            this.conditions.add(conditions.get(0));
        }

        for (Token branchToken : data.branches()) {
            ArrayList<Token> branchBody = new ArrayList<>();
            branchBody.add(branchToken);

            @SuppressWarnings("unchecked")
            List<Token> branchBodyTokens = (List<Token>) branchToken.value();
            int eofLine = branchBodyTokens.get(branchBodyTokens.size() - 1).line();
            int eofCol = branchBodyTokens.get(branchBodyTokens.size() - 1).column();

            branchBody.add(new Token(TokenType.EOF, eofLine, eofCol, "<BRANCH BODY END>"));
            TokenStorage branchTokenStorage = new TokenStorage(branchBody);
            List<Node> branches = new Parser(branchTokenStorage).parse();

            if (branches.size() != 1)
                throw new RuntimeException("If-statement branches must be wrapped in braces");

            insertChild(branches.get(0));
        }

        // Set the if statement to a no-op
        if (getChild(conditionsLen) == null)
            setChild(conditionsLen, new NoOpExpression());

        if (!isFull())
            throw new RuntimeException("If-statement has more branches than conditions");
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        ExecutionContext tempCtx = new ExecutionContext(ctx);
        parent.resume();

        int i;
        for (i = 0; i < conditions.size(); i++) {
            Value conditionValue = conditions.get(i).evaluate(tempCtx);

            if (conditionValue.type() != DataType.BOOLEAN)
                throw new RuntimeException("If-statement conditions must return booleans");
            if ((boolean) conditionValue.value()) {
                Value braceReturn = getChild(i).evaluate(tempCtx);
                ExecutionResult result = (ExecutionResult) braceReturn.value();
                if (result.earlyReturnType() != EarlyReturnType.NONE) {
                    parent.stop();
                    return braceReturn;
                }
                break;
            }
        }

        if (i == conditions.size()) {
            Value elseReturn = getChild(i).evaluate(tempCtx);
            ExecutionResult elseResult = (ExecutionResult) elseReturn.value();

            // elseResult may be null since it can be a no-op
            if (elseResult != null && elseResult.earlyReturnType() != EarlyReturnType.NONE) {
                parent.stop();
                return elseResult.returnValue();
            }
        }
        return Value.NULL;
    }
}