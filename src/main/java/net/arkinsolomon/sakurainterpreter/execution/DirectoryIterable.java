/*
 * Copyright (c) 2023. Arkin Solomon.
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

package net.arkinsolomon.sakurainterpreter.execution;

import net.arkinsolomon.sakurainterpreter.exceptions.SakuraException;
import net.arkinsolomon.sakurainterpreter.operations.Operation;

import java.io.File;

/**
 * An iterable to loop over the files or directories of a directory.
 */
public class DirectoryIterable implements Iterable {

    private final File[] files;
    private int current = 0;

    /**
     * Create an iterable to loop over each file or files in a directory.
     *
     * @param directory The directory to loop over.
     */
    public DirectoryIterable(File directory) {
        if (directory.isFile())
            throw new SakuraException("The provided path: \"%s\" is not a directory.".formatted(Operation.getFilePathStr(directory)));
        files = directory.listFiles();
    }

    /**
     * Create an iterable using files to loop over.
     *
     * @param files The files to loop over.
     */
    private DirectoryIterable(File[] files) {
        this.files = files;
    }

    @Override
    public Value next() {
        if (current >= files.length)
            return null;

        Value returnVal = new Value(DataType.PATH, files[current], false);
        current++;
        return returnVal.setMutability(false);
    }

    @Override
    public Iterable copy() {
        return new DirectoryIterable(files);
    }
}
