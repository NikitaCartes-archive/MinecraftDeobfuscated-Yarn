/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TransparentBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
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

    private static boolean hasHoneyBlockEffects(Entity entity) {
        return entity instanceof LivingEntity || entity instanceof AbstractMinecartEntity || entity instanceof TntEntity || entity instanceof BoatEntity;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        return SHAPE;
    }

    @Override
    public void onLandedUpon(World world, BlockPos blockPos, Entity entity, float f) {
        entity.playSound(SoundEvents.BLOCK_HONEY_BLOCK_SLIDE, 1.0f, 1.0f);
        if (!world.isClient) {
            world.sendEntityStatus(entity, (byte)54);
        }
        if (entity.handleFallDamage(f, 0.2f)) {
            entity.playSound(this.soundGroup.getFallSound(), this.soundGroup.getVolume() * 0.5f, this.soundGroup.getPitch() * 0.75f);
        }
    }

    @Override
    public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
        if (this.isSliding(blockPos, entity)) {
            this.triggerAdvancement(entity, blockPos);
            this.updateSlidingVelocity(entity);
            this.addCollisionEffects(world, entity);
        }
        super.onEntityCollision(blockState, world, blockPos, entity);
    }

    private boolean isSliding(BlockPos blockPos, Entity entity) {
        if (entity.onGround) {
            return false;
        }
        if (entity.getY() > (double)blockPos.getY() + 0.9375 - 1.0E-7) {
            return false;
        }
        if (entity.getVelocity().y >= -0.08) {
            return false;
        }
        double d = Math.abs((double)blockPos.getX() + 0.5 - entity.getX());
        double e = Math.abs((double)blockPos.getZ() + 0.5 - entity.getZ());
        double f = 0.4375 + (double)(entity.getWidth() / 2.0f);
        return d + 1.0E-7 > f || e + 1.0E-7 > f;
    }

    private void triggerAdvancement(Entity entity, BlockPos blockPos) {
        if (entity instanceof ServerPlayerEntity && entity.world.getTime() % 20L == 0L) {
            Criterions.SLIDE_DOWN_BLOCK.test((ServerPlayerEntity)entity, entity.world.getBlockState(blockPos));
        }
    }

    private void updateSlidingVelocity(Entity entity) {
        Vec3d vec3d = entity.getVelocity();
        if (vec3d.y < -0.13) {
            double d = -0.05 / vec3d.y;
            entity.setVelocity(new Vec3d(vec3d.x * d, -0.05, vec3d.z * d));
        } else {
            entity.setVelocity(new Vec3d(vec3d.x, -0.05, vec3d.z));
        }
        entity.fallDistance = 0.0f;
    }

    private void addCollisionEffects(World world, Entity entity) {
        if (HoneyBlock.hasHoneyBlockEffects(entity)) {
            if (world.random.nextInt(5) == 0) {
                entity.playSound(SoundEvents.BLOCK_HONEY_BLOCK_SLIDE, 1.0f, 1.0f);
            }
            if (!world.isClient && world.random.nextInt(5) == 0) {
                world.sendEntityStatus(entity, (byte)53);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static void addRegularParticles(Entity entity) {
        HoneyBlock.addParticles(entity, 5);
    }

    @Environment(value=EnvType.CLIENT)
    public static void addRichParticles(Entity entity) {
        HoneyBlock.addParticles(entity, 10);
    }

    @Environment(value=EnvType.CLIENT)
    private static void addParticles(Entity entity, int i) {
        if (!entity.world.isClient) {
            return;
        }
        BlockState blockState = Blocks.HONEY_BLOCK.getDefaultState();
        for (int j = 0; j < i; ++j) {
            entity.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), entity.getX(), entity.getY(), entity.getZ(), 0.0, 0.0, 0.0);
        }
    }
}

