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

import net.sakura.interpreter.execution.DataType;
import net.sakura.interpreter.execution.ListIterable;
import net.sakura.interpreter.execution.Value;

import java.util.List;

public final class ListFunction implements Function{

    @Override
    public Value execute(List<Value> args) {
        return new Value(DataType.ITERABLE, new ListIterable(args), false);
    }
}
