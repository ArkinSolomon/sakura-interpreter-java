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

/**
 * A value that can be iterated over in a for loop.
 */
public interface Iterable {

    /**
     * Get the next value of the iterable, or null if the next value does not exist.
     *
     * @return The next value of the iterable.
     */
    Value next();

    /**
     * Copy the iterable to reset all pointers.
     */
    Iterable copy();
}
