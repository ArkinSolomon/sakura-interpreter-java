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

import net.arkinsolomon.sakurainterpreter.exceptions.SakuraException;
import net.arkinsolomon.sakurainterpreter.execution.DataType;
import net.arkinsolomon.sakurainterpreter.execution.Value;

import java.util.List;

/**
 * Function to get string representation of a value.
 */
public final class StrFunction implements Function {

    @Override
    public Value execute(List<Value> args) {
        if (args.size() == 0)
            throw new SakuraException("Str function requires at least one argument");

        return new Value(DataType.STRING, args.get(0).toString(), false);
    }
}
