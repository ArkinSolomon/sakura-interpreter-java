/*
 * Copyright (c) 2022-2023. Sakura Interpreter Java Contributors.
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

package net.arkinsolomon.sakurainterpreter.parser;

/**
 * Precedences for every type of operator.
 */
final class Precedences {
    final static int RETURN = 20;
    final static int ASSIGNMENT = 30;
    final static int BINARY_OR = 33;
    final static int BINARY_AND = 35;
    final static int EQUALITY = 37;
    final static int COMPARISON = 40;
    final static int ADD_SUB = 50;
    final static int MULTIPLY_SLASH = 60;
    final static int PREFIX = 80;
    final static int PARENTHETICAL = 90;
    final static int VALUE = 100;

    /**
     * No class instances.
     */
    private Precedences() {
    }
}
