/*
 * Copyright (c) 2022-2023. Arkin Solomon.
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

package net.arkinsolomon.sakurainterpreter;

import net.arkinsolomon.sakurainterpreter.exceptions.SakuraException;
import net.arkinsolomon.sakurainterpreter.lexer.TokenStorage;
import net.arkinsolomon.sakurainterpreter.parser.Parser;
import net.arkinsolomon.sakurainterpreter.exceptions.ExitException;
import net.arkinsolomon.sakurainterpreter.execution.DataType;
import net.arkinsolomon.sakurainterpreter.execution.ExecutionContext;
import net.arkinsolomon.sakurainterpreter.execution.Value;
import net.arkinsolomon.sakurainterpreter.lexer.Lexer;
import net.arkinsolomon.sakurainterpreter.lexer.Token;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An interpreter to interpret Sakura.
 */
public class SakuraInterpreter {

    public static final String LANG_VERSION = "0.1.0-beta-1";
    public static final String INTERPRETER_VERSION = "1.0.1";

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

    public static void main(String[] args) throws IOException {

        ArgumentParser parser = ArgumentParsers.newFor("ska-java")
                .build()
                .description("Sakura interpreter built on Java")
                .version("Sakura interpreter for Java %s. (c) 2023 Arkin Solomon.\nLanguage version %s.\nContribute/Bug reports at https://github.com/ArkinSolomon/sakura-interpreter-java".formatted(INTERPRETER_VERSION, LANG_VERSION));

        MutuallyExclusiveGroup g = parser.addMutuallyExclusiveGroup("execs").required(true);
        g.addArgument("-f", "--file")
                .dest("file")
                .metavar("file")
                .type(String.class)
                .nargs(1)
                .help("execute a file");

        g.addArgument("-e", "--execute")
                .dest("code")
                .metavar("code")
                .type(String.class)
                .nargs(1)
                .help("execute a string of text as a script");

        parser.addArgument("-c", "--config")
                .dest("configFile")
                .metavar("file")
                .type(String.class)
                .nargs(1)
                .help("specify a configuration file");

        parser.addArgument("-v", "--version")
                .action(Arguments.version())
                .help("print the version");
        try {
            Namespace res = parser.parseArgs(args);

            InterpreterOptions options;

            List<String> config = res.get("configFile");
            if (config != null) {
                String configPath = config.get(0);
                File configFile = new File(configPath);
                if (!configFile.exists())
                    throw new RuntimeException("Specified configuration file \"%s\" does not exist".formatted(configPath));

                options = parseConfig(configFile);
            } else
                options = new InterpreterOptions("sakura.java.cli");

            SakuraInterpreter interpreter = new SakuraInterpreter(options);
            Value executionValue;

            List<String> runFilePath = res.get("file");
            List<String> runText = res.get("code");

            // One of these must run, argument parser guarantees -e and -f are mutually exclusive
            if (res.getString("file") != null) {
                File runFile = new File(runFilePath.get(0));
                if (!runFile.exists())
                    throw new RuntimeException("File \"%s\" does not exist".formatted(runFilePath));

                executionValue = interpreter.executeFile(runFile);
            } else
                executionValue = interpreter.executeText(runText.get(0));

            if (executionValue.type() != DataType.NULL)
                System.out.println(executionValue);
            System.exit(0);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        } catch (SakuraException e) {
            System.err.println(e.getMessage());
            for (String call : e.getCallstack())
                System.err.println("\tat " + call);
            if (e instanceof ExitException)
                System.exit(((ExitException) e).getCode());
        }
    }

