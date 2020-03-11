/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.AbstractPlantBlock;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineLogic;
import net.minecraft.util.math.Direction;

public class WeepingVinesPlantBlock
extends AbstractPlantBlock {
    public WeepingVinesPlantBlock(Block.Settings settings) {
        super(settings, Direction.DOWN, VineLogic.STEM_OUTLINE_SHAPE, false);
    }

    @Override
    protected AbstractPlantStemBlock getStem() {
        return (AbstractPlantStemBlock)Blocks.WEEPING_VINES;
    }
}

