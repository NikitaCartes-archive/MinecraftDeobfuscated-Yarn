/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;

public class GlassBlock
extends AbstractGlassBlock {
    public GlassBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}

