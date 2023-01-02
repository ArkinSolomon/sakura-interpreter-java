/*
 * Copyright (c) 2022. Sakura Contributors.
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

package net.sakura.interpreter.lexer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * This class has lets us use it as a scanner and peek to the next value,
 * character by character, without scanning it. See
 * <a href="http://www.javased.com/?post=4288643">source</a>.
 */
public class PeekableScanner {
    private final Scanner scan1;
    private final Scanner scan2;
    private String next;

    /**
     * Create a new peekable scanner with a file.
     *
     * @param source The path to the file to scan.
     */
    public PeekableScanner(Path source) throws IOException {
        scan1 = new Scanner(source);
        scan2 = new Scanner(source);
        init();
    }

    /**
     * Create a new peekable scanner with a string.
     *
     * @param input The string to create the scanner from.
     */
    public PeekableScanner(String input) {
        scan1 = new Scanner(input);
        scan2 = new Scanner(input);
        init();
    }

    /**
     * Initialize the scanners.
     */
    private void init() {
        scan1.useDelimiter("");
        scan2.useDelimiter("");
        next = scan2.next();
    }

    /**
     * Check if the scanner has another character.
     *
     * @return True if the scanner has another character.
     */
    public boolean hasNext() {
        return scan1.hasNext();
    }

    /**
     * Get the next character.
     *
     * @return The next character.
     */
    public String next() {
        next = (scan2.hasNext() ? scan2.next() : null);
        return scan1.next();
    }

    /**
     * Get the next character without advancing the scanner's pointer.
     *
     * @return The next character.
     */
    public String peek() {
        return next;
    }
}