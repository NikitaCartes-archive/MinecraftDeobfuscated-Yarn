/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Optional;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.class_5360;
import net.minecraft.class_5362;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
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
import net.minecraft.world.explosion.Explosion;

public class RespawnAnchorBlock
extends Block {
    public static final IntProperty CHARGES = Properties.CHARGES;

    public RespawnAnchorBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(CHARGES, 0));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (hand == Hand.MAIN_HAND && !RespawnAnchorBlock.isChargeItem(itemStack) && RespawnAnchorBlock.isChargeItem(player.getStackInHand(Hand.OFF_HAND))) {
            return ActionResult.PASS;
        }
        if (RespawnAnchorBlock.isChargeItem(itemStack) && RespawnAnchorBlock.canCharge(state)) {
            RespawnAnchorBlock.charge(world, pos, state);
            if (!player.abilities.creativeMode) {
                itemStack.decrement(1);
            }
            return ActionResult.method_29236(world.isClient);
        }
        if (state.get(CHARGES) == 0) {
            return ActionResult.PASS;
        }
        if (RespawnAnchorBlock.isNether(world)) {
            ServerPlayerEntity serverPlayerEntity;
            if (!(world.isClient || (serverPlayerEntity = (ServerPlayerEntity)player).getSpawnPointDimension() == world.getRegistryKey() && serverPlayerEntity.getSpawnPointPosition().equals(pos))) {
                serverPlayerEntity.setSpawnPoint(world.getRegistryKey(), pos, false, true);
                world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, SoundCategory.BLOCKS, 1.0f, 1.0f);
                return ActionResult.SUCCESS;
            }
            return RespawnAnchorBlock.canCharge(state) ? ActionResult.PASS : ActionResult.CONSUME;
        }
        if (!world.isClient) {
            this.method_29561(state, world, pos);
        }
        return ActionResult.method_29236(world.isClient);
    }

    private static boolean isChargeItem(ItemStack itemStack) {
        return itemStack.getItem() == Items.GLOWSTONE;
    }

    private static boolean canCharge(BlockState blockState) {
        return blockState.get(CHARGES) < 4;
    }

    private static boolean method_29560(BlockPos blockPos, World world) {
        FluidState fluidState = world.getFluidState(blockPos);
        if (!fluidState.matches(FluidTags.WATER)) {
            return false;
        }
        if (fluidState.isStill()) {
            return true;
        }
        float f = fluidState.getLevel();
        if (f < 2.0f) {
            return false;
        }
        FluidState fluidState2 = world.getFluidState(blockPos.down());
        return !fluidState2.matches(FluidTags.WATER);
    }

    private void method_29561(BlockState blockState, World world, final BlockPos blockPos2) {
        world.removeBlock(blockPos2, false);
        boolean bl = Direction.Type.HORIZONTAL.method_29716().map(blockPos2::offset).anyMatch(blockPos -> RespawnAnchorBlock.method_29560(blockPos, world));
        final boolean bl2 = bl || world.getFluidState(blockPos2.up()).matches(FluidTags.WATER);
        class_5362 lv = new class_5362(){

            @Override
            public Optional<Float> method_29555(Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
                if (blockPos.equals(blockPos2) && bl2) {
                    return Optional.of(Float.valueOf(Blocks.WATER.getBlastResistance()));
                }
                return class_5360.field_25397.method_29555(explosion, blockView, blockPos, blockState, fluidState);
            }

            @Override
            public boolean method_29554(Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, float f) {
                return class_5360.field_25397.method_29554(explosion, blockView, blockPos, blockState, f);
            }
        };
        world.createExplosion(null, DamageSource.netherBed(), lv, (double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.5, (double)blockPos2.getZ() + 0.5, 5.0f, true, Explosion.DestructionType.DESTROY);
    }

    public static boolean isNether(World world) {
        return world.getDimension().isNether();
    }

    public static void charge(World world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, (BlockState)state.with(CHARGES, state.get(CHARGES) + 1), 3);
        world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(CHARGES) == 0) {
            return;
        }
        if (random.nextInt(100) == 0) {
            world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_RESPAWN_ANCHOR_AMBIENT, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        double d = (double)pos.getX() + 0.5 + (0.5 - random.nextDouble());
        double e = (double)pos.getY() + 1.0;
        double f = (double)pos.getZ() + 0.5 + (0.5 - random.nextDouble());
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

    public static Optional<Vec3d> findRespawnPosition(EntityType<?> entity, WorldView world, BlockPos pos) {
        for (BlockPos blockPos : BlockPos.iterate(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
            Optional<Vec3d> optional = BedBlock.canWakeUpAt(entity, world, blockPos);
            if (!optional.isPresent()) continue;
            return optional;
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

