package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InfestedBlock extends Block {
	private final Block regularBlock;
	private static final Map<Block, Block> INFESTED_TO_REGULAR = Maps.<Block, Block>newIdentityHashMap();

	public InfestedBlock(Block block, Block.Settings settings) {
		super(settings);
		this.regularBlock = block;
		INFESTED_TO_REGULAR.put(block, this);
	}

	public Block getRegularBlock() {
		return this.regularBlock;
	}

	public static boolean hasRegularBlock(BlockState blockState) {
		return INFESTED_TO_REGULAR.containsKey(blockState.getBlock());
	}

	@Override
	public void onStacksDropped(BlockState blockState, World world, BlockPos blockPos, ItemStack itemStack) {
		super.onStacksDropped(blockState, world, blockPos, itemStack);
		if (!world.isClient && world.getGameRules().getBoolean("doTileDrops") && EnchantmentHelper.getLevel(Enchantments.field_9099, itemStack) == 0) {
			SilverfishEntity silverfishEntity = EntityType.SILVERFISH.create(world);
			silverfishEntity.setPositionAndAngles((double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, 0.0F, 0.0F);
			world.spawnEntity(silverfishEntity);
			silverfishEntity.method_5990();
		}
	}

	public static BlockState getRegularBlock(Block block) {
		return ((Block)INFESTED_TO_REGULAR.get(block)).getDefaultState();
	}
}
