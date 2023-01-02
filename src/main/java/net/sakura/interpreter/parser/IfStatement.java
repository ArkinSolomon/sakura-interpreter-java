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
import net.sakura.interpreter.lexer.IfData;
import net.sakura.interpreter.lexer.Token;
import net.sakura.interpreter.lexer.TokenStorage;
import net.sakura.interpreter.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * An if-statement node.
 */
public class IfStatement extends Expression {

    private final List<Node> conditions = new ArrayList<>();

    private final Parser parent;

    /**
     * Create a new if statement from a token.
     *
     * @param token The token to create the if statement from.
     * @param parent The parser that created this expression.
     */
    public IfStatement(Token token, Parser parent) {
        super(token, 0);
        this.parent = parent;

        IfData data = (IfData) token.value();
        int conditionsLen= data.conditions().size();
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
            int eofPos = branchBodyTokens.get(branchBodyTokens.size() - 1).position();

            branchBody.add(new Token(TokenType.EOF, eofPos, "<BRANCH BODY END>"));
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

        int i;
        for (i = 0; i < conditions.size(); i++) {
            Value conditionValue = conditions.get(i).evaluate(tempCtx);

            if (conditionValue.type() != DataType.BOOLEAN)
                throw new RuntimeException("If-statement conditions must return booleans");
            if ((boolean) conditionValue.value()) {
                Value braceReturn = getChild(i).evaluate(tempCtx);
                ExecutionResult result = (ExecutionResult) braceReturn.value();
                if (result.earlyReturn()){
                    parent.stop();
                    return result.returnValue();
                }
                break;
            }
        }
        if (i  == conditions.size()){
            Value elseReturn = getChild(i).evaluate(tempCtx);

            if (elseReturn.type() == DataType.__BRACE_RETURN && ((ExecutionResult) elseReturn.value()).earlyReturn()){
                parent.stop();
                return ((ExecutionResult) elseReturn.value()).returnValue();
            }
        }
        return Value.NULL;
    }
}
