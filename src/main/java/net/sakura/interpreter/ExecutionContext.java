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

package net.sakura.interpreter;

import java.util.HashMap;
import java.util.Map;

/**
 * The execution context of sentence.
 */
public class ExecutionContext {

    // Map identifiers to values
    private final Map<String, Value> identifiers = new HashMap<>();

    private ExecutionContext parent = null;

    /**
     * Create a new blank execution context
     */
    public ExecutionContext() {
        this(new HashMap<String, Value>());
    }

    /**
     * Create a new execution context with environment variables.
     *
     * @param envVars The environment variables to create.
     */
    public ExecutionContext(Map<String, Value> envVars) {
        identifiers.putAll(envVars);
        assignDefaults();
    }

    /**
     * Create a new execution context within the scope of another execution context.
     *
     * @param parent The context to inherit from.
     */
    public ExecutionContext(ExecutionContext parent) {
        this.parent = parent;
    }

    public Value getIdentifier(String identifier) {
        if (identifiers.containsKey(identifier))
            return identifiers.get(identifier);

        if (parent != null)
            return parent.getIdentifier(identifier);
        return Value.NULL;
    }

    public void assignIdentifier(String identifier, Value val) {
        if (identifier.startsWith("@"))
            throw new UnsupportedOperationException("Can not assign to environment variable");

        if (getIdentifier(identifier) != Value.NULL) {
            Value oldValue = identifiers.get(identifier);
            if (!oldValue.isMutable())
                throw new UnsupportedOperationException("Value not mutable");
        }

        identifiers.put(identifier, val);
    }

    /**
     * Check if this context contains an identifier.
     *
     * @param identifier The identifier to check for.
     * @return True if the identifier exists.
     */
    public boolean hasIdentifier(String identifier){
        final boolean parentHasKey = this.parent != null && this.parent.hasIdentifier(identifier);
        return identifiers.containsKey(identifier) || parentHasKey;
    }

    /**
     * Set default values
     */
    private void assignDefaults() {
        identifiers.put("NULL", Value.NULL);

        identifiers.put("@__lang_version", new Value(Datatype.STRING, "1.0.0", false));
        identifiers.put("@__interpreter", new Value(Datatype.STRING, "sakura.official", false));
        identifiers.put("@__interpreter_version", new Value(Datatype.STRING, "1.0-SNAPSHOT", false));
    }

    /**
     * Print the execution context.
     */
    public void printContext() {
        for (String k : identifiers.keySet()) {
            System.out.printf("%s: %s%n", k, identifiers.get(k));
        }
    }
}
