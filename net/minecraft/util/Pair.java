/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

public class Pair<A, B> {
    private A left;
    private B right;

    public Pair(A object, B object2) {
        this.left = object;
        this.right = object2;
    }

    public A getLeft() {
        return this.left;
    }

    public B getRight() {
        return this.right;
    }
}

