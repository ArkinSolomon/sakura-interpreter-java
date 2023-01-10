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

package net.sakura.interpreter.operations;

import net.sakura.interpreter.exceptions.SakuraException;

import java.io.File;
import java.nio.file.Files;

/**
 * Configuration options for file operations.
 */
public final class OperationConfig {

    private static File tmpDir;

    /**
     * Initialize the operation config.
     */
    public static void init() {
        try {
            tmpDir = Files.createTempDirectory("sakura").toFile();
        }
        catch (Throwable e){
            throw new SakuraException("Could not initialize operation configuration.", e);
        }
    }

    /**
     * Get the temporary directory for Sakura. Exception thrown if {@link OperationConfig#init()} has not been called (if tmpDir is null).
     *
     * @return The location of the temporary directory.
     */
    public static File getTmpDir() {
        if (tmpDir == null)
            throw new SakuraException("Temporary directory is null.");
        return tmpDir;
    }
}
