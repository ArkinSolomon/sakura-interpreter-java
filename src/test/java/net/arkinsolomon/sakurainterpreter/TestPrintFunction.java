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

package net.arkinsolomon.sakurainterpreter;

import net.arkinsolomon.sakurainterpreter.execution.Value;
import net.arkinsolomon.sakurainterpreter.functions.Function;

import java.util.List;

/**
 * A print function used for tests.
 */
public class TestPrintFunction implements Function {

    private String output = "";

    /**
     * Get the final output.
     *
     * @return The final output of all calls to this function.
     */
    public String getOutput() {
        return output;
    }

    @Override
    public Value execute(List<Value> args) {
        StringBuilder printStr = new StringBuilder();
        for (int i = 0; i < args.size(); i++) {
            Value arg = args.get(i);
            printStr.append(arg);
            if (i != args.size() - 1)
                printStr.append(" ");
        }
        String printValue = printStr.toString();

        output += printValue + "\n";
        System.out.println(printValue);
        return Value.NULL;
    }
}
