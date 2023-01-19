/*
 * Copyright (c) 2022-2023. Sakura Contributors.
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

import net.sakura.interpreter.exceptions.FileExistsException;
import net.sakura.interpreter.exceptions.SakuraException;
import net.sakura.interpreter.execution.ExecutionContext;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Create a directory (or directories).
 */
public final class MkdirOperation extends Operation {

    private final File directory;
    private final boolean recursive;
    private Path lastNonExistentParent;

    /**
     * Create a new operation to create a directory at a specified place. Assume the directory intending to be created does not exist.
     *
     * @param ctx The execution context in which to run the operation.
     * @param directory The directory to create.
     * @param recursive True if parent directories should be created as well.
     */
    public MkdirOperation(ExecutionContext ctx, File directory, boolean recursive) {
        super(ctx);
        this.directory = directory;
        this.recursive = recursive;
    }

    @Override
    void perform() {
        if (performed)
            throw new SakuraException("Can not re-perform operation.");

        if (!ctx.getOperationConfig().isValidWritePath(directory))
            throw new SakuraException("No write permissions for file \"%s\".".formatted(getFilePathStr(directory)));

        lastNonExistentParent = directory.toPath();
        try {
            if (recursive) {

                // Find the first directory that is created (so that we can delete it on undo)
                Path lastExistingParent = directory.toPath();
                while (!Files.exists(lastExistingParent)) {
                    lastNonExistentParent = lastExistingParent;
                    lastExistingParent = lastExistingParent.getParent();
                }

                if (lastExistingParent.equals(directory.toPath()))
                    throw new FileExistsException(directory);

                Files.createDirectories(directory.toPath());
            } else
                Files.createDirectory(directory.toPath());

            performed = true;
        }catch (SakuraException e){
            throw e;
        }catch (FileAlreadyExistsException e){
            throw new FileExistsException(directory, e);
        } catch (Throwable e) {
            throw new SakuraException("There was an unknown error creating the directory \"%s\".".formatted(getFilePathStr(directory)), e);
        }
    }

    @Override
    void undo() {
        if (!performed)
            return;
        try {
            FileUtils.deleteDirectory(lastNonExistentParent.toFile());
        } catch (Throwable e) {
            throw new SakuraException("Could not delete directory.", e);
        }
    }

    @Override
    public String toString() {
        return "[Mkdir Operation]: Creating directory at \"%s\"".formatted(getFilePathStr(directory)) + (recursive ? " recursively" : "");
    }
}
