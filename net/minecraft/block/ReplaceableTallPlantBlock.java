/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.EnumProperty;

public class ReplaceableTallPlantBlock
extends TallPlantBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF = TallPlantBlock.HALF;

    public ReplaceableTallPlantBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext ctx) {
        boolean bl = super.canReplace(state, ctx);
        if (bl && ctx.getStack().getItem() == this.asItem()) {
            return false;
        }
        return bl;
    }
}

