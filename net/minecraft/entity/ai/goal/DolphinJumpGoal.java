/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.DiveJumpingGoal;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class DolphinJumpGoal
extends DiveJumpingGoal {
    private static final int[] field_6474 = new int[]{0, 1, 4, 5, 6, 7};
    private final DolphinEntity dolphin;
    private final int chance;
    private boolean field_6473;

    public DolphinJumpGoal(DolphinEntity dolphinEntity, int i) {
        this.dolphin = dolphinEntity;
        this.chance = i;
    }

    @Override
    public boolean canStart() {
        if (this.dolphin.getRandom().nextInt(this.chance) != 0) {
            return false;
        }
        Direction direction = this.dolphin.getMovementDirection();
        int i = direction.getOffsetX();
        int j = direction.getOffsetZ();
        BlockPos blockPos = new BlockPos(this.dolphin);
        for (int k : field_6474) {
            if (this.isWater(blockPos, i, j, k) && this.isAirAbove(blockPos, i, j, k)) continue;
            return false;
        }
        return true;
    }

    private boolean isWater(BlockPos blockPos, int i, int j, int k) {
        BlockPos blockPos2 = blockPos.add(i * k, 0, j * k);
        return this.dolphin.world.getFluidState(blockPos2).matches(FluidTags.WATER) && !this.dolphin.world.getBlockState(blockPos2).getMaterial().blocksMovement();
    }

    private boolean isAirAbove(BlockPos blockPos, int i, int j, int k) {
        return this.dolphin.world.getBlockState(blockPos.add(i * k, 1, j * k)).isAir() && this.dolphin.world.getBlockState(blockPos.add(i * k, 2, j * k)).isAir();
    }

    @Override
    public boolean shouldContinue() {
        double d = this.dolphin.getVelocity().y;
        return !(d * d < (double)0.03f && this.dolphin.pitch != 0.0f && Math.abs(this.dolphin.pitch) < 10.0f && this.dolphin.isTouchingWater() || this.dolphin.onGround);
    }

    @Override
    public boolean canStop() {
        return false;
    }

    @Override
    public void start() {
        Direction direction = this.dolphin.getMovementDirection();
        this.dolphin.setVelocity(this.dolphin.getVelocity().add((double)direction.getOffsetX() * 0.6, 0.7, (double)direction.getOffsetZ() * 0.6));
        this.dolphin.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.dolphin.pitch = 0.0f;
    }

    @Override
    public void tick() {
        boolean bl = this.field_6473;
        if (!bl) {
            FluidState fluidState = this.dolphin.world.getFluidState(new BlockPos(this.dolphin));
            this.field_6473 = fluidState.matches(FluidTags.WATER);
        }
        if (this.field_6473 && !bl) {
            this.dolphin.playSound(SoundEvents.ENTITY_DOLPHIN_JUMP, 1.0f, 1.0f);
        }
        Vec3d vec3d = this.dolphin.getVelocity();
        if (vec3d.y * vec3d.y < (double)0.03f && this.dolphin.pitch != 0.0f) {
            this.dolphin.pitch = this.updatePitch(this.dolphin.pitch, 0.0f, 0.2f);
        } else {
            double d = Math.sqrt(Entity.squaredHorizontalLength(vec3d));
            double e = Math.signum(-vec3d.y) * Math.acos(d / vec3d.length()) * 57.2957763671875;
            this.dolphin.pitch = (float)e;
        }
    }
}

