/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import java.util.stream.Stream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.NbtDataSource;
import net.minecraft.util.Identifier;

public record StorageNbtDataSource(Identifier id) implements NbtDataSource
{
    @Override
    public Stream<NbtCompound> get(ServerCommandSource source) {
        NbtCompound nbtCompound = source.getServer().getDataCommandStorage().get(this.id);
        return Stream.of(nbtCompound);
    }

    @Override
    public String toString() {
        return "storage=" + this.id;
    }
}

