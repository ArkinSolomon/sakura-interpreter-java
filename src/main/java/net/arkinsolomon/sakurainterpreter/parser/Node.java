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

package net.arkinsolomon.sakurainterpreter.parser;

import net.arkinsolomon.sakurainterpreter.execution.ExecutionContext;
import net.arkinsolomon.sakurainterpreter.execution.Value;
import net.arkinsolomon.sakurainterpreter.lexer.Token;

import java.util.Arrays;
import java.util.Objects;

/**
 * A node of the tree. An expression.
 */
public abstract class Node {

    protected final Token token;
    protected int childCount;
    protected Node[] children;
    private Node parent;

    /**
     * Create a new node with a specific amount of children.
     *
     * @param token      The token of the node.
     * @param childCount The amount of children in the node.
     */
    protected Node(Token token, int childCount) {
        this.childCount = childCount;
        this.token = token;
        children = new Node[childCount];
    }

    /**
     * Evaluate the node.
     *
     * @param ctx The current execution context.
     * @return The value of the node.
     */
    public abstract Value evaluate(ExecutionContext ctx);

    /**
     * Assign a value to this node.
     *
     * @param ctx The context in which to assign.
     * @param val The value which to assign.
     */
    public abstract void assign(ExecutionContext ctx, Value val);

    /**
     * Resize a node and copy all children that fit.
     *
     * @param newSize The new size of the node.
     */
    protected final void resize(int newSize) {
        Node[] newChildren = new Node[newSize];
        int loopSize = Math.min(newSize, childCount);
        if (loopSize >= 0)
            System.arraycopy(children, 0, newChildren, 0, loopSize);
        children = newChildren;
        childCount = newSize;
    }

    /**
     * Set the child of the tree.
     *
     * @param i     The index of the child to set.
     * @param child The child to set.
     */
    public final void setChild(int i, Node child) {
        if (i >= childCount)
            throw new IndexOutOfBoundsException("Index i is greater than children in node");
        children[i] = child;
        children[i].parent = this;
    }

    /**
     * Get the child of a node
     *
     * @param i The index of the child to get
     * @return The child node.
     */
    public final Node getChild(int i) {
        if (i >= childCount)
            throw new IndexOutOfBoundsException("Index %d is greater than children in node".formatted(i));
        return children[i];
    }

    /**
     * Find the index of a specific node.
     *
     * @return The index of the node in this node's children, or -1 if the node is not a child.
     */
    public final int findChild(Node node) {
        for (int i = 0; i < childCount; ++i) {
            if (children[i] == node)
                return i;
        }
        return -1;
    }


    /**
     * Insert value as the next child of the node.
     */
    public final void insertChild(Node child) {
        for (int i = 0; i < childCount; i++) {
            if (children[i] == null) {
                setChild(i, child);
                return;
            }
        }
        throw new IndexOutOfBoundsException("Node is full");
    }

    /**
     * Get the token that this node used to be created.
     *
     * @return The token that caused the creation of this node.
     */
    public Token getToken() {
        return token;
    }

    /**
     * Get the parent of the node.
     *
     * @return The parent node, or null if there is no parent.
     */
    public final Node getParent() {
        return this.parent;
    }

    /**
     * The precedence of the node.
     *
     * @return The precedence of the node.
     */
    public abstract int getPrecedence();

    /**
     * Check if the node and all it's children are full.
     */
    public final boolean isCompletelyFull() {
        if (!isFull())
            return false;

        for (Node child : children)
            if (!child.isCompletelyFull())
                return false;
        return true;
    }

    /**
     * Check if the node is full.
     *
     * @return True if all the node's children are not null.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public final boolean isFull() {
        return Arrays.stream(children).noneMatch(Objects::isNull);
    }

    /**
     * Print out this node and all of its children, with a starting indent of zero.
     */
    public final void print() {
        print(0);
    }

    /**
     * Print out this node and all its children.
     *
     * @param indentCount The indent for this node.
     */
    public final void print(int indentCount) {
        System.out.println("-".repeat(indentCount) + this);
        for (Node child : children) {
            if (child == null) {
                System.out.println("-".repeat(indentCount + 2) + "<NULL CHILD>");
                continue;
            }
            child.print(indentCount + 4);
        }
    }

    /**
     * True if the node can be a child of another node.
     *
     * @return True if the node can be a child of another node.
     */
    public boolean canBeChild() {
        return true;
    }

    @Override
    public String toString() {
        return "[" + getClass().getSimpleName() + "] " + (token == null ? "NO TOKEN" : token.toString());
    }
}
