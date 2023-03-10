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

package net.arkinsolomon.sakurainterpreter.functions;

import com.google.errorprone.annotations.Var;
import java.util.List;
import net.arkinsolomon.sakurainterpreter.exceptions.ExitException;
import net.arkinsolomon.sakurainterpreter.exceptions.SakuraException;
import net.arkinsolomon.sakurainterpreter.execution.DataType;
import net.arkinsolomon.sakurainterpreter.execution.ExecutionContext;
import net.arkinsolomon.sakurainterpreter.execution.Value;

/**
 * A function that terminates script execution immediately.
 */
public final class ExitFunction implements Function {

    /**
     * Throw an exception that terminates execution.
     */
    @Override
    public Value execute(List<Value> args, ExecutionContext ctx) {
        @Var String reason = "<unknown reason>";
        @Var Value retVal = Value.NULL;
        @Var byte code = 0;

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
