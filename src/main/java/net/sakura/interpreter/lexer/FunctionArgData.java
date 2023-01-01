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
 * Data for a single function argument in a function definition.
 *
 * @param identifier The identifier of the argument.
 * @param isConstant True if the variable is a constant.
 * @param hasDefault True if the variable has a default value.
 * @param defaultValue The tokens to parse for the default value.
 */
public record FunctionArgData(String identifier, boolean isConstant, boolean hasDefault, List<Token> defaultValue) {
}
