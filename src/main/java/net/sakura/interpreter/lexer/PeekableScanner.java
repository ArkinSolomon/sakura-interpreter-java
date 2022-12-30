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

package net.sakura.interpreter.lexer;

import java.util.Scanner;

// See http://www.javased.com/?post=4288643
// TODO javadoc
public class PeekableScanner
{
    private Scanner scan1;
    private Scanner scan2;
    private String next;

    public PeekableScanner( String source )
    {
        scan1 = new Scanner(source);
        scan1.useDelimiter("");
        scan2 = new Scanner(source);
        scan2.useDelimiter("");
        next = scan2.next();
    }

    public boolean hasNext()
    {
        return scan1.hasNext();
    }

    public String next()
    {
        next = (scan2.hasNext() ? scan2.next() : null);
        return scan1.next();
    }

    public String peek()
    {
        return next;
    }
}