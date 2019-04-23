/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;

public abstract class FacingBlock
extends Block {
    public static final DirectionProperty FACING = Properties.FACING;

    protected FacingBlock(Block.Settings settings) {
        super(settings);
    }
}

