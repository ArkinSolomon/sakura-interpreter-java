/*
 * Copyright (c) 2023. Sakura Interpreter Java Contributors.
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

package net.arkinsolomon.sakurainterpreter.lexer;

import java.util.List;

/**
 * Data for a command with a "TO" in it.
 *
 * @param first The tokens to the left of the "TO" statement.
 * @param second The tokens to the right of the "TO".
 */
public record DualArgCmdData(List<Token> first, List<Token> second) {
}
