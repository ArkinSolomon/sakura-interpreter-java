/*
 * Copyright (c) 2022. XPkg-Client Contributors.
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

package net.sakura.interpreter.operations;

/**
 * An instance of this class is an operation done on the file system which can be undone.
 */
public abstract class Operation {

    protected boolean performed = false;

    /**
     * Perform the operation.
     */
    public abstract void perform();

    /**
     * Undo the operation.
     */
    public abstract void undo();

    /**
     * Force overriding for operations.
     *
     * @return The string representation of the operation.
     */
    @Override
    public abstract String toString();
}
