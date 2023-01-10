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

import net.sakura.interpreter.exceptions.FileNotFoundException;
import net.sakura.interpreter.exceptions.SakuraException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.UUID;

/**
 * A simple operation to delete a file or folder. Note: deleting just moves it to a temporary file, so it can be undone if needed.
 */
public final class DeleteOperation extends Operation {

    private final Path fileToDelete;
    private Path deletedFile;

    /**
     * Create a new operation to delete the file.
     *
     * @param fileToDelete The file to delete.
     */
    public DeleteOperation(File fileToDelete) {
        this.fileToDelete = fileToDelete.toPath();
    }

    @Override
    public void perform() {
        if (performed)
            throw new SakuraException("Can not re-perform operations.");

        try {
            deletedFile = new File(OperationConfig.getTmpDir(), UUID.randomUUID().toString()).toPath();
            Files.move(fileToDelete, deletedFile);
            performed = true;
        } catch (NoSuchFileException e) {
            throw new FileNotFoundException(fileToDelete.toFile(), e);
        } catch (Throwable e) {
            throw new SakuraException("An unknown exception occurred while deleting the file \"%s\"".formatted(fileToDelete), e);
        }
    }

    @Override
    public void undo() {
        if (!performed)
            return;

        try {
            Files.move(deletedFile, fileToDelete);
        } catch (Throwable e) {
            throw new SakuraException("Could not undo file deletion", e);
        }
    }

    @Override
    public String toString() {
        return "[Delete Operation]: Deleting \"%s\" by moving it to \"%s\"".formatted(fileToDelete.toFile().getAbsolutePath(), deletedFile.toFile().getAbsolutePath());
    }
}
