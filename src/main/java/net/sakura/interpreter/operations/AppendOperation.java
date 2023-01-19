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
import net.sakura.interpreter.execution.ExecutionContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * An operation to append text to a file.
 */
public final class AppendOperation extends Operation {

    private final Path path;
    private final String appendContent;
    private String oldContent;

    /**
     * Create a new operation to append some text to the end of a file.
     *
     * @param ctx           The execution context in which to perform the operation.
     * @param file          The file to which to append to.
     * @param appendContent The content to append to the end of the file.
     */
    public AppendOperation(ExecutionContext ctx, File file, String appendContent) {
        super(ctx);
        path = file.toPath();
        this.appendContent = appendContent;
    }

    @Override
    void perform() {
        if (performed)
            throw new SakuraException("Can not re-perform operation.");

        if (!Files.exists(path))
            throw new FileNotFoundException(path.toFile());

        if (!ctx.getOperationConfig().isValidWritePath(path.toFile()))
            throw new SakuraException("No write permissions for file \"%s\".".formatted(getFilePathStr(path.toFile())));

        try {
            oldContent = String.join("\n", Files.readAllLines(path));
        } catch (IOException e) {
            throw new SakuraException("Could not read file \"%s\".".formatted(getFilePathStr(path.toFile())), e);
        }

        try {
            Files.write(path, appendContent.getBytes(), StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new SakuraException("Could not append to file \"%s\".".formatted(getFilePathStr(path.toFile())), e);
        }

        performed = true;
    }

    @Override
    void undo() {
        if (!performed)
            return;

        try {
            Files.write(path, oldContent.getBytes());
        } catch (Exception e) {
            throw new SakuraException("Could not undo operation by writing old content to \"%s\"".formatted(getFilePathStr(path.toFile())));
        }
    }

    @Override
    public String toString() {
        return "[Append Operation]: Append \"%s\" to \"%s\".".formatted(appendContent, getFilePathStr(path.toFile()));
    }
}