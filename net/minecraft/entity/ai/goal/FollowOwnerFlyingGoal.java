/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

public class FollowOwnerFlyingGoal
extends FollowOwnerGoal {
    public FollowOwnerFlyingGoal(TameableEntity tameableEntity, double d, float f, float g) {
        super(tameableEntity, d, f, g);
    }

    @Override
    protected boolean method_6263(BlockPos blockPos) {
        BlockState blockState = this.world.getBlockState(blockPos);
        return (blockState.hasSolidTopSurface(this.world, blockPos, this.tameable) || blockState.matches(BlockTags.LEAVES)) && this.world.method_22347(blockPos.up()) && this.world.method_22347(blockPos.up(2));
    }
}

