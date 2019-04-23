/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InfestedBlock
extends Block {
    private final Block regularBlock;
    private static final Map<Block, Block> REGULAR_TO_INFESTED = Maps.newIdentityHashMap();

    public InfestedBlock(Block block, Block.Settings settings) {
        super(settings);
        this.regularBlock = block;
        REGULAR_TO_INFESTED.put(block, this);
    }

    public Block getRegularBlock() {
        return this.regularBlock;
    }

    public static boolean isInfestable(BlockState blockState) {
        return REGULAR_TO_INFESTED.containsKey(blockState.getBlock());
    }

    @Override
    public void onStacksDropped(BlockState blockState, World world, BlockPos blockPos, ItemStack itemStack) {
        super.onStacksDropped(blockState, world, blockPos, itemStack);
        if (!world.isClient && world.getGameRules().getBoolean("doTileDrops") && EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, itemStack) == 0) {
            SilverfishEntity silverfishEntity = EntityType.SILVERFISH.create(world);
            silverfishEntity.setPositionAndAngles((double)blockPos.getX() + 0.5, blockPos.getY(), (double)blockPos.getZ() + 0.5, 0.0f, 0.0f);
            world.spawnEntity(silverfishEntity);
            silverfishEntity.playSpawnEffects();
        }
    }

    public static BlockState fromRegularBlock(Block block) {
        return REGULAR_TO_INFESTED.get(block).getDefaultState();
    }
}

