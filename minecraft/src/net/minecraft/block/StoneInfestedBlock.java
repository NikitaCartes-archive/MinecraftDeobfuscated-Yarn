package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StoneInfestedBlock extends Block {
	private final Block field_11178;
	private static final Map<Block, Block> field_11179 = Maps.<Block, Block>newIdentityHashMap();

	public StoneInfestedBlock(Block block, Block.Settings settings) {
		super(settings);
		this.field_11178 = block;
		field_11179.put(block, this);
	}

	public Block method_10271() {
		return this.field_11178;
	}

	public static boolean method_10269(BlockState blockState) {
		return field_11179.containsKey(blockState.getBlock());
	}

	@Override
	public void onStacksDropped(BlockState blockState, World world, BlockPos blockPos, ItemStack itemStack) {
		super.onStacksDropped(blockState, world, blockPos, itemStack);
		if (!world.isRemote && world.getGameRules().getBoolean("doTileDrops") && EnchantmentHelper.getLevel(Enchantments.field_9099, itemStack) == 0) {
			SilverfishEntity silverfishEntity = new SilverfishEntity(world);
			silverfishEntity.setPositionAndAngles((double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, 0.0F, 0.0F);
			world.spawnEntity(silverfishEntity);
			silverfishEntity.method_5990();
		}
	}

	public static BlockState method_10270(Block block) {
		return ((Block)field_11179.get(block)).getDefaultState();
	}
}
