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

package net.sakura.interpreter.execution;

import java.util.List;

/**
 * An iterable from a list.
 */
public class ListIterable implements Iterable{

    private List<Value> values;
    private int current = 0;

    /**
     * Create a new iterable with the values from the list.
     *
     * @param values The values to store in the iterable.
     */
    public ListIterable(List<Value> values) {
        this.values = values;
    }

    @Override
    public Value next() {
        if (current >= values.size())
            return null;

        Value returnVal = values.get(current);
        current++;
        return returnVal.setMutability(false);
    }
}
