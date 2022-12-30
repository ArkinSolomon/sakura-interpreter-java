/*
 * Copyright (c) 2022. Sakura Contributors.
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

package net.sakura.interpreter;

import net.sakura.interpreter.lexer.Lexer;
import net.sakura.interpreter.lexer.Token;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

public class SakuraInterpreter {

    public static void main(String[] args) throws IOException, URISyntaxException {
//        String input = "$var = @default here a NULL";
        URL url = SakuraInterpreter.class.getResource("/main.ska");
        File file = new File(url.toURI());
        String input = Files.readString(file.toPath());

        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.analyze();

        for (Token token : tokens){
            System.out.println(token);
        }
    }
}
