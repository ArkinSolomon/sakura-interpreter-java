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

import net.arkinsolomon.sakurainterpreter.exceptions.SakuraException;
import net.arkinsolomon.sakurainterpreter.execution.ExecutionContext;

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
     * @param ctx The execution context in which to run this operation.
     * @param originalFile The original file or directory.
     * @param newName      The new name of the file or directory.
     */
    public RenameOperation(ExecutionContext ctx, File originalFile, String newName) {
        super(ctx);
        this.originalFile = originalFile;
        newFile = originalFile.toPath().resolveSibling(newName);

        if (Files.exists(newFile))
            throw new SakuraException("File exists: " + newFile.toFile());
    }

    @Override
    void perform() {
        if (performed)
            throw new SakuraException("Can not re-perform operation.");

        // Even though newFile *should* be in the same directory, just in case we'll check anyway (the error will only show the original, however)
        if (!ctx.getOperationConfig().isValidWritePath(originalFile) || !ctx.getOperationConfig().isValidReadPath(newFile.toFile()))
            throw new SakuraException("No write permission for file \"%s\".".formatted(Operation.getFilePathStr(originalFile)));

        try {
            Files.move(originalFile.toPath(), newFile);
            performed = true;
        } catch (Throwable e) {
            throw new SakuraException("Could not rename file \"%s\" to \"%s\".".formatted(originalFile, newFile), e);
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
        return "[Rename Operation]: Rename \"%s\" to \"%s\"".formatted(getFilePathStr(originalFile), getFilePathStr(newFile.toFile()));
    }
}
