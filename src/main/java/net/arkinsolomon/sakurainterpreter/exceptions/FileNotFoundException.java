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
 * An exception thrown when attempting an invalid operation on a non-existent file or directory.
 */
public final class FileNotFoundException extends SakuraException {

    /**
     * Create a new exception saying that the file was not found.
     *
     * @param file The file that was not found.
     */
    public FileNotFoundException(File file) {
        this(file, null);
    }

    /**
     * Create an exception saying that the file was not found, and that this exception was caused by another one.
     *
     * @param file The file that was not found.
     * @param cause The exception that caused this one.
     */
    public FileNotFoundException(File file, Throwable cause){
        super("The file or directory at \"%s\" does not exist.".formatted(Operation.getFilePathStr(file)), cause);
    }
}
