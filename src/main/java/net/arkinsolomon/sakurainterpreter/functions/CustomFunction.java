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

package net.arkinsolomon.sakurainterpreter.functions;

import net.arkinsolomon.sakurainterpreter.execution.ExecutionContext;
import net.arkinsolomon.sakurainterpreter.execution.Value;

import java.util.List;

/**
 * This class should be extended by the executor in order to add a custom function to the interpreter.
 */
public abstract class CustomFunction implements Function {

    /**
     * Hide execution context from executor.
     *
     * @param args The values of the arguments passed to the function.
     * @return The return value of the function, or {@link Value#NULL} if there is none.
     */
    public abstract Value execute(List<Value> args);

    @Override
    public final Value execute(List<Value> args, ExecutionContext ctx) {
        return execute(args);
    }
}
