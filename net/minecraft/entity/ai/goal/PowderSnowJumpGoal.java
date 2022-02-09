/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

public class PowderSnowJumpGoal
extends Goal {
    private final MobEntity entity;
    private final World world;

    public PowderSnowJumpGoal(MobEntity entity, World world) {
        this.entity = entity;
        this.world = world;
        this.setControls(EnumSet.of(Goal.Control.JUMP));
    }

    @Override
    public boolean canStart() {
        boolean bl;
        boolean bl2 = bl = this.entity.wasInPowderSnow || this.entity.inPowderSnow;
        if (!bl || !this.entity.getType().isIn(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS)) {
            return false;
        }
        BlockPos blockPos = this.entity.getBlockPos().up();
        BlockState blockState = this.world.getBlockState(blockPos);
        return blockState.isOf(Blocks.POWDER_SNOW) || blockState.getCollisionShape(this.world, blockPos) == VoxelShapes.empty();
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.entity.getJumpControl().setActive();
    }
}

