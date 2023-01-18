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

package net.sakura.interpreter;

import net.sakura.interpreter.execution.DataType;
import net.sakura.interpreter.execution.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Run basic tests.
 */
public class InterpreterTests {

    private static File temp;
    private static File testRoot;

    private static SakuraInterpreter interpreter;
    private static TestPrintFunction printer;

    @BeforeAll
    static void beforeAll() throws IOException {
        temp = Files.createTempDirectory("interpreter-tests").toFile();
    }

    /**
     * Get the absolute path to a resource.
     *
     * @param resourcePath The path of the resource relative to the test resource root.
     * @return The absolute path to the resource,
     */
    private static Path getResource(String resourcePath) {
        return Path.of(Objects.requireNonNull(InterpreterTests.class.getResource(resourcePath)).getFile());
    }

    @BeforeEach
    void beforeEach() throws IOException {
        InterpreterOptions options = new InterpreterOptions();
        options.setExecutor("sakura.tester");

        testRoot = new File(temp, "interpreter-test-files");
        options.setRoot(testRoot);
        Files.createDirectories(testRoot.toPath());

        printer = new TestPrintFunction();
        options.defineFunc("print", printer);

        interpreter = new SakuraInterpreter(options);
    }

    @AfterEach
    void teardown() throws IOException {
        Files.delete(testRoot.toPath());
    }

    @Test
    void testPrint() throws IOException {
        Path path = getResource("/test-print.ska");
        interpreter.executeFile(path);
        assertEquals("Hello World!\n", printer.getOutput());
    }

    @Test
    void testPrintEnvVar() throws IOException {
        Path path = getResource("/test-print-env-var.ska");
        interpreter.executeFile(path);
        assertEquals("sakura.tester\n", printer.getOutput());
    }

    @Test
    void testFibonacci() throws IOException {
        Path path = getResource("/test-fibonacci.ska");
        Value val = interpreter.executeFile(path);
        assertEquals(val.type(), DataType.NUMBER);
        assertEquals(val.value(), 34d);
    }

    @Test
    void testReturn() throws IOException {
        Path path = getResource("/test-return.ska");
        Value retVal = interpreter.executeFile(path);
        assertEquals("A return value!", retVal.value());
    }
}
