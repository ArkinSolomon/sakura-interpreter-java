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

package net.arkinsolomon.sakurainterpreter.functions;

import net.arkinsolomon.sakurainterpreter.execution.Value;

import java.util.List;

/**
 * The print function to print a value.
 */
public final class PrintFunction implements Function {

    /**
     * Print the values provided.
     *
     * @param args The arguments of the function.
     * @return Always returns {@link Value#NULL}.
     */
    @Override
    public Value execute(List<Value> args) {
       String[] values = new String[args.size()];
        for (int i = 0; i < args.size(); i++) {
            Value arg = args.get(i);
            values[i] = arg.toString();
        }

        String printValue = String.join(" ", values);
        System.out.println(printValue);
        return Value.NULL;
    }
}
