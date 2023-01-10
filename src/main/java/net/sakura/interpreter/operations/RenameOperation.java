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

import net.sakura.interpreter.exceptions.SakuraException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This operation renames a file or directory.
 */
public final class RenameOperation extends Operation {

    private final File originalFile;
    private final Path newFile;

    /**
     * Create a new operation to rename a file or directory at the location to the new name.
     *
     * @param originalFile The original file path.
     * @param newName      The new name of the file.
     */
    public RenameOperation(File originalFile, String newName) {
        this.originalFile = originalFile;
        newFile = originalFile.toPath().resolveSibling(newName);

        if (Files.exists(newFile))
            throw new SakuraException("File exists: " + newFile.toFile());
    }

    /**
     * Get the path to the new file which the original file was renamed to.
     *
     * @return The path to the renamed file.
     */
    public Path getNewFile() {
        return newFile;
    }

    @Override
    void perform() {
        if (performed)
            throw new SakuraException("Can not re-perform operation.");

        try {
            Files.move(originalFile.toPath(), newFile);
            performed = true;
        } catch (Throwable e) {
            throw new SakuraException("Could not rename file ", e);
        }
    }

    @Override
    void undo() {
        if (!performed)
            return;

        try {
            Files.move(newFile, originalFile.toPath());
        } catch (Throwable e) {
            throw new SakuraException("Could not undo file renaming");
        }
    }

    @Override
    public String toString() {
        return "[Rename Operation]: \"%s\" to \"%s\"".formatted(originalFile.getAbsolutePath(), newFile.toFile().getAbsolutePath());
    }
}
