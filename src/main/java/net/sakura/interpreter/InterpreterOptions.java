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
import net.sakura.interpreter.functions.Function;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Options for the interpreter.,
 */
public class InterpreterOptions {

    String executor = "unknown";

    Map<String, Value> envVariables = new HashMap<>();
    Map<String, Function> functions = new HashMap<>();

    File root = new File("/");

    /**
     * Set the executor of the interpreter so that the programmer can identify where their script is running.
     *
     * @param executor The executor of the interpreter.
     */
    public void setExecutor(String executor) {
        this.executor = executor;
    }

    /**
     * Set the root of the interpreter.
     *
     * @param root The root of the interpreter.
     */
    public void setRoot(File root){
        this.root = root;
    }

    /**
     * Define a boolean environment variable for the interpreter.
     *
     * @param identifier The identifier for the variable, without the "@" prefix.
     * @param value The value of the variable.
     */
    public void defineEnvVar(String identifier, boolean value) {
        envVariables.put("@" + identifier, value ? Value.TRUE : Value.FALSE);
    }

    /**
     * Define a string environment variable for the interpreter.
     *
     * @param identifier The identifier for the variable, without the "@" prefix.
     * @param value The value of the variable.
     */
    public void defineEnvVar(String identifier, String value) {
        envVariables.put("@" + identifier, new Value(DataType.STRING, value, false));
    }

    /**
     * Define an environment variable of type number for the interpreter.
     *
     * @param identifier The identifier for the variable, without the "@" prefix.
     * @param value The value of the number.
     */
    public void defineEnvVar(String identifier, double value) {
        envVariables.put("@" + identifier, new Value(DataType.NUMBER, value, false));
    }

    /**
     * Override a default function, or define one.
     *
     * @param identifier The identifier of the function.
     * @param function The code to execute.
     */
    public void defineFunc(String identifier, Function function) {
        functions.put(identifier, function);
    }
}
