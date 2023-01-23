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

import net.arkinsolomon.sakurainterpreter.execution.Value;

import java.util.Deque;

/**
 * Exception thrown when an exception occurs.
 */
public final class ExitException extends SakuraException {

    private final byte code;
    private Value value = Value.NULL;

    /**
     * Create a new exception with a code and a message.
     *
     * @param code The error code, which is zero if there is no error.
     * @param msg The message (or reason) for the early termination.
     */
    public ExitException(byte code, String msg){
        super(msg);
        this.code = code;
    }

    /**
     * Create a new exception with code 0, and a value.
     *
     * @param returnVal The value passed as a second argument to the function (if the exit code is zero).
     */
    public ExitException(Value returnVal){
        this((byte) 0, "Exit with code 0");
        this.value = returnVal;
    }

    /**
     * Create a new exit exception and preserve parent exception properties.
     *
     * @param line The line of the exception.
     * @param col The column of the exception.
     * @param code The exit code of the exception.
     * @param msg The message of the exception.
     * @param cause The cause of the exception.
     * @param callstack The callstack of the exception.
     * @param returnVal The return value of the function if it's code is zero.
     */
    ExitException(int line, int col, byte code, String msg, Throwable cause, Deque<String> callstack, Value returnVal){
        super(line, col, msg, cause, callstack);
        this.code = code;
        this.value = returnVal;
    }

    /**
     * Get the exit code provided at runtime.
     *
     * @return The exit code.
     */
    public byte getCode() {
        return code;
    }

    /**
     * Get the exit value if the code is zero.
     *
     * @returns The value returned.
     */
    public Value getValue() {
        return value;
    }
}
