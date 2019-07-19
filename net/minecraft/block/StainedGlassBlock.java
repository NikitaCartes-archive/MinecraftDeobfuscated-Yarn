/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Stainable;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.DyeColor;

public class StainedGlassBlock
extends AbstractGlassBlock
implements Stainable {
    private final DyeColor color;

    public StainedGlassBlock(DyeColor dyeColor, Block.Settings settings) {
        super(settings);
        this.color = dyeColor;
    }

    @Override
    public DyeColor getColor() {
        return this.color;
    }

    @Override
    public RenderLayer getRenderLayer() {
        return RenderLayer.TRANSLUCENT;
    }
}

