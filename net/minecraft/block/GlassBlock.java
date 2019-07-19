/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;

public class GlassBlock
extends AbstractGlassBlock {
    public GlassBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public RenderLayer getRenderLayer() {
        return RenderLayer.CUTOUT;
    }
}

