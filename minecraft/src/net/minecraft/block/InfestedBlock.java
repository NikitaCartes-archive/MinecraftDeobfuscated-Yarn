package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class InfestedBlock extends Block {
	private final Block regularBlock;
	private static final Map<Block, Block> REGULAR_TO_INFESTED = Maps.<Block, Block>newIdentityHashMap();

	public InfestedBlock(Block regularBlock, Block.Settings settings) {
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

	@Override
	public void onStacksDropped(BlockState state, World world, BlockPos pos, ItemStack stack) {
		super.onStacksDropped(state, world, pos, stack);
		if (!world.isClient && world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS) && EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) == 0) {
			SilverfishEntity silverfishEntity = EntityType.SILVERFISH.create(world);
			silverfishEntity.setPositionAndAngles((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, 0.0F, 0.0F);
			world.spawnEntity(silverfishEntity);
			silverfishEntity.playSpawnEffects();
		}
	}

	public static BlockState fromRegularBlock(Block regularBlock) {
		return ((Block)REGULAR_TO_INFESTED.get(regularBlock)).getDefaultState();
	}
}
