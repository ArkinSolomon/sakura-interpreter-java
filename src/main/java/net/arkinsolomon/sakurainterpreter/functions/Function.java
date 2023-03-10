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
 * This interface provides a basis for functions to execute. Note that this interface should not be implemented by executors, and is for internal use only. Access to the execution context is not supported. Extend the abstract class {@link CustomFunction} instead for executors.
 */
public interface Function {

    /**
     * Execute a function.
     *
     * @param args The argument values.
     * @param ctx The execution context of the function.
     * @return The result of the function.
     */
    Value execute(List<Value> args, ExecutionContext ctx);
}
