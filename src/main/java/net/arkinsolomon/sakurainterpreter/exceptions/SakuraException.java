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

package net.arkinsolomon.sakurainterpreter.exceptions;

import net.arkinsolomon.sakurainterpreter.lexer.Token;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * Base class for all sakura exceptions.
 */
public class SakuraException extends RuntimeException {

    private Deque<String> callstack = new ArrayDeque<>();
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
     * @param msg   The exception message.
     * @param cause The exception that caused this one.
     */
    public SakuraException(String msg, Throwable cause) {
        super(msg, cause);
        this.msg = msg;
    }

    /**
     * Create a new exception caused at the position of the token.
     *
     * @param token The token at the position of the exception.
     * @param msg   The message of the exception.
     */
    public SakuraException(Token token, String msg) {
        this(token, msg, null);
    }

    /**
     * Create a new exception caused at the position of the token, with an exception that caused this one.
     *
     * @param token The token at the position of the exception.
     * @param msg   The message of the exception.
     * @param cause The exception that caused this one.
     */
    public SakuraException(Token token, String msg, Throwable cause) {
        this(token.line(), token.column(), msg, cause);
    }

    /**
     * Create a new exception at a certain location with a message and a cause.
     *
     * @param line  The line number of the exception.
     * @param col   The column number of the exception.
     * @param msg   The exception message.
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

    /**
     * Create an exception with default values.
     *
     * @param line      The line of the exception.
     * @param col       The column of the exception.
     * @param msg       The message of the exception.
     * @param cause     The cause of the exception.
     * @param callstack The exception's callstack.
     */
    protected SakuraException(int line, int col, String msg, Throwable cause, Deque<String> callstack) {
        this(line, col, msg, cause);
        this.callstack = callstack;
    }

    /**
     * Get just the text part of the message, ignoring the position.
     *
     * @return The text part of the message.
     */
    public String getMessageText() {
        return msg;
    }

    /**
     * Check if this exception already has a location.
     *
     * @return True if this exception already has a location.
     */
    public final boolean isLocationSet() {
        return hasLoc;
    }

    /**
     * Add an item to the error's callstack.
     *
     * @param line       The line of the call.
     * @param col        The column of the call.
     * @param identifier The function that made the call.
     */
    public final void addStackTraceItem(int line, int col, String identifier) {
        callstack.push("[%d:%d] %s".formatted(line, col, identifier));
    }

    /**
     * Get the stacktrace.
     *
     * @return The stacktrace.
     */
    @SuppressWarnings("AvoidObjectArrays")
    public final String[] getCallstack() {
        List<String> traceList = Arrays.asList(callstack.toArray(String[]::new));
        Collections.reverse(traceList);
        return traceList.toArray(String[]::new);
    }

    /**
     * Create an exception with the same message and cause, but with a different position.
     *
     * @param line The line number of the position.
     * @param col  The column of the position.
     * @return A new exception at the position.
     */
    public final SakuraException setPosition(int line, int col) {
        Throwable cause = getCause();

        // Propagate exit exceptions
        if (this instanceof ExitException)
            return new ExitException(line, col, ((ExitException) this).getCode(), msg, cause == null ? this : cause, callstack, ((ExitException) this).getValue());

        return new SakuraException(line, col, msg, cause == null ? this : cause, callstack);
    }

    /**
     * Create an exception with the same message and cause, but with a different position, at the location of a token.
     *
     * @param token The token at the position of the exception.
     * @return A new exception at the position given by the token.
     */
    public final SakuraException setPosition(Token token) {
        return setPosition(token.line(), token.column());
    }
}
