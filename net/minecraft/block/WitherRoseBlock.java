/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class WitherRoseBlock
extends FlowerBlock {
    public WitherRoseBlock(StatusEffect statusEffect, Block.Settings settings) {
        super(statusEffect, 8, settings);
    }

    @Override
    protected boolean canPlantOnTop(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        Block block = blockState.getBlock();
        return super.canPlantOnTop(blockState, blockView, blockPos) || block == Blocks.NETHERRACK || block == Blocks.SOUL_SAND;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        VoxelShape voxelShape = this.getOutlineShape(blockState, world, blockPos, EntityContext.absent());
        Vec3d vec3d = voxelShape.getBoundingBox().getCenter();
        double d = (double)blockPos.getX() + vec3d.x;
        double e = (double)blockPos.getZ() + vec3d.z;
        for (int i = 0; i < 3; ++i) {
            if (!random.nextBoolean()) continue;
            world.addParticle(ParticleTypes.SMOKE, d + (double)(random.nextFloat() / 5.0f), (double)blockPos.getY() + (0.5 - (double)random.nextFloat()), e + (double)(random.nextFloat() / 5.0f), 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
        LivingEntity livingEntity;
        if (world.isClient || world.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        if (entity instanceof LivingEntity && !(livingEntity = (LivingEntity)entity).isInvulnerableTo(DamageSource.WITHER)) {
            livingEntity.addPotionEffect(new StatusEffectInstance(StatusEffects.WITHER, 40));
        }
    }
}