    /**
     * Parse a config file.
     *
     * @param configFile The configuration file to parse.
     * @return Options generated from the parsed configuration file.
     */
    private static InterpreterOptions parseConfig(File configFile) {
        InterpreterOptions opts = new InterpreterOptions();
        opts.setExecutor("arkinsolomon.java.cli");

        try {
            JSONParser parser = new JSONParser();
            JSONObject configObj = (JSONObject) parser.parse(new FileReader(configFile, StandardCharsets.UTF_8));

            if (configObj.containsKey("executor")) {
                Object executor = configObj.get("executor");
                if (!(executor instanceof String))
                    throw new RuntimeException("If provided in the configuration file, \"executor\" must be a string");
                opts.setExecutor((String) executor);
            }

            if (configObj.containsKey("root")) {
                Object root = configObj.get("root");
                if (!(root instanceof String))
                    throw new RuntimeException("If provided in the configuration file, \"root\" must be a string (which represents path to a file)");
                opts.setExecutor((String) root);
            }

            if (configObj.containsKey("allowRead")) {
                Object allowRead = configObj.get("allowRead");
                if (!(allowRead instanceof JSONArray))
                    throw new RuntimeException("If provided in the configuration file, \"allowRead\" must be a list of strings (which represents paths)");

                @SuppressWarnings("unchecked")
                List<String> files = (List<String>) ((JSONArray) allowRead).stream().toList();
                List<File> allowReadFiles = new ArrayList<>();
                files.forEach(path -> allowReadFiles.add(new File(path)));
                opts.allowRead(allowReadFiles);
            }

            if (configObj.containsKey("disallowRead")) {
                Object disallowRead = configObj.get("disallowRead");
                if (!(disallowRead instanceof JSONArray))
                    throw new RuntimeException("If provided in the configuration file, \"disallowRead\" must be a list of strings (which represents paths)");

                @SuppressWarnings("unchecked")
                List<String> files = (List<String>) ((JSONArray) disallowRead).stream().toList();
                List<File> disallowReadFiles = new ArrayList<>();
                files.forEach(path -> disallowReadFiles.add(new File(path)));
                opts.allowRead(disallowReadFiles);
            }

            if (configObj.containsKey("allowWrite")) {
                Object allowWrite = configObj.get("allowWrite");
                if (!(allowWrite instanceof JSONArray))
                    throw new RuntimeException("If provided in the configuration file, \"allowWrite\" must be a list of strings (which represents paths)");

                @SuppressWarnings("unchecked")
                List<String> files = (List<String>) ((JSONArray) allowWrite).stream().toList();
                List<File> allowWriteFiles = new ArrayList<>();
                files.forEach(path -> allowWriteFiles.add(new File(path)));
                opts.allowRead(allowWriteFiles);
            }

            if (configObj.containsKey("disallowWrite")) {
                Object disallowWrite = configObj.get("disallowWrite");
                if (!(disallowWrite instanceof JSONArray))
                    throw new RuntimeException("If provided in the configuration file, \"disallowWrite\" must be a list of strings (which represents paths)");

                @SuppressWarnings("unchecked")
                List<String> files = (List<String>) ((JSONArray) disallowWrite).stream().toList();
                List<File> disallowWriteFiles = new ArrayList<>();
                files.forEach(path -> disallowWriteFiles.add(new File(path)));
                opts.allowRead(disallowWriteFiles);
            }
        } catch (ParseException e) {
            throw new RuntimeException("There was an error parsing the configuration file", e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Specified configuration file does not exist", e);
        } catch (IOException e) {
            throw new RuntimeException("An unknown error occurred while attempting to parse the specified configuration file", e);
        }

        return opts;
    }

    /**
     * Execute a file.
     *
     * @param path The  path to the file to execute.
     * @return The value returned by the file. Will be {@link Value#NULL} if the file does not return anything.
     */
    public Value executeFile(Path path) throws IOException {
        Lexer lexer = new Lexer(path);
        return execLexer(lexer);
    }

    /**
     * Execute a file.
     *
     * @param file The file to execute.
     * @return The value returned by the file. Will be {@link Value#NULL} if the file does not return anything.
     */
    public Value executeFile(File file) throws IOException {
        return executeFile(file.toPath());
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
        ExecutionContext ctx = createContext();
        try {
            List<Token> tokens = lexer.analyze();
            TokenStorage tokenStorage = new TokenStorage(tokens);

            Parser parser = new Parser(tokenStorage);
            parser.parse(true, true);

            return parser.execute(ctx).returnValue();
        } catch (Throwable e) {
            if (!(e instanceof ExitException) || ((ExitException) e).getCode() != 0) {
                ctx.getFileTracker().undoOperations();
            } else
                return ((ExitException) e).getValue();

            throw e;
        }
    }

    /**
     * Create a new execution context with the provided options.
     *
     * @return A new execution context configured with the given options.
     */
    private ExecutionContext createContext() {
        Map<String, Value> envVars = options.envVariables;
        envVars.put("@__executor", new Value(DataType.STRING, options.executor, false));

        options.updateRestrictions();
        return new ExecutionContext(envVars, options.functions, options.root, options.operationConfig);
    }
}
