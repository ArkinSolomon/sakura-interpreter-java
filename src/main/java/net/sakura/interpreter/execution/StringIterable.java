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

/**
 * Get the iterable of a string
 */
public class StringIterable implements Iterable {

    private final String str;
    private int currentPos = 0;

    /**
     * Create an iterable from a string.
     */
    public StringIterable(String str) {
        this.str = str;
    }

    @Override
    public Value next() {
        if (currentPos >= str.length())
            return null;

        String returnVal = String.valueOf(str.charAt(currentPos));
        currentPos++;
        return new Value(DataType.STRING, returnVal, false);
    }

    @Override
    public Iterable copy() {
        return new StringIterable(str);
    }
}
