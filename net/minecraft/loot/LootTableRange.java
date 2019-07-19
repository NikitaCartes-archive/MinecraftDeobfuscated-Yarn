/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot;

import java.util.Random;
import net.minecraft.util.Identifier;

public interface LootTableRange {
    public static final Identifier CONSTANT = new Identifier("constant");
    public static final Identifier UNIFORM = new Identifier("uniform");
    public static final Identifier BINOMIAL = new Identifier("binomial");

    public int next(Random var1);

    public Identifier getType();
}

