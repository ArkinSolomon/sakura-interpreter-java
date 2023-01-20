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

package net.sakura.interpreter.functions;

import net.sakura.interpreter.exceptions.ExitException;
import net.sakura.interpreter.exceptions.SakuraException;
import net.sakura.interpreter.execution.DataType;
import net.sakura.interpreter.execution.Value;

import java.util.List;

/**
 * A function that terminates script execution immediately.
 */
public final class ExitFunction implements Function {

    /**
     * Throw an exception that terminates execution.
     */
    @Override
    public Value execute(List<Value> args) {
        String reason = "<unknown reason>";
        Value retVal = Value.NULL;
        byte code = 0;

        if (args.size() >= 1){
            Value firstArg  = args.get(0);

            if (firstArg.type() != DataType.NUMBER)
                throw new SakuraException("The first argument of the \"exit()\" (if provided) must be a number");

            code = (byte) ((double) firstArg.value());
        }

        if (args.size() >= 2){
            reason = args.get(1).value().toString();
            if (code == 0)
                retVal = args.get(1);
        }

        if (code == 0)
            throw new ExitException(retVal);
        else
            throw new ExitException(code, "Script execution terminated (code %d): %s".formatted(code, reason));
    }
}
