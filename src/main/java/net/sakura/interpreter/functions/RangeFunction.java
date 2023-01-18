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

import net.sakura.interpreter.exceptions.SakuraException;
import net.sakura.interpreter.execution.DataType;
import net.sakura.interpreter.execution.Value;

import java.util.List;

/**
 * A range function to loop over a range of values.
 */
public final class RangeFunction implements Function {

    /**
     * Execute a function.
     *
     * @param args The argument values.
     * @return The result of the function.
     */
    @Override
    public Value execute(List<Value> args) {
        if (args.size() == 0)
            throw new SakuraException("Range function requires at least one argument.");

        Integer[] numbers = args
                .stream()
                .limit(3)
                .map(v -> {
                    if (v == null || v.type() != DataType.NUMBER)
                        throw new SakuraException("All arguments of range must be a number.");

                    return (int) Math.floor((double) v.value());
                })
                .toArray(Integer[]::new);

        RangeIterable i;
        if (args.size() == 1)
            i = new RangeIterable(0, numbers[0], 1);
        else if (args.size() == 2)
            i = new RangeIterable(numbers[0], numbers[1], 1);
        else
            i = new RangeIterable(numbers[0], numbers[1], numbers[2]);

        return new Value(DataType.ITERABLE, i, false);
    }
}
