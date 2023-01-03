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

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * Base class for all sakura exceptions.
 */
public class SakuraException extends RuntimeException {

    private Deque<String> stacktrace = new ArrayDeque<>();
    private boolean hasLoc = false;
    private String msg;

    /**
     * Create an exception with a message.
     *
     * @param msg The exception message.
     */
    public SakuraException(String msg) {
        this(msg, null);
    }

    /**
     * Create an exception with a message and a cause.
     *
     * @param msg The exception message.
     * @param cause The exception that caused this one.
     */
    public SakuraException(String msg, Throwable cause) {
        super(msg, cause);
        this.msg = msg;
    }

    /**
     * Create a new exception at a certain location with a message and a cause.
     *
     * @param line The line number of the exception.
     * @param col  The column number of the exception.
     * @param msg  The exception message.
     * @param cause The exception that caused this one.
     */
    public SakuraException(int line, int col, String msg, Throwable cause) {
        this("[%d:%d] %s".formatted(line, col, msg), cause);
        this.msg = msg;
        hasLoc = true;
    }

    /**
     * Create a new exception at a certain location with a message.
     *
     * @param line The line number of the exception.
     * @param col  The column number of the exception.
     * @param msg  The exception message.
     */
    public SakuraException(int line, int col, String msg) {
        this(line, col, msg, null);
    }

    private SakuraException(int line, int col, String msg, Throwable cause, Deque<String> callstack){
        this(line, col, msg, cause);
        this.stacktrace = callstack;
    }

    /**
     * Check if this exception already has a location.
     *
     * @return True if this exception already has a location.
     */
    public boolean isLocationSet() {
        return hasLoc;
    }

    /**
     * Add an item to the error's callstack.
     *
     * @param line The line of the call.
     * @param col The column of the call.
     * @param identifier The function that made the call.
     */
    public void addStackTraceItem(int line, int col, String identifier) {
        stacktrace.push("[%d:%d] %s".formatted(line, col, identifier));
    }

    /**
     * Get the stacktrace.
     *
     * @return The stacktrace.
     */
    public String[] getStacktrace(){
        List<String> traceList= Arrays.asList(stacktrace.toArray(String[]::new));
         Collections.reverse(traceList);
         return traceList.toArray(String[]::new);
    }

    /**
     * Create an exception with the same message and cause, but with a different position.
     *
     * @param line The line number of the position.
     * @param col The column of the position.
     * @return A new exception at the position.
     */
    public SakuraException setPosition(int line, int col) {
        Throwable cause = getCause();
        return new SakuraException(line, col, msg, cause == null ? this : cause, stacktrace);
    }
}
