/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.util.math.BlockPos;

public class HangingSignBlockEntity
extends SignBlockEntity {
    private static final int MAX_TEXT_WIDTH = 50;
    private static final int TEXT_LINE_HEIGHT = 8;

    public HangingSignBlockEntity(BlockPos blockPos, BlockState blockState) {
        super((BlockEntityType)BlockEntityType.HANGING_SIGN, blockPos, blockState);
    }

    @Override
    public int getTextLineHeight() {
        return 8;
    }

    @Override
    public int getMaxTextWidth() {
        return 50;
    }
}

