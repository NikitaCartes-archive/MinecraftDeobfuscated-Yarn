/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class InfestedBlock
extends Block {
    private final Block regularBlock;
    private static final Map<Block, Block> REGULAR_TO_INFESTED = Maps.newIdentityHashMap();
    private static final Map<BlockState, BlockState> field_33564 = Maps.newIdentityHashMap();
    private static final Map<BlockState, BlockState> field_33565 = Maps.newIdentityHashMap();

    /**
     * Creates an infested block
     * 
     * @param regularBlock the block this infested block should mimic
     * @param settings block settings
     */
    public InfestedBlock(Block regularBlock, AbstractBlock.Settings settings) {
        super(settings);
        this.regularBlock = regularBlock;
        REGULAR_TO_INFESTED.put(regularBlock, this);
    }

    public Block getRegularBlock() {
        return this.regularBlock;
    }

    public static boolean isInfestable(BlockState block) {
        return REGULAR_TO_INFESTED.containsKey(block.getBlock());
    }

    private void spawnSilverfish(ServerWorld world, BlockPos pos) {
        SilverfishEntity silverfishEntity = EntityType.SILVERFISH.create(world);
        silverfishEntity.refreshPositionAndAngles((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5, 0.0f, 0.0f);
        world.spawnEntity(silverfishEntity);
        silverfishEntity.playSpawnEffects();
    }

    @Override
    public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack) {
        super.onStacksDropped(state, world, pos, stack);
        if (world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS) && EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) == 0) {
            this.spawnSilverfish(world, pos);
        }
    }

    @Override
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        if (world instanceof ServerWorld) {
            this.spawnSilverfish((ServerWorld)world, pos);
        }
    }

    public static BlockState method_36366(BlockState blockState) {
        return InfestedBlock.method_36363(field_33564, blockState, () -> REGULAR_TO_INFESTED.get(blockState.getBlock()).getDefaultState());
    }

    public BlockState fromRegularBlock(BlockState blockState) {
        return InfestedBlock.method_36363(field_33565, blockState, () -> this.getRegularBlock().getDefaultState());
    }

    private static BlockState method_36363(Map<BlockState, BlockState> map, BlockState blockState2, Supplier<BlockState> supplier) {
        return map.computeIfAbsent(blockState2, blockState -> {
            BlockState blockState2 = (BlockState)supplier.get();
            for (Property<?> property : blockState.getProperties()) {
                blockState2 = blockState2.contains(property) ? (BlockState)blockState2.with(property, blockState.get(property)) : blockState2;
            }
            return blockState2;
        });
    }
}

