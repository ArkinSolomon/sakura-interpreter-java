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

package net.arkinsolomon.sakurainterpreter;

import net.arkinsolomon.sakurainterpreter.execution.DataType;
import net.arkinsolomon.sakurainterpreter.execution.Value;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Base class to run tests
 */
public abstract class SakuraTestBase {

    private static File temp;
    protected static File testRoot;

    protected static SakuraInterpreter interpreter;
    protected static TestPrintFunction printer;

    @BeforeAll
    static void beforeAll() throws IOException {
        temp = Files.createTempDirectory("interpreter-tests-").toFile();
    }

    @AfterAll
    static void afterAll() throws IOException {
        Files.delete(temp.toPath());
    }

    /**
     * Get the absolute path to a resource.
     *
     * @param resourcePath The path of the resource relative to the test resource root.
     * @return The absolute path to the resource,
     */
    protected static Path getResource(String resourcePath) {
        try {
            URI uri = ClassLoader.getSystemResource(resourcePath).toURI();
            return Paths.get(uri);
        } catch (NullPointerException e) {
            throw new RuntimeException("Could not find resource: " + resourcePath);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid URI", e);
        }
    }

    /**
     * Test a numerical return value from a Sakura file.
     *
     * @param resourcePath The path to the resource file to execute (relative to test resource root).
     * @param expected     The expected return value.
     */
    protected static void assertReturnValue(String resourcePath, double expected) {
        try {
            Path path = getResource(resourcePath);
            Value retVal = interpreter.executeFile(path);
            assertEquals(DataType.NUMBER, retVal.type());
            assertEquals(expected, (double) retVal.value(), 1e-12);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Test a boolean return value from a Sakura file.
     *
     * @param resourcePath The path to the resource file to execute (relative to test resource root).
     * @param expected     The expected return value.
     */
    protected static void assertReturnValue(String resourcePath, boolean expected) {
        try {
            Path path = getResource(resourcePath);
            Value retVal = interpreter.executeFile(path);
            assertEquals(DataType.BOOLEAN, retVal.type());
            assertEquals(expected, retVal.value());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Test a string return value from a Sakura file.
     *
     * @param resourcePath The path to the resource file to execute (relative to test resource root).
     * @param expected     The expected return value.
     */
    protected static void assertReturnValue(String resourcePath, String expected) {
        try {
            Path path = getResource(resourcePath);
            Value retVal = interpreter.executeFile(path);
            assertEquals(DataType.STRING, retVal.type());
            assertEquals(expected, retVal.value());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Assert that a Sakura file prints out a value.
     *
     * @param resourcePath The path to the resource to execute (relative to test resource root).
     * @param expected The expected printed value.
     * @return The value returned from the script.
     */
    protected static Value assertPrints(String resourcePath, String expected) {
        try {
            Path path = getResource(resourcePath);
            Value retVal = interpreter.executeFile(path);

            String content = printer.getOutput();
            assertEquals(expected, content);

            return retVal;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void beforeEach() throws IOException {
        InterpreterOptions options = new InterpreterOptions("arkinsolomon.java.tester");

        testRoot = new File(temp, "interpreter-test-files");
        Files.createDirectories(testRoot.toPath());
        options.setRoot(testRoot);

        File disallowWritePath = new File(testRoot, "disallow-write");
        File disallowReadPath = new File(testRoot, "disallow-read");
        Files.createDirectories(disallowWritePath.toPath());
        Files.createDirectories(disallowReadPath.toPath());

        File existingWriteFile = new File(disallowWritePath, "existing-file.txt");
        File existingReadFile = new File(disallowReadPath, "existing-file.txt");

        Files.writeString(existingWriteFile.toPath(), "existing write file", Charset.defaultCharset(), StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
        Files.writeString(existingReadFile.toPath(), "existing read file", Charset.defaultCharset(), StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);

        options.allowWrite(testRoot);
        options.disallowWrite(disallowWritePath);

        options.allowRead(testRoot);
        options.disallowRead(disallowReadPath);

        printer = new TestPrintFunction();
        options.defineFunc("print", printer);

        interpreter = new SakuraInterpreter(options);
    }

    @AfterEach
    void teardown() throws IOException {
        FileUtils.deleteDirectory(testRoot);
    }
}
