/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ContainerFactory {
    @Nullable
    public Container createMenu(int var1, PlayerInventory var2, PlayerEntity var3);
}

