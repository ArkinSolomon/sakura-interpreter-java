/*
 * Copyright (c) 2023 Arkin Solomon.
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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import net.arkinsolomon.sakurainterpreter.exceptions.SakuraException;
import net.arkinsolomon.sakurainterpreter.execution.ExecutionContext;

/**
 * An operation to overwrite the contents of a file with new contents, or to create the file if it doesn't exist.
 */
public final class WriteOperation extends Operation {

    private final Path file;
    private final String newContent;
    private String oldContent = null;

    /**
     * Create a new operation to write {@code content} to {@code file}.
     *
     * @param ctx     The execution context in which to perform the operation.
     * @param file    The file to write the content to.
     * @param content The new content of the file.
     */
    public WriteOperation(ExecutionContext ctx, File file, String content) {
        super(ctx);
        this.file = file.toPath();
        newContent = content;
    }

    @Override
    void perform() {
        if (performed)
            throw new SakuraException("Can not re-perform operation.");

        if (!ctx.getOperationConfig().isValidWritePath(file.toFile()))
            throw new SakuraException("No write permissions for file \"%s\".".formatted(getFilePathStr(file.toFile())));

        if (Files.exists(file)) {

            if (Files.isDirectory(file))
                throw new SakuraException("Can not overwrite the directory \"%s\" with text.".formatted(getFilePathStr(file.toFile())));

            try {
                oldContent = String.join("\n", Files.readAllLines(file));
            } catch (IOException e) {
                throw new SakuraException("Could not read old content from existing file file \"%s\".".formatted(getFilePathStr(file.toFile())), e);
            }
        }

        try {
            Files.write(file, newContent.getBytes(UTF_8), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            String msg;
            if (!Files.exists(file.getParent()))
                msg = "Could not write new content to file \"%s\". The parent directory \"%s\" does not exist.".formatted(getFilePathStr(file.toFile()), getFilePathStr(file.getParent().toFile()));
            else
                msg = "Could not write new content to file \"%s\".".formatted(getFilePathStr(file.toFile()));

            throw new SakuraException(msg, e);
        }

        performed = true;
    }

    @Override
    void undo() {
        if (!performed)
            return;

        try {
            if (oldContent == null)
                Files.delete(file);
            else
                Files.write(file, oldContent.getBytes(UTF_8));
        } catch (IOException e) {
            throw new SakuraException("Could not undo write operation.", e);
        }
    }

    @Override
    public String toString() {
        return "[Write Operation]: Write \"%s\" to \"%s\"".formatted(newContent, getFilePathStr(file.toFile()));
    }
}
