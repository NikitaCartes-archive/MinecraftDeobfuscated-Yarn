package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class InfestedBlock extends Block {
	private final Block regularBlock;
	private static final Map<Block, Block> REGULAR_TO_INFESTED = Maps.<Block, Block>newIdentityHashMap();

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

	private void spawnSilverfish(ServerWorld serverWorld, BlockPos pos) {
		SilverfishEntity silverfishEntity = EntityType.field_6125.create(serverWorld);
		silverfishEntity.refreshPositionAndAngles((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, 0.0F, 0.0F);
		serverWorld.spawnEntity(silverfishEntity);
		silverfishEntity.playSpawnEffects();
	}

	@Override
	public void onStacksDropped(BlockState state, ServerWorld serverWorld, BlockPos pos, ItemStack stack) {
		super.onStacksDropped(state, serverWorld, pos, stack);
		if (serverWorld.getGameRules().getBoolean(GameRules.DO_TILE_DROPS) && EnchantmentHelper.getLevel(Enchantments.field_9099, stack) == 0) {
			this.spawnSilverfish(serverWorld, pos);
		}
	}

	@Override
	public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
		if (world instanceof ServerWorld) {
			this.spawnSilverfish((ServerWorld)world, pos);
		}
	}

	public static BlockState fromRegularBlock(Block regularBlock) {
		return ((Block)REGULAR_TO_INFESTED.get(regularBlock)).getDefaultState();
	}
}
