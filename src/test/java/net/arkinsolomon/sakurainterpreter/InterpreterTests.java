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

import net.arkinsolomon.sakurainterpreter.exceptions.ExitException;
import net.arkinsolomon.sakurainterpreter.exceptions.SakuraException;
import net.arkinsolomon.sakurainterpreter.execution.DataType;
import net.arkinsolomon.sakurainterpreter.execution.Iterable;
import net.arkinsolomon.sakurainterpreter.execution.Value;
import net.arkinsolomon.sakurainterpreter.operations.Operation;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class InterpreterTests extends SakuraTestBase {
    @Test
    void testPrint() throws IOException {
        Path path = getResource("test-print.ska");
        interpreter.executeFile(path);
        assertEquals("Hello World!\n", printer.getOutput());
    }

    @Test
    void testPrintEnvVar() throws IOException {
        Path path = getResource("test-print-env-var.ska");
        interpreter.executeFile(path);
        assertEquals("arkinsolomon.java.tester\n", printer.getOutput());
    }

    @Test
    void testFibonacci() {
        assertReturnValue("test-fibonacci.ska", 34d);
    }

    @Test
    void testReturn() {
        assertReturnValue("test-return.ska", "A return value!");
    }

    @Test
    void testRangeSum() {
        assertReturnValue("test-range-sum.ska", 1225);
    }

    @Test
    void testPathParsing() throws IOException {
        Path path = getResource("test-path-parsing.ska");
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
        Path path = getResource("test-write.ska");
        interpreter.executeFile(path);

        String content = String.join("\n", Files.readAllLines(new File(testRoot, "file.txt").toPath()));
        assertEquals("test-write\nline 2 here!", content);
    }

    @Test
    void testIterableFunc() {
        assertReturnValue("test-iterable-func.ska", 6);
    }

    @Test
    void testArglessFunc() throws IOException {
        Path path = getResource("test-argless-func.ska");
        interpreter.executeFile(path);

        String output = printer.getOutput();
        assertEquals("cheeseburger\ncheeseburger\ncheeseburger\n", output);
    }

    @Test
    void testArglessParenFunc() throws IOException {
        Path path = getResource("test-argless-paren-func.ska");
        interpreter.executeFile(path);

        String output = printer.getOutput();
        assertEquals("No args!\nNo args!\n", output);
    }

    @Test
    void testPrintBothArgs() throws IOException {
        Path path = getResource("test-print-both-args.ska");
        interpreter.executeFile(path);

        String output = printer.getOutput();
        assertEquals("Arg 1 was: John, and arg 2 was: Smith\n", output);
    }

    @Test
    void testWriteSecurity() {
        Path path = getResource("test-write-security.ska");
        SakuraException thrown = assertThrows(SakuraException.class, () -> interpreter.executeFile(path));
        assertTrue(thrown.getMessageText().startsWith("No write permissions for file"));
    }

    @Test
    void testReadSecurity() {
        Path path = getResource("test-read-security.ska");
        SakuraException thrown = assertThrows(SakuraException.class, () -> interpreter.executeFile(path));
        assertTrue(thrown.getMessageText().startsWith("No read permissions for file"));
    }

    @Test
    void testAppendSecurity() {
        Path path = getResource("test-append-security.ska");
        SakuraException thrown = assertThrows(SakuraException.class, () -> interpreter.executeFile(path));
        assertTrue(thrown.getMessageText().startsWith("No write permissions for file"));
    }

    @Test
    void testDeleteSecurity() {
        Path path = getResource("test-delete-security.ska");
        SakuraException thrown = assertThrows(SakuraException.class, () -> interpreter.executeFile(path));
        assertTrue(thrown.getMessage().startsWith("[2:1] No write permissions for file"));

        // Even though we deleted the deletable file, it should roll back
        File existingReadFile = new File(new File(testRoot, "disallow-read"), "existing-file.txt");
        assertTrue(existingReadFile.exists());
    }

    @Test
    void testMkdirSecurity() {
        Path path = getResource("test-mkdir-security.ska");
        SakuraException thrown = assertThrows(SakuraException.class, () -> interpreter.executeFile(path));
        assertTrue(thrown.getMessageText().startsWith("No write permissions for file"));
    }

    @Test
    void testMkdirsSecurity() {
        Path path = getResource("test-mkdirs-security.ska");
        SakuraException thrown = assertThrows(SakuraException.class, () -> interpreter.executeFile(path));
        assertTrue(thrown.getMessageText().startsWith("No write permissions for file"));
    }

    @Test
    void testMoveSecurity() {
        Path path = getResource("test-move-security.ska");
        SakuraException thrown = assertThrows(SakuraException.class, () -> interpreter.executeFile(path));
        assertTrue(thrown.getMessageText().startsWith("Insufficient permissions to move"));
    }

    @Test
    void testExistsSecurity() {
        Path path = getResource("test-exists-security.ska");
        SakuraException thrown = assertThrows(SakuraException.class, () -> interpreter.executeFile(path));
        assertTrue(thrown.getMessageText().startsWith("Insufficient permission"));
    }

    @Test
    void testIsDirSecurity() {
        Path path = getResource("test-isdir-security.ska");
        SakuraException thrown = assertThrows(SakuraException.class, () -> interpreter.executeFile(path));
        assertTrue(thrown.getMessageText().startsWith("Insufficient permission"));
    }

    @Test
    void testIsFileSecurity() {
        Path path = getResource("test-isfile-security.ska");
        SakuraException thrown = assertThrows(SakuraException.class, () -> interpreter.executeFile(path));
        assertTrue(thrown.getMessageText().startsWith("Insufficient permission"));
    }

    @Test
    void testExitNoArgs() throws IOException {
        Path path = getResource("test-exit-no-args.ska");
        Value retVal = interpreter.executeFile(path);
        assertEquals(DataType.NULL, retVal.type());
    }

    @Test
    void testExitException() {
        Path path = getResource("test-exit-exception.ska");
        ExitException thrown = assertThrows(ExitException.class, () -> interpreter.executeFile(path));
        assertEquals(29, thrown.getCode());
    }

    @Test
    void testExitReturn() {
        assertReturnValue("test-exit-return.ska", 0d);
    }

    @Test
    void testTypeFunc() {
        assertReturnValue("test-type-func.ska", "string");
    }

    @Test
    void testForLoopControl() {
        assertPrints("test-for-loop-control.ska", "1\n2\n4\n");
    }

    @Test
    void testForLoopReturn() {
        assertReturnValue("test-for-loop-return.ska", 5d);
    }

    @Test
    void testReturnPosPrefix() {
        assertReturnValue("test-return-pos-prefix.ska", 4);
    }

    @Test
    void testReturnNegPrefix() {
        assertReturnValue("test-return-neg-prefix.ska", -24);
    }

    @Test
    void testReturnNotPrefix() {
        assertReturnValue("test-return-not-prefix.ska", false);
    }

    @Test
    void testDotPathParsing() throws IOException {
        Path path = getResource("test-dot-path-parsing.ska");
        Value retVal = interpreter.executeFile(path);

        assertEquals(DataType.ITERABLE, retVal.type());

        Iterable iter = (Iterable) retVal.value();

        Value current = iter.next();
        assertEquals(DataType.PATH, current.type());
        assertEquals(Operation.getFilePathStr(testRoot.getParentFile()), Operation.getFilePathStr((File) current.value()));

        current = iter.next();
        assertEquals(DataType.PATH, current.type());
        assertEquals(Operation.getFilePathStr(testRoot), Operation.getFilePathStr((File) current.value()));

        current = iter.next();
        assertEquals(DataType.PATH, current.type());
        assertEquals(Operation.getFilePathStr(testRoot.getParentFile()), Operation.getFilePathStr((File) current.value()));

        current = iter.next();
        assertEquals(DataType.PATH, current.type());
        assertEquals(Operation.getFilePathStr(testRoot.getParentFile()), Operation.getFilePathStr((File) current.value()));

        assertNull(iter.next());
    }

    @Test
    void testCanReadFunc(){
        assertReturnValue("test-can-read-func.ska", false);
    }

    @Test
    void testCanWriteFunc(){
        assertReturnValue("test-can-write-func.ska", false);
    }
}
