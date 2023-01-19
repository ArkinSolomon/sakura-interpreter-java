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
import net.sakura.interpreter.execution.ExecutionContext;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Files;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

/**
 * This operation copies a file from one location to another.
 */
public final class CopyOperation extends Operation {

    private final File file;
    private final File target;

    /**
     * Create a new operation to copy a file to another location.
     *
     * @param ctx The execution context in which to run this operation.
     * @param file   The file to be copied.
     * @param target The destination for it to be copied to.
     */
    public CopyOperation(ExecutionContext ctx, File file, File target) {
        super(ctx);
        this.file = file;
        this.target = target;
    }

    @Override
    void perform() {
        if (performed)
            throw new SakuraException("Can not re-perform operation.");

        if (target.exists())
            ctx.getFileTracker().runOperation(new DeleteOperation(ctx, target));

        if (!ctx.getOperationConfig().isValidWritePath(target) || !ctx.getOperationConfig().isValidReadPath(file))
            throw new SakuraException("Insufficient permissions to copy \"%s\" to \"%s\".".formatted(getFilePathStr(file), getFilePathStr(target)));

        try {
            if (file.isDirectory())
                FileUtils.copyDirectory(file, target);
            else
                Files.copy(file.toPath(), target.toPath(), NOFOLLOW_LINKS);
            performed = true;
        } catch (Throwable e) {
            throw new SakuraException("Error copying file \"%s\" to \"%s\".".formatted(getFilePathStr(file), getFilePathStr(target)), e);
        }
    }

    @Override
    void undo() {
        if (!performed)
            return;

        try {
            if (target.isDirectory())
                FileUtils.deleteDirectory(target);
            else if (!target.delete())
                throw new SakuraException("Could not undo operation by deleting target file at " + target);

        } catch (Exception e) {
            throw new SakuraException("Could not undo operation by deleting target file at " + target);
        }
    }

    @Override
    public String toString() {
        return "[Copy Operation]: Copying \"%s\" to \"%s\"".formatted(getFilePathStr(file), getFilePathStr(target));
    }
}
