/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;

@FunctionalInterface
public interface class_7419 {
    public Stream<NbtCompound> toNbt(ServerCommandSource var1) throws CommandSyntaxException;
}

