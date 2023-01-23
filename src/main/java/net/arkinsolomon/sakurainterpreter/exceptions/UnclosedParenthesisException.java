/*
 * Copyright (c) 2023. Sakura Interpreter Java Contributors.
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

/**
 * An exception for unclosed parentheses.
 */
public final class UnclosedParenthesisException extends SakuraException {

    /**
     * Say that a closing parenthesis was expected at a certain location.
     *
     * @param line The line of the missing parentheses.
     * @param col The column of the missing parentheses.
     */
    public UnclosedParenthesisException(int line, int col){
        super(line, col, "Missing closing parenthesis.");
    }
}
