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

import net.sakura.interpreter.execution.DataType;
import net.sakura.interpreter.execution.ExecutionContext;
import net.sakura.interpreter.execution.Value;
import net.sakura.interpreter.lexer.Lexer;
import net.sakura.interpreter.lexer.Token;
import net.sakura.interpreter.lexer.TokenStorage;
import net.sakura.interpreter.parser.Parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * An interpreter to interpret Sakura.
 */
public class SakuraInterpreter {

    private final InterpreterOptions options;

    /**
     * Create a new interpreter instance with no configuration.
     */
    public SakuraInterpreter() {
        this(new InterpreterOptions());
    }

    /**
     * Create a new interpreter instance with options for the interpreter.
     *
     * @param options The options for the interpreter.
     */
    public SakuraInterpreter(InterpreterOptions options) {
        this.options = options;
    }

    /**
     * Execute a file.
     *
     * @param file The  path to the file to execute.
     * @return The value returned by the file. Will be {@link Value#NULL} if the file does not return anything.
     */
    public Value executeFile(Path file) throws IOException {
        Lexer lexer = new Lexer(file);
        return execLexer(lexer);
    }

    /**
     * Execute some code in a string.
     *
     * @return The value returned by the code.
     */
    public Value executeText(String text) {
        Lexer lexer = new Lexer(text);
        return execLexer(lexer);
    }

    /**
     * Execute the result of a lexical analysis.
     *
     * @param lexer The lexer that performed the analysis.
     * @return The result of the analyzed tokens after execution.
     */
    private Value execLexer(Lexer lexer) {
        List<Token> tokens = lexer.analyze();
        TokenStorage tokenStorage = new TokenStorage(tokens);

        System.out.println();
        tokenStorage.printTokens();

        Parser parser = new Parser(tokenStorage);
        parser.parse();

        System.out.println("\n--Output--\n");
        ExecutionContext ctx = createContext();
        Value retVal = parser.execute(ctx).returnValue();

        System.out.println("\n--Execution context--\n");
        ctx.printContext();

        return retVal;
    }

    private ExecutionContext createContext() {
        Map<String, Value> envVars = options.envVariables;
        envVars.put("@__executor", new Value(DataType.STRING, options.executor, false));
        return new ExecutionContext(envVars);
    }

    public static void main(String[] args) throws IOException {
        InterpreterOptions opts = new InterpreterOptions();
        opts.setExecutor("dev-env");

        SakuraInterpreter interpreter = new SakuraInterpreter(opts);
        interpreter.executeFile(Path.of(Objects.requireNonNull(SakuraInterpreter.class.getResource("/test.ska")).getFile()));
    }
}
