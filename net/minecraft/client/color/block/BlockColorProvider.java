/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.color.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface BlockColorProvider {
    public int getColor(BlockState var1, @Nullable ExtendedBlockView var2, @Nullable BlockPos var3, int var4);
}

