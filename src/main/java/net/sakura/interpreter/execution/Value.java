/*
 * Copyright (c) 2022-2023. Sakura Contributors.
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
 * A single value, consisting of its type and actual value.
 *
 * @param type The type of the variable.
 * @param value The value of the variable.
 * @param isMutable True if the variable is mutable.
 */
public record Value(DataType type, Object value, boolean isMutable) {
    public static final Value NULL = new Value(DataType.NULL, null, false);

    /**
     * Get the string representation of the data of this value.
     *
     * @return The string representation of the data of this value.
     */
    @Override
    public String toString() {
        if (value == null)
            return "NULL";
        return value.toString();
    }
}