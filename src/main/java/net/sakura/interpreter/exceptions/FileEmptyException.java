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

package net.sakura.interpreter.exceptions;

/**
 * An exception thrown when a Sakura script is empty.
 */
public final class FileEmptyException extends SakuraException {

    /**
     * Create a new script saying that you can not execute empty files.
     *
     * @param fileName The name of the file (or path) that was empty. Null if a file wasn't executed, and it was just some text
     */
    public FileEmptyException(String fileName) {
        super("Can not execute empty %s".formatted(fileName == null ? "script." : "file: \"%s\".".formatted(fileName)));
    }
}
