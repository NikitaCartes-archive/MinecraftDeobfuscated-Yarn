/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class RedstoneOreBlock
extends Block {
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    public RedstoneOreBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)this.getDefaultState().with(LIT, false));
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        RedstoneOreBlock.light(state, world, pos);
        super.onBlockBreakStart(state, world, pos, player);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!entity.bypassesSteppingEffects()) {
            RedstoneOreBlock.light(state, world, pos);
        }
        super.onSteppedOn(world, pos, state, entity);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            RedstoneOreBlock.spawnParticles(world, pos);
        } else {
            RedstoneOreBlock.light(state, world, pos);
        }
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.getItem() instanceof BlockItem && new ItemPlacementContext(player, hand, itemStack, hit).canPlace()) {
            return ActionResult.PASS;
        }
        return ActionResult.SUCCESS;
    }

    private static void light(BlockState state, World world, BlockPos pos) {
        RedstoneOreBlock.spawnParticles(world, pos);
        if (!state.get(LIT).booleanValue()) {
            world.setBlockState(pos, (BlockState)state.with(LIT, true), Block.NOTIFY_ALL);
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return state.get(LIT);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(LIT).booleanValue()) {
            world.setBlockState(pos, (BlockState)state.with(LIT, false), Block.NOTIFY_ALL);
        }
    }

    @Override
    public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
        super.onStacksDropped(state, world, pos, tool, dropExperience);
        if (dropExperience && EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, tool) == 0) {
            int i = 1 + world.random.nextInt(5);
            this.dropExperience(world, pos, i);
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(LIT).booleanValue()) {
            RedstoneOreBlock.spawnParticles(world, pos);
        }
    }

    private static void spawnParticles(World world, BlockPos pos) {
        double d = 0.5625;
        Random random = world.random;
        for (Direction direction : Direction.values()) {
            BlockPos blockPos = pos.offset(direction);
            if (world.getBlockState(blockPos).isOpaqueFullCube(world, blockPos)) continue;
            Direction.Axis axis = direction.getAxis();
            double e = axis == Direction.Axis.X ? 0.5 + 0.5625 * (double)direction.getOffsetX() : (double)random.nextFloat();
            double f = axis == Direction.Axis.Y ? 0.5 + 0.5625 * (double)direction.getOffsetY() : (double)random.nextFloat();
            double g = axis == Direction.Axis.Z ? 0.5 + 0.5625 * (double)direction.getOffsetZ() : (double)random.nextFloat();
            world.addParticle(DustParticleEffect.DEFAULT, (double)pos.getX() + e, (double)pos.getY() + f, (double)pos.getZ() + g, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }
}

