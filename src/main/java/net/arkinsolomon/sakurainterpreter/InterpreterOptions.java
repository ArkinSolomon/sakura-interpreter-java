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

import net.arkinsolomon.sakurainterpreter.execution.DataType;
import net.arkinsolomon.sakurainterpreter.execution.Value;
import net.arkinsolomon.sakurainterpreter.functions.Function;
import net.arkinsolomon.sakurainterpreter.operations.OperationConfig;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Options for the interpreter.,
 */
public class InterpreterOptions {

    final OperationConfig operationConfig = new OperationConfig();
    String executor;
    private final Set<File> allowRead = new HashSet<>();
    private final Set<File> disallowRead = new HashSet<>();
    private final Set<File> allowWrite = new HashSet<>();
    private final Set<File> disallowWrite = new HashSet<>();
    final Map<String, Value> envVariables = new HashMap<>();
    final Map<String, Function> functions = new HashMap<>();
    File root = null;

    /**
     * Default constructor.
     */
    public InterpreterOptions() {
        this("unknown");
    }

    /**
     * Shorthand constructor to automatically set the executor.
     */
    public InterpreterOptions(String executor) {
        this.executor = executor;
    }

    /**
     * Set the executor of the interpreter so that the programmer can identify where their script is running.
     *
     * @param executor The executor of the interpreter.
     */
    public void setExecutor(String executor) {
        this.executor = executor;
    }

    /**
     * Set the root folder of the interpreter.
     *
     * @param root The root folder of the interpreter.
     */
    public void setRoot(File root) {
        if (!root.exists() || !root.isDirectory())
            throw new RuntimeException("The root file of the Sakura interpreter must be an existing directory");
        this.root = root;
    }

    /**
     * Get the operation config set by this interpreter.
     *
     * @return The operation config set by this interpreter.
     */
     OperationConfig getOperationConfig() {
        return operationConfig;
    }

    /**
     * Update restrictions for the operation config.
     */
    void updateRestrictions() {
        operationConfig.updateRestrictions(allowRead, disallowRead, allowWrite, disallowWrite);
    }

    /**
     * Define a boolean environment variable for the interpreter.
     *
     * @param identifier The identifier for the variable, without the "@" prefix.
     * @param value      The value of the variable.
     */
    public void defineEnvVar(String identifier, boolean value) {
        envVariables.put("@" + identifier, value ? Value.TRUE : Value.FALSE);
    }

    /**
     * Define a string environment variable for the interpreter.
     *
     * @param identifier The identifier for the variable, without the "@" prefix.
     * @param value      The value of the variable.
     */
    public void defineEnvVar(String identifier, String value) {
        envVariables.put("@" + identifier, new Value(DataType.STRING, value, false));
    }

    /**
     * Define an environment variable of type number for the interpreter.
     *
     * @param identifier The identifier for the variable, without the "@" prefix.
     * @param value      The value of the number.
     */
    public void defineEnvVar(String identifier, double value) {
        envVariables.put("@" + identifier, new Value(DataType.NUMBER, value, false));
    }

    /**
     * Override a default function, or define one.
     *
     * @param identifier The identifier of the function.
     * @param function   The code to execute.
     */
    public void defineFunc(String identifier, Function function) {
        functions.put(identifier, function);
    }

    /**
     * Allow read operations on a certain file or directory.
     *
     * @param file The file or directory to allow read permissions to.
     */
    public void allowRead(File file) {
        allowRead.add(file);
    }

    /**
     * Allow read operations on a certain file or directory.
     *
     * @param path The path to the file or directory to allow read permissions to.
     */
    public void allowRead(Path path) {
        allowRead(path.toFile());
    }

    /**
     * Allow read operations on multiple files or directories.
     *
     * @param files The files or directories to allow read permissions to.
     */
    public void allowRead(List<File> files) {
        allowRead.addAll(files);
    }

    /**
     * Deny read operations on a certain file or directory.
     *
     * @param file The file or directory to deny read permissions to.
     */
    public void disallowRead(File file) {
        disallowRead.add(file);
    }

    /**
     * Deny read operations on a certain file or directory.
     *
     * @param path The path to the file or directory to deny read permissions to.
     */
    public void disallowRead(Path path) {
        disallowRead(path.toFile());
    }

    /**
     * Deny read operations on multiple files or directories.
     *
     * @param files The files or directories to deny read permissions to.
     */
    public void disallowRead(List<File> files) {
        disallowRead.addAll(files);
    }

    /**
     * Allow write operations on a certain file or directory.
     *
     * @param file The file or directory to allow write permissions to.
     */
    public void allowWrite(File file) {
        allowWrite.add(file);
    }

    /**
     * Allow write operations on a certain file or directory.
     *
     * @param path The path to the file or directory to allow write permissions to.
     */
    public void allowWrite(Path path) {
        allowWrite(path.toFile());
    }

    /**
     * Allow write operations on multiple files or directories.
     *
     * @param files The files or directories to allow write permissions to.
     */
    public void allowWrite(List<File> files) {
        allowWrite.addAll(files);
    }

    /**
     * Deny write operations on a certain file or directory.
     *
     * @param file The file or directory to deny write permissions to.
     */
    public void disallowWrite(File file) {
        disallowWrite.add(file);
    }

    /**
     * Deny write operations on a certain file or directory.
     *
     * @param path The path to the file or directory to deny write permissions to.
     */
    public void disallowWrite(Path path) {
        disallowWrite(path.toFile());
    }

    /**
     * Deny write operations on multiple files or directories.
     *
     * @param files The files or directories to deny write permissions to.
     */
    public void disallowWrite(List<File> files) {
        disallowWrite.addAll(files);
    }
}
