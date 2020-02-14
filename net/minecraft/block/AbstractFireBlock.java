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
import net.minecraft.block.FireBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class AbstractFireBlock
extends Block {
    private final float damage;

    public AbstractFireBlock(Block.Settings settings, float damage) {
        super(settings);
        this.damage = damage;
    }

    public static BlockState getState(BlockView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.getBlock() == Blocks.SOUL_SOIL) {
            return Blocks.SOUL_FIRE.getDefaultState();
        }
        return ((FireBlock)Blocks.FIRE).getStateForPosition(world, pos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, EntityContext context) {
        return VoxelShapes.empty();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        block12: {
            double f;
            double e;
            double d;
            int i;
            block11: {
                BlockPos blockPos;
                BlockState blockState;
                if (random.nextInt(24) == 0) {
                    world.playSound((float)pos.getX() + 0.5f, (double)((float)pos.getY() + 0.5f), (double)((float)pos.getZ() + 0.5f), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0f + random.nextFloat(), random.nextFloat() * 0.7f + 0.3f, false);
                }
                if (!this.isFlammable(blockState = world.getBlockState(blockPos = pos.down())) && !blockState.isSideSolidFullSquare(world, blockPos, Direction.UP)) break block11;
                for (int i2 = 0; i2 < 3; ++i2) {
                    double d2 = (double)pos.getX() + random.nextDouble();
                    double e2 = (double)pos.getY() + random.nextDouble() * 0.5 + 0.5;
                    double f2 = (double)pos.getZ() + random.nextDouble();
                    world.addParticle(ParticleTypes.LARGE_SMOKE, d2, e2, f2, 0.0, 0.0, 0.0);
                }
                break block12;
            }
            if (this.isFlammable(world.getBlockState(pos.west()))) {
                for (i = 0; i < 2; ++i) {
                    d = (double)pos.getX() + random.nextDouble() * (double)0.1f;
                    e = (double)pos.getY() + random.nextDouble();
                    f = (double)pos.getZ() + random.nextDouble();
                    world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
                }
            }
            if (this.isFlammable(world.getBlockState(pos.east()))) {
                for (i = 0; i < 2; ++i) {
                    d = (double)(pos.getX() + 1) - random.nextDouble() * (double)0.1f;
                    e = (double)pos.getY() + random.nextDouble();
                    f = (double)pos.getZ() + random.nextDouble();
                    world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
                }
            }
            if (this.isFlammable(world.getBlockState(pos.north()))) {
                for (i = 0; i < 2; ++i) {
                    d = (double)pos.getX() + random.nextDouble();
                    e = (double)pos.getY() + random.nextDouble();
                    f = (double)pos.getZ() + random.nextDouble() * (double)0.1f;
                    world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
                }
            }
            if (this.isFlammable(world.getBlockState(pos.south()))) {
                for (i = 0; i < 2; ++i) {
                    d = (double)pos.getX() + random.nextDouble();
                    e = (double)pos.getY() + random.nextDouble();
                    f = (double)(pos.getZ() + 1) - random.nextDouble() * (double)0.1f;
                    world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
                }
            }
            if (!this.isFlammable(world.getBlockState(pos.up()))) break block12;
            for (i = 0; i < 2; ++i) {
                d = (double)pos.getX() + random.nextDouble();
                e = (double)(pos.getY() + 1) - random.nextDouble() * (double)0.1f;
                f = (double)pos.getZ() + random.nextDouble();
                world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
            }
        }
    }

    protected abstract boolean isFlammable(BlockState var1);

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!(entity.isFireImmune() || entity instanceof LivingEntity && EnchantmentHelper.hasFrostWalker((LivingEntity)entity) || entity.isTouchingWaterOrRain())) {
            entity.setFireTicks(entity.getFireTicks() + 1);
            if (entity.getFireTicks() == 0) {
                entity.setOnFireFor(8);
            }
            entity.damage(DamageSource.IN_FIRE, this.damage);
        }
        super.onEntityCollision(state, world, pos, entity);
    }
}

