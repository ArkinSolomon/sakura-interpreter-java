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

package net.arkinsolomon.sakurainterpreter.execution;

import net.arkinsolomon.sakurainterpreter.lexer.Token;
import net.arkinsolomon.sakurainterpreter.parser.EarlyReturnType;

/**
 * The result of executing a bit of code.
 *
 * @param earlyReturnType The type of the early return. None if there was not an early return.
 * @param returnValue The return value of the early return.
 * @param returner The token that started the return.
 */
public record ExecutionResult(EarlyReturnType earlyReturnType, Value returnValue, Token returner) {
}
