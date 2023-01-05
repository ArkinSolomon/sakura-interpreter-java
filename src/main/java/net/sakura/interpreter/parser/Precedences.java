/*
 * Copyright (c) 2022-2023. Sakura Contributors.
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

package net.sakura.interpreter.parser;

/**
 * Precedences for every type of operator.
 */
final class Precedences {
    static int RETURN = 20;
    static int ASSIGNMENT = 30;
    static int BINARY_OR = 33;
    static int BINARY_AND = 35;
    static int COMPARISON = 40;
    static int ADD_SUB = 50;
    static int MULTIPLY_SLASH = 60;
    static int PREFIX = 80;
    static int PARENTHETICAL = 90;
    static int VALUE = 100;

    /**
     * No class instances.
     */
    private Precedences() {}
}
