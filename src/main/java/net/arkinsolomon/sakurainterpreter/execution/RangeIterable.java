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

package net.arkinsolomon.sakurainterpreter.execution;

import net.arkinsolomon.sakurainterpreter.exceptions.SakuraException;

/**
 * An iterator for a certain range.
 */
public final class RangeIterable implements Iterable {

    private final int start;
    private final int end;
    private final int step;
    private int current;

    /**
     * Create a new iterator to go from {@code start} to {@code end}, in steps of {@code step}.
     *
     * @param start The starting value of the iterator.
     * @param end   The ending value of the iterator.
     * @param step  The step value of the iterator.
     */
    public RangeIterable(int start, int end, int step) {
        this.start = start;
        this.end = end;
        this.step = step;
        current = start - step;

        if (step == 0)
            throw new SakuraException("Range function step can not be zero");
    }

    @Override
    public Value next() {
        current += step;
        if (current >= end)
            return null;
        return new Value(DataType.NUMBER, (double) current, false);
    }

    @Override
    public Iterable copy() {
        return new RangeIterable(start, end, step);
    }
}
