/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TransparentBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.EntityPose;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class HoneyBlock
extends TransparentBlock {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);

    public HoneyBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        return SHAPE;
    }

    @Override
    public void onLandedUpon(World world, BlockPos blockPos, Entity entity, float f) {
        this.method_23358(world, blockPos, entity);
        if (entity.handleFallDamage(f, 0.2f)) {
            entity.playSound(this.soundGroup.getFallSound(), this.soundGroup.getVolume() * 0.5f, this.soundGroup.getPitch() * 0.75f);
        }
    }

    @Override
    public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
        if (this.method_23356(blockPos, entity)) {
            Vec3d vec3d = entity.getVelocity();
            if (vec3d.y < -0.05) {
                if (entity instanceof ServerPlayerEntity && vec3d.y < -0.127) {
                    Criterions.SLIDE_DOWN_BLOCK.test((ServerPlayerEntity)entity, world.getBlockState(blockPos));
                }
                entity.setVelocity(new Vec3d(vec3d.x, -0.05, vec3d.z));
            }
            entity.fallDistance = 0.0f;
            this.method_23357(world, blockPos, entity);
            if (world.getTime() % 10L == 0L) {
                entity.playSound(SoundEvents.BLOCK_HONEY_BLOCK_SLIDE, 1.0f, 1.0f);
            }
        }
        super.onEntityCollision(blockState, world, blockPos, entity);
    }

    private boolean method_23356(BlockPos blockPos, Entity entity) {
        if (entity.onGround) {
            return false;
        }
        if (entity.getY() > (double)blockPos.getY() + 0.9375 - 1.0E-7) {
            return false;
        }
        if (entity.getVelocity().y >= -0.04) {
            return false;
        }
        double d = Math.abs((double)blockPos.getX() + 0.5 - entity.getX());
        double e = Math.abs((double)blockPos.getZ() + 0.5 - entity.getZ());
        double f = 0.4375 + (double)(entity.getWidth() / 2.0f);
        return d + 1.0E-7 > f || e + 1.0E-7 > f;
    }

    private void method_23357(World world, BlockPos blockPos, Entity entity) {
        float f = entity.getDimensions((EntityPose)EntityPose.STANDING).width;
        this.method_23355(entity, world, blockPos, 1, ((double)world.random.nextFloat() - 0.5) * (double)f, world.random.nextFloat() / 2.0f, ((double)world.random.nextFloat() - 0.5) * (double)f, (double)world.random.nextFloat() - 0.5, world.random.nextFloat() - 1.0f, (double)world.random.nextFloat() - 0.5);
    }

    private void method_23358(World world, BlockPos blockPos, Entity entity) {
        float f = entity.getDimensions((EntityPose)EntityPose.STANDING).width;
        this.method_23355(entity, world, blockPos, 10, ((double)world.random.nextFloat() - 0.5) * (double)f, 0.0, ((double)world.random.nextFloat() - 0.5) * (double)f, (double)world.random.nextFloat() - 0.5, 0.5, (double)world.random.nextFloat() - 0.5);
    }

    private void method_23355(Entity entity, World world, BlockPos blockPos, int i, double d, double e, double f, double g, double h, double j) {
        BlockState blockState = world.getBlockState(new BlockPos(blockPos));
        for (int k = 0; k < i; ++k) {
            entity.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), entity.getX() + d, entity.getY() + e, entity.getZ() + f, g, h, j);
        }
    }
}

