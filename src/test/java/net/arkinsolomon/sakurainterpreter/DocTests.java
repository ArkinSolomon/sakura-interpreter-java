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

package net.arkinsolomon.sakurainterpreter;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for most examples within the language documentation.
 */
public final class DocTests extends SakuraTestBase {

    @Test
    void testHomeGettingStarted() throws IOException {
        File some = new File(testRoot, "some");
        File someOther = new File(some, "other");
        File someOtherDirectory = new File(someOther, "directory");
        File someDirectory = new File(some, "directory");
        File writtenFile = new File(someOtherDirectory, "a_file.txt");

        Files.createDirectories(someDirectory.toPath());

        Path path = getResource("doc-examples/home-getting-started.ska");
        interpreter.executeFile(path);

        assertFalse(someDirectory.exists());
        assertTrue(someOtherDirectory.exists());
        assertTrue(writtenFile.exists());

        String content = Files.readString(writtenFile.toPath(), StandardCharsets.UTF_8);
        assertEquals("Hello World!", content);
    }

    @Test
    void testCommandsPathStore() throws IOException {
        File some = new File(testRoot, "some");
        File someFile = new File(some, "file");

        Files.createDirectory(some.toPath());

        Path path = getResource("doc-examples/commands-path-store.ska");
        interpreter.executeFile(path);

        assertTrue(someFile.exists());
        String content = Files.readString(someFile.toPath(), StandardCharsets.UTF_8);
        assertEquals("Some text!", content);
    }

    @Test
    void testCommandsPathFileRoots() throws IOException {
        File some = new File(testRoot, "some");
        File someDirectory = new File(some, "directory");
        File someDirectoryHere = new File(someDirectory, "here");
        File file1 = new File(someDirectoryHere, "file_1.txt");
        File file2 = new File(someDirectoryHere, "file_2.txt");

        Path path = getResource("doc-examples/commands-path-file-roots.ska");
        interpreter.executeFile(path);

        assertTrue(file1.exists());
        assertTrue(file2.exists());

        String file1Content = Files.readString(file1.toPath(), StandardCharsets.UTF_8);
        String file2Content = Files.readString(file2.toPath(), StandardCharsets.UTF_8);
        assertEquals("File 1", file1Content);
        assertEquals("File 2", file2Content);
    }

    @Test
    void testCommandsPathDynamic() throws IOException {
        File folder1 = new File(testRoot, "folder_1");
        File folder2 = new File(testRoot, "folder_2");
        File newFile = new File(folder2, "new_file.txt");

        Path path = getResource("doc-examples/commands-path-dynamic.ska");
        interpreter.executeFile(path);

        assertFalse(folder1.exists());
        assertTrue(newFile.exists());

        String fileContent = Files.readString(newFile.toPath(), StandardCharsets.UTF_8);
        assertEquals("new file", fileContent);
    }

    @Test
    void testCommandsWriteThenRead() {
        assertPrints("doc-examples/commands-write-then-read.ska", "Some text!\n");
    }

    @Test
    void testCommandsAppend() {
        assertPrints("doc-examples/commands-append.ska", "Hello World!\n");
    }

    @Test
    void testCommandsCopy() throws IOException {
        File file = new File(testRoot, "file.txt");
        boolean fileCreated = file.createNewFile();

        if (!fileCreated)
            throw new RuntimeException("Test file failed to create");

        assertPrints("doc-examples/commands-copy.ska", "true\ntrue\n");
    }

    @Test
    void testCommandsMove() throws IOException {
        File file = new File(testRoot, "file.txt");
        boolean fileCreated = file.createNewFile();

        if (!fileCreated)
            throw new RuntimeException("Test file failed to create");

        assertPrints("doc-examples/commands-move.ska", "false\ntrue\n");
    }

    @Test
    void testCommandsRename() throws IOException {
        File file = new File(testRoot, "file.txt");
        boolean fileCreated = file.createNewFile();

        if (!fileCreated)
            throw new RuntimeException("Test file failed to create");

        assertPrints("doc-examples/commands-rename.ska", "false\ntrue\n");
    }
}
