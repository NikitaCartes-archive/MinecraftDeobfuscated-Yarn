/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument;

import net.minecraft.command.argument.DecoratableArgumentType;

public interface SignedArgumentType<T>
extends DecoratableArgumentType<T> {
    public String toSignedString(T var1);
}

