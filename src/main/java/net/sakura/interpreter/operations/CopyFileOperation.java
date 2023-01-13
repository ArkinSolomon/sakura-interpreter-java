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

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

/**
 * This operation copies a file from one location to another.
 */
public final class CopyFileOperation extends Operation {

    private final File file;
    private final File target;

    /**
     * Create a new operation to copy a file to another location.
     *
     * @param file   The file to be copied.
     * @param target The destination for it to be copied to.
     */
    public CopyFileOperation(File file, File target) {
        super();
        this.file = file;
        this.target = target;
    }

    @Override
    void perform() {
        if (performed)
            throw new SakuraException("Can not re-perform operation.");

        try {
            if (file.isDirectory())
                FileUtils.copyDirectory(file, target);
            else
                Files.copy(file.toPath(), target.toPath(), NOFOLLOW_LINKS);
            performed = true;
        }catch (Throwable e){
            throw new SakuraException("Error copying file \"%s\" to \"%s\".".formatted(file.getAbsolutePath(), target.getAbsolutePath()), e);
        }
    }

    @Override
    void undo()  {
        if (!performed)
            return;

        try {
            if (target.isDirectory())
                FileUtils.deleteDirectory(target);
            else if (!target.delete())
                throw new SakuraException("Could not undo operation by deleting target file at " + target);

        }catch (Exception e){
            throw new SakuraException("Could not undo operation by deleting target file at " + target);
        }
    }

    @Override
    public String toString() {
        return "[Copy Operation]: Copying \"%s\" to \"%s\"".formatted(file.getAbsolutePath(), target.getAbsolutePath());
    }
}
