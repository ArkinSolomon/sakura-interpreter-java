/*
 * Copyright (c) 2023. Arkin Solomon.
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

package net.arkinsolomon.sakurainterpreter.exceptions;

import net.arkinsolomon.sakurainterpreter.operations.Operation;

import java.io.File;

/**
 * An exception thrown when attempting to perform an operation invalidly on a file or directory that already exists.
 */
public final class FileExistsException extends SakuraException{

    /**
     * Create a new exception saying that a file already exists.
     *
     * @param file The file that already exists.
     */
    public FileExistsException(File file) {
        this(file, null);
    }

    /**
     * Create a new exception saying that a file already exists, and that another exception caused this one.
     *
     * @param file The file that already exists.
     * @param cause The exception that caused this one.
     */
    public FileExistsException(File file, Throwable cause){
        super("The file or directory \"%s\" already exists.".formatted(Operation.getFilePathStr(file)), cause);
    }
}
