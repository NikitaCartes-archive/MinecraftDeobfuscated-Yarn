/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.color.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface BlockColorProvider {
    /**
     * {@return the color of the block state for the specified tint index,
     * or -1 if not tinted}
     */
    public int getColor(BlockState var1, @Nullable BlockRenderView var2, @Nullable BlockPos var3, int var4);
}

