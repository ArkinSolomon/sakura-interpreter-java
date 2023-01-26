/*
 * Copyright (c) 2022-2023. Arkin Solomon.
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

package net.arkinsolomon.sakurainterpreter.operations;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * An instance of this class tracks all system changes.
 */
public final class FileTracker {

    private final Deque<Operation> operations = new ArrayDeque<>();

    /**
     * Run an operation, should throw an exception if it fails.
     *
     * @param operation The operation to add and run.
     */
    public void runOperation(Operation operation) {
        operation.perform();
        operations.addFirst(operation);
    }

    /**
     * Undo all operations.
     */
    public void undoOperations() {
        while (!operations.isEmpty()){
            Operation operation = operations.removeFirst();
            operation.undo();
        }
    }
}
