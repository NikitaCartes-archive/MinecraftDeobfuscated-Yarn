/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument;

import net.minecraft.command.argument.DecoratableArgumentType;
import net.minecraft.text.Text;

public interface TextConvertibleArgumentType<T>
extends DecoratableArgumentType<T> {
    public Text toText(T var1);
}

