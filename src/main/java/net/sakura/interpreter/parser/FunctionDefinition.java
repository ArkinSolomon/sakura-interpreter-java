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
import net.sakura.interpreter.execution.ListIterable;
import net.sakura.interpreter.execution.Value;
import net.sakura.interpreter.functions.Function;
import net.sakura.interpreter.lexer.FunctionArgData;
import net.sakura.interpreter.lexer.FunctionDefinitionData;
import net.sakura.interpreter.lexer.Token;
import net.sakura.interpreter.lexer.TokenStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * A function definition node.
 */
final class FunctionDefinition extends Expression implements Function {

    private final FunctionDefinitionData data;
    private final Parser parsedFunc;
    private final List<Node> defaultArgExpressions = new ArrayList<>();
    private ExecutionContext rootCtx;

    private boolean hasRest;
    private boolean isRestConst;

    /**
     * Create a new function definition.
     *
     * @param token The token for the function definition.
     */
    public FunctionDefinition(Token token) {
        super(token, 0);
        data = (FunctionDefinitionData) token.value();

        @SuppressWarnings("unchecked")
        TokenStorage body = new TokenStorage((List<Token>) data.body().value());
        parsedFunc = new Parser(body);
        parsedFunc.parse();

        hasRest = false;
        for (FunctionArgData argData : data.args()) {
            if (argData.isRest()) {
                hasRest = true;
                isRestConst = argData.isConstant();
                break;
            } else if (!argData.hasDefault()) {
                defaultArgExpressions.add(null);
                continue;
            }

            TokenStorage ts = new TokenStorage(argData.defaultValue());
            Parser argParser = new Parser(ts);
            List<Node> argExpr = argParser.parse();

            if (argExpr.size() == 0)
                throw new RuntimeException("Default argument expression not provided");
            else if (argExpr.size() > 1)
                throw new RuntimeException("Default arguments can not be more than one expression");

            defaultArgExpressions.add(argExpr.get(0));
        }
    }

    @Override
    public Value evaluate(ExecutionContext ctx) {
        throw new RuntimeException("Can not evaluate function definition");
    }

    /**
     * Register this function with the given context.
     *
     * @param ctx The context in which to register this function.
     */
    public void register(ExecutionContext ctx) {
        rootCtx = ctx;

        if (ctx.hasIdentifier(data.identifier()))
            throw new RuntimeException("Function already exists");
        ctx.registerFunc(data.identifier(), this);
    }

    @Override
    public Value execute(List<Value> args) {
        ExecutionContext tempCtx = new ExecutionContext(rootCtx);

        @SuppressWarnings("ConstantConditions")
        List<Value> argValues = new ArrayList<>(defaultArgExpressions
                .stream()
                .map(node -> node == null ? null : node.evaluate(rootCtx))
                .toList());

        if (!hasRest) {

            // Pick the default value if no argument is provided
            int minLen = Math.min(argValues.size(), args.size());
            for (int i = 0; i < minLen; i++)
                argValues.set(i, args.get(i));
        } else {
            for (int i = 0; i < argValues.size(); i++)
                argValues.set(i, args.get(i));
            argValues.add(new Value(DataType.ITERABLE, new ListIterable(args.subList(argValues.size(), args.size())), !isRestConst));
        }

        // Assign variables to the temporary context
        for (int i = 0; i < argValues.size(); i++) {
            FunctionArgData argData = data.args().get(i);
            String argId = argData.identifier();
            Value val = argValues.get(i);

            tempCtx.defineIdentifier(argId, val == null ? Value.NULL : new Value(val.type(), val.value(), val.isMutable()));
        }

        return parsedFunc.execute(tempCtx).returnValue();
    }
}
