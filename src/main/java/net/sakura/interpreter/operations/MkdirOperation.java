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
import org.apache.commons.io.FileUtils;

import java.io.File;
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
     * @param directory The directory to create.
     * @param recursive True if parent directories should be created as well.
     */
    public MkdirOperation(File directory, boolean recursive) {
        super();
        this.directory = directory;
        this.recursive = recursive;
    }

    @Override
    public void perform() {
        if (performed)
            throw new SakuraException("Can not re-perform operation.");

        lastNonExistentParent = directory.toPath();
        try {
            if (recursive) {

                // Find the first directory that is created
                Path lastExistingParent = new File(directory.getParent()).toPath();
                while (!Files.exists(lastExistingParent)) {
                    lastNonExistentParent = lastExistingParent;
                    lastExistingParent = lastExistingParent.getParent();
                }

                Files.createDirectories(directory.toPath());
            } else
                Files.createDirectory(directory.toPath());

            performed = true;
        } catch (Throwable e) {
            throw new SakuraException("There was an error creating a directory.", e);
        }
    }

    @Override
    public void undo() {
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
        return "[Mkdir Operation]: Creating directory at \"%s\"".formatted(directory.getAbsolutePath()) + (recursive ? " recursively" : "");
    }
}
