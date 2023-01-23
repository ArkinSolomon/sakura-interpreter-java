/*
 * Copyright (c) 2022-2023. Sakura Interpreter Java Contributors.
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

import net.arkinsolomon.sakurainterpreter.execution.ExecutionContext;

import java.io.File;
import java.io.IOException;

/**
 * An instance of this class is an operation done on the file system which can be undone.
 */
public abstract class Operation {

    protected boolean performed = false;
    protected final ExecutionContext ctx;

    Operation(ExecutionContext ctx){
        this.ctx = ctx;
    }

    /**
     * Perform the operation.
     */
    abstract void perform();

    /**
     * Undo the operation.
     */
    abstract void undo();

    /**
     * Force overriding for operations.
     *
     * @return The string representation of the operation.
     */
    @Override
    public abstract String toString();

    /**
     * Get the string representation of a path to a file.
     *
     * @param file The file to get the string representation of the path.
     * @return The string representation of a path to a file.
     */
    public static String getFilePathStr(File file) {
        try{
            return file.getCanonicalPath();
        } catch (IOException e) {
            return file.getAbsolutePath();
        }
    }
}
