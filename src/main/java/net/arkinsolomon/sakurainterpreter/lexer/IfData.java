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

import java.util.ArrayList;
import java.util.List;

/**
 * The token of the if statement.
 *
 * @param conditions The branches of the if statement.
 * @param branches The body for each branch of the if statement.
 */
public record IfData(ArrayList<List<Token>> conditions, ArrayList<Token> branches) {
}