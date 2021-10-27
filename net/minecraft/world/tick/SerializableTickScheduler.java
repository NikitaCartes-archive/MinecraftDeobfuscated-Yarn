/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.tick;

import java.util.function.Function;
import net.minecraft.nbt.NbtElement;

public interface SerializableTickScheduler<T> {
    public NbtElement toNbt(long var1, Function<T, String> var3);
}

