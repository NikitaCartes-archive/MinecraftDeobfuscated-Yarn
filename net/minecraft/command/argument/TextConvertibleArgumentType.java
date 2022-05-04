/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.text.Text;

public interface TextConvertibleArgumentType<T>
extends ArgumentType<T> {
    public Text toText(T var1);
}

