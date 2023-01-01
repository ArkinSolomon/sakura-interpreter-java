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

package net.sakura.interpreter.lexer;

import java.util.List;

/**
 * Data regarding a function definition.
 *
 * @param identifier The identifier for the new function.
 * @param args The arguments of the function.
 * @param body The body of the function.
 */
public record FunctionDefinitionData(String identifier, List<FunctionArgData> args, Token body) {

    /**
     * Add a token as the body.
     *
     * @param body The token to set as the body.
     * @return A new instance with the updated body.
     */
    FunctionDefinitionData addBody(Token body) {
        return new FunctionDefinitionData(identifier, args, body);
    }
}
