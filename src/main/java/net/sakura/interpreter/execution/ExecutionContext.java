/*
 * Copyright (c) 2022-2023. Sakura Contributors.
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

package net.sakura.interpreter.execution;

import net.sakura.interpreter.exceptions.SakuraException;
import net.sakura.interpreter.functions.Function;
import net.sakura.interpreter.functions.PrintFunction;
import net.sakura.interpreter.functions.RangeFunction;
import net.sakura.interpreter.functions.TerminateFunction;
import net.sakura.interpreter.parser.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The execution context of sentence.
 */
public class ExecutionContext {

    // Map identifiers to values
    private final Map<String, Value> identifiers = new HashMap<>();
    private final ExecutionContext root;
    private ExecutionContext parent = null;

    /**
     * Create a new blank root execution context
     */
    public ExecutionContext() {
        this(new HashMap<>());
    }

    /**
     * Create a new root execution context with environment variables.
     *
     * @param envVars The environment variables to create.
     */
    public ExecutionContext(Map<String, Value> envVars) {
        identifiers.putAll(envVars);
        assignDefaults();
        root = this;
    }

    /**
     * Create a new execution context within the scope of another execution context.
     *
     * @param parent The context to inherit from.
     */
    public ExecutionContext(ExecutionContext parent) {
        this.parent = parent;
        root = parent.root;
    }

    /**
     * Get the root (top-level) execution context.
     *
     * @return The root execution context.
     */
    public ExecutionContext getRoot() {
        return root;
    }

    /**
     * Get the value tied to an identifier in this or any ancestor contexts.
     *
     * @param identifier The identifier to get the value of.
     * @return The value tied to the identifier, or null if the identifier does not exist.
     */
    public Value getIdentifier(String identifier) {
        if (identifiers.containsKey(identifier))
            return identifiers.get(identifier);

        if (parent != null)
            return parent.getIdentifier(identifier);
        return Value.NULL;
    }

    /**
     * Define an identifier in the local context.
     *
     * @param identifier The name of the identifier to define.
     * @param val        The value of the identifier.
     */
    public void defineIdentifier(String identifier, Value val) {
        identifiers.put(identifier, val);
    }

    /**
     * Modify an identifier.
     *
     * @param identifier The name of the identifier to modify.
     * @param val        The new value of the identifier.
     */
    public void modifyIdentifier(String identifier, Value val) {
        if (!hasIdentifier(identifier))
            throw new RuntimeException("Identifier \"%s\" not found".formatted(identifier));

        if (identifiers.containsKey(identifier)) {
            if (!identifiers.get(identifier).isMutable())
                throw new RuntimeException("Identifier \"%s\" is not mutable".formatted(identifier));

            identifiers.put(identifier, val);
        } else
            parent.modifyIdentifier(identifier, val);
    }

    /**
     * Check if this context or any parent context contains an identifier.
     *
     * @param identifier The identifier to check for.
     * @return True if the identifier exists.
     */
    public boolean hasIdentifier(String identifier) {
        final boolean parentHasKey = this.parent != null && this.parent.hasIdentifier(identifier);
        return identifiers.containsKey(identifier) || parentHasKey;
    }

    /**
     * Check if this context contains an identifier, ignoring parent contexts.
     *
     * @param identifier The identifier to check for.
     * @return True if this context contains such an identifier.
     */
    public boolean hasLocalIdentifier(String identifier){
        return identifiers.containsKey(identifier);
    }

    /**
     * Execute a function.
     *
     * @param identifier The function identifier.
     * @param args       The value of the arguments to pass to the function.
     */
    public Value executeFunc(String identifier, List<Value> args) {
        Value functionValue = getIdentifier(identifier);
        if (functionValue.type() != DataType.FUNCTION)
            throw new SakuraException("Can not call \"%s\" of type \"%s\". Only function types are callable.".formatted(identifier, functionValue.type()));

        try {
            Function func = (Function) functionValue.value();
            return func.execute(args);
        } catch (SakuraException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("An exception occurred while executing the function \"%s\".".formatted(identifier), e);
        }
    }

    /**
     * Register a function with the execution context.
     *
     * @param identifier The function identifier.
     * @param function   The function to execute.
     */
    public void registerFunc(String identifier, Function function) {
        identifiers.put(identifier, new Value(DataType.FUNCTION, function, false));
    }

    /**
     * Set default values
     */
    private void assignDefaults() {
        identifiers.put("NULL", Value.NULL);
        identifiers.put("TRUE", Value.TRUE);
        identifiers.put("FALSE", Value.FALSE);

        identifiers.put("@__lang_version", new Value(DataType.STRING, "1.0.0-alpha", false));
        identifiers.put("@__interpreter", new Value(DataType.STRING, "sakura.official.java", false));
        identifiers.put("@__interpreter_version", new Value(DataType.STRING, "1.0-SNAPSHOT", false));

        registerFunc("print", new PrintFunction());
        registerFunc("range", new RangeFunction());
        registerFunc("terminate", new TerminateFunction());
    }

    /**
     * Print the execution context.
     */
    public void printContext() {
        String[] ctxIds = identifiers
                .keySet()
                .stream()
                .sorted()
                .toArray(String[]::new);

        for (String k : ctxIds){
            Value val = identifiers.get(k);
            String output = val.toString();
            if (val.value() instanceof Function){
                if (val.value() instanceof Node)
                    output = "<defined function>";
                else
                    output = val.value().getClass().getCanonicalName();
            }

            System.out.printf("%s: %s%n", k, output);
        }

    }
}
