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

package net.sakura.interpreter.functions;

import net.sakura.interpreter.SakuraException;
import net.sakura.interpreter.execution.Value;

import java.util.List;

/**
 * A function that terminates script execution immediately.
 */
public final class TerminateFunction implements Function {

    /**
     * Throw an exception that terminates execution.
     */
    @Override
    public Value execute(List<Value> args) {
        String reason = "<unknown reason>";
        if (args.size() > 0)
            reason = args.get(0).value().toString();

        throw new SakuraException("Script execution terminated: " + reason);
    }
}
