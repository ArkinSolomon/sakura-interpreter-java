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

package net.arkinsolomon.sakurainterpreter.operations;

import net.arkinsolomon.sakurainterpreter.exceptions.SakuraException;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

/**
 * Configuration options for file operations.
 */
public final class OperationConfig {

    private Set<File> allowRead = new HashSet<>();
    private Set<File> disallowRead = new HashSet<>();
    private Set<File> allowWrite = new HashSet<>();
    private Set<File> disallowWrite = new HashSet<>();
    private final File tmpDir;

    /**
     * Initialize the operation config.
     */
    public OperationConfig() {
        try {
            tmpDir = Files.createTempDirectory("sakura-").toFile();

            // Try to delete on JVM exit if possible
            tmpDir.deleteOnExit();
        } catch (Throwable e) {
            throw new SakuraException("Could not initialize operation configuration.", e);
        }
    }

    /**
     * Update file and directory restrictions.
     *
     * @param allowRead The files and directories to allow read operations on.
     * @param disallowRead The files and directories to disallow read operations on.
     * @param allowWrite The files and directories to allow write operations on.
     * @param disallowWrite The files and directories to disallow write operations on.
     */
    public void updateRestrictions(Set<File> allowRead, Set<File> disallowRead, Set<File> allowWrite, Set<File> disallowWrite) {
        this.allowRead = allowRead;
        this.disallowRead = disallowRead;
        this.allowWrite = allowWrite;
        this.disallowWrite = disallowWrite;
    }

    /**
     * Check if {@code testFile} is a child of {@code directory}.
     *
     * @param testFile  The file to check for.
     * @param directory The directory to determine if it is an ancestor.
     * @return True if {@code directory} is an ancestor of {@code testFile}.
     */
    private static boolean isWithinDirectory(File testFile, File directory) {
        return Operation.getFilePathStr(testFile).startsWith(Operation.getFilePathStr(directory) + File.separator);
    }

    /**
     * Get the temporary directory for files.
     *
     * @return The location of the temporary directory.
     */
    public File getTmpDir() {
        if (tmpDir == null)
            throw new SakuraException("Temporary directory is null.");
        return tmpDir;
    }

    /**
     * Determine if write operations can be performed on a file.
     *
     * @param file The file to determine if write operations can be performed.
     * @return True if write operations can be performed on {@code file}.
     */
    public boolean isValidWritePath(File file) {
        if (file.exists() && !file.canWrite())
            return false;

        if (allowWrite.isEmpty() && disallowWrite.isEmpty())
            return true;


        for (File writeable : allowWrite) {
            if (!isWithinDirectory(file, writeable))
                return false;
        }

        for (File nonWriteable : disallowWrite) {
            if (isWithinDirectory(file, nonWriteable))
                return false;
        }

        return true;
    }

    /**
     * Determine if read operations can be performed on a file.
     *
     * @param file The file to determine if read operations can be performed.
     * @return True if read operations can be performed on {@code file}.
     */
    public boolean isValidReadPath(File file) {
        if (file.exists() && !file.canRead())
            return false;


        if (allowRead.isEmpty() && disallowRead.isEmpty())
            return true;


        for (File readable : allowRead) {
            if (!isWithinDirectory(file, readable))
                return false;
        }

        for (File nonReadable : disallowRead) {
            if (isWithinDirectory(file, nonReadable))
                return false;
        }

        return false;
    }
}
