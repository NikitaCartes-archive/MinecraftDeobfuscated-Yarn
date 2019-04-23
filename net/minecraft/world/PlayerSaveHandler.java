/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public interface PlayerSaveHandler {
    public void savePlayerData(PlayerEntity var1);

    @Nullable
    public CompoundTag loadPlayerData(PlayerEntity var1);
}

