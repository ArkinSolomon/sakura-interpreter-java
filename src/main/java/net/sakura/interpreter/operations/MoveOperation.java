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

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

/**
 * An operation to move a file or directory from one location to another.
 */
public final class MoveOperation extends Operation {

    private final File file;
    private final File target;

    /**
     * Create a new operation to move a folder to another place.
     *
     * @param file   The original (source) file.
     * @param target The destination file to copy to.
     */
    public MoveOperation(File file, File target) {
        this.file = file;
        this.target = target;
    }

    @Override
    void perform() {
        if (performed)
            throw new SakuraException("Can not re-perform an operation.");

        if (!file.exists())
            throw new FileNotFoundException(file);

        try {
            Files.move(file.toPath(), target.toPath(), NOFOLLOW_LINKS);
        } catch (Throwable e) {
            throw new SakuraException("Could not move the file \"%s\" to \"%s\".".formatted(file.getAbsolutePath(), target.getAbsolutePath()), e);
        }

        performed = true;
    }

    @Override
    void undo() {
        if (!performed)
            return;

        try {
            Files.move(target.toPath(), file.toPath(), NOFOLLOW_LINKS);
        } catch (Throwable e) {
            throw new SakuraException("Could not undo a move operation which moved \"%s\" to \"%s\".".formatted(file.getAbsolutePath(), target.getAbsolutePath()), e);
        }
    }

    @Override
    public String toString() {
        return "[Move Operation]: Move \"%s\" to \"%s\".".formatted(file.getAbsolutePath(), target.getAbsolutePath());
    }
}
