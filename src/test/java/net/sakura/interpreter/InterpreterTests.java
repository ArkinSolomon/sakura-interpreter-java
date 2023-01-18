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
import net.sakura.interpreter.execution.Iterable;
import net.sakura.interpreter.execution.Value;
import org.apache.commons.io.FileUtils;
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
        try {
            return Path.of(Objects.requireNonNull(InterpreterTests.class.getResource(resourcePath)).getFile());
        } catch (NullPointerException e) {
            throw new RuntimeException("Could not find resource: " + resourcePath);
        }
    }

    @BeforeEach
    void beforeEach() throws IOException {
        InterpreterOptions options = new InterpreterOptions();
        options.setExecutor("sakura.tester");

        testRoot = new File(temp, "interpreter-test-files");
        Files.createDirectories(testRoot.toPath());
        options.setRoot(testRoot);

        printer = new TestPrintFunction();
        options.defineFunc("print", printer);

        interpreter = new SakuraInterpreter(options);
    }

    @AfterEach
    void teardown() throws IOException {
        FileUtils.deleteDirectory(testRoot);
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
        Value val = interpreter.executeFile(path);
        assertEquals("A return value!", val.value());
    }

    @Test
    void testRangeSum() throws IOException {
        Path path = getResource("/test-range-sum.ska");
        Value val = interpreter.executeFile(path);
        assertEquals(val.type(), DataType.NUMBER);
        assertEquals(val.value(), 1225d);
    }

    @Test
    void testPathParsing() throws IOException {
        Path path = getResource("/test-path-parsing.ska");
        Value val = interpreter.executeFile(path);
        assertEquals(val.type(), DataType.ITERABLE);

        Iterable iterable = ((Iterable) val.value()).copy();
        Value currVal = iterable.next();

        assertEquals(currVal.type(), DataType.PATH);
        assertEquals(testRoot, currVal.value());

        currVal = iterable.next();
        assertEquals(currVal.type(), DataType.PATH);
        File subDir = new File(testRoot, "subdir");
        assertEquals(subDir.getCanonicalPath(), ((File) currVal.value()).getCanonicalPath());

        currVal = iterable.next();
        assertEquals(currVal.type(), DataType.PATH);
        File subDir2 = new File(subDir, "subdir2");
        assertEquals(subDir2.getCanonicalPath(), ((File) currVal.value()).getCanonicalPath());
    }

    @Test
    void testWrite() throws IOException {
        Path path = getResource("/test-write.ska");
        interpreter.executeFile(path);

        String content = String.join("\n", Files.readAllLines(new File(testRoot, "file.txt").toPath()));
        assertEquals("test-write\nline 2 here!", content);
    }

    @Test
    void testIterableFunc() throws IOException {
        Path path = getResource("/test-iterable-func.ska");
        Value val = interpreter.executeFile(path);

        assertEquals(val.type(), DataType.NUMBER);
        assertEquals(val.value(), 6d);
    }

    @Test
    void testArglessFunc() throws IOException {
        Path path = getResource("/test-argless-func.ska");
        interpreter.executeFile(path);

        String output = printer.getOutput();
        assertEquals("cheeseburger\ncheeseburger\ncheeseburger\n", output);
    }

    @Test
    void testArglessParenFunc() throws IOException {
        Path path = getResource("/test-argless-paren-func.ska");
        interpreter.executeFile(path);

        String output = printer.getOutput();
        assertEquals("No args!\nNo args!\n", output);
    }

    @Test
    void testPrintBothArgs() throws IOException {
        Path path = getResource("/test-print-both-args.ska");
        interpreter.executeFile(path);

        String output = printer.getOutput();
        assertEquals("Arg 1 was: John, and arg 2 was: Smith\n", output);
    }
}
