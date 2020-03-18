/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Optional;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;

public class RespawnAnchorBlock
extends Block {
    public static final IntProperty CHARGES = Properties.CHARGES;

    public RespawnAnchorBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(CHARGES, 0));
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        world.getBlockTickScheduler().schedule(pos, state.getBlock(), 20);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        int i = state.get(CHARGES);
        if (player.getStackInHand(hand).getItem() == Items.GLOWSTONE) {
            if (i < 4) {
                world.setBlockState(pos, (BlockState)state.with(CHARGES, i + 1), 3);
                world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                return ActionResult.SUCCESS;
            }
            return ActionResult.CONSUME;
        }
        if (i == 0) {
            return ActionResult.CONSUME;
        }
        if (world.dimension.getType() == DimensionType.THE_NETHER) {
            if (!world.isClient) {
                ((ServerPlayerEntity)player).setSpawnPoint(world.dimension.getType(), pos, false, true);
            }
            world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
        } else {
            world.removeBlock(pos, false);
            world.createExplosion(null, DamageSource.netherBed(), (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 5.0f, true, Explosion.DestructionType.DESTROY);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(CHARGES) == 0) {
            return;
        }
        if (random.nextInt(100) == 0) {
            world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_RESPAWN_ANCHOR_AMBIENT, SoundCategory.BLOCKS, 0.5f, random.nextFloat() * 0.4f + 0.8f, false);
        }
        double d = (double)pos.getX() + 0.5 + (double)(0.5f - random.nextFloat());
        double e = (double)pos.getY() + 1.0;
        double f = (double)pos.getZ() + 0.5 + (double)(0.5f - random.nextFloat());
        double g = (double)random.nextFloat() * 0.04;
        world.addParticle(ParticleTypes.REVERSE_PORTAL, d, e, f, 0.0, g, 0.0);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CHARGES);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    public static int getLightLevel(BlockState state, int maxLevel) {
        return MathHelper.floor((float)(state.get(CHARGES) - 0) / 4.0f * (float)maxLevel);
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return RespawnAnchorBlock.getLightLevel(state, 15);
    }

    public static Optional<Vec3d> findRespawnPosition(WorldView world, BlockPos pos) {
        for (BlockPos blockPos : BlockPos.iterate(pos.add(-1, 0, -1), pos.add(1, 0, 1))) {
            BlockPos blockPos2 = blockPos.down();
            BlockPos blockPos3 = blockPos.up();
            if (!world.getBlockState(blockPos2).isSideSolidFullSquare(world, blockPos2, Direction.DOWN) || !world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty() || !world.getBlockState(blockPos3).getCollisionShape(world, blockPos3).isEmpty()) continue;
            return Optional.of(Vec3d.method_24955(blockPos));
        }
        return Optional.empty();
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }
}

