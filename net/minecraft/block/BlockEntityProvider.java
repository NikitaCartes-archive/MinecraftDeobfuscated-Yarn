/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public interface BlockEntityProvider {
    @Nullable
    public BlockEntity createBlockEntity(BlockView var1);
}

