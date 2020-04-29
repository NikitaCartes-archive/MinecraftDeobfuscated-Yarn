package net.minecraft.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ShovelItem extends MiningToolItem {
	private static final Set<Block> EFFECTIVE_BLOCKS = Sets.<Block>newHashSet(
		Blocks.CLAY,
		Blocks.DIRT,
		Blocks.COARSE_DIRT,
		Blocks.PODZOL,
		Blocks.FARMLAND,
		Blocks.GRASS_BLOCK,
		Blocks.GRAVEL,
		Blocks.MYCELIUM,
		Blocks.SAND,
		Blocks.RED_SAND,
		Blocks.SNOW_BLOCK,
		Blocks.SNOW,
		Blocks.SOUL_SAND,
		Blocks.GRASS_PATH,
		Blocks.WHITE_CONCRETE_POWDER,
		Blocks.ORANGE_CONCRETE_POWDER,
		Blocks.MAGENTA_CONCRETE_POWDER,
		Blocks.LIGHT_BLUE_CONCRETE_POWDER,
		Blocks.YELLOW_CONCRETE_POWDER,
		Blocks.LIME_CONCRETE_POWDER,
		Blocks.PINK_CONCRETE_POWDER,
		Blocks.GRAY_CONCRETE_POWDER,
		Blocks.LIGHT_GRAY_CONCRETE_POWDER,
		Blocks.CYAN_CONCRETE_POWDER,
		Blocks.PURPLE_CONCRETE_POWDER,
		Blocks.BLUE_CONCRETE_POWDER,
		Blocks.BROWN_CONCRETE_POWDER,
		Blocks.GREEN_CONCRETE_POWDER,
		Blocks.RED_CONCRETE_POWDER,
		Blocks.BLACK_CONCRETE_POWDER,
		Blocks.SOUL_SOIL
	);
	protected static final Map<Block, BlockState> PATH_BLOCKSTATES = Maps.<Block, BlockState>newHashMap(
		ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.GRASS_PATH.getDefaultState())
	);

	public ShovelItem(ToolMaterial material, float attackDamage, float attackSpeed, Item.Settings settings) {
		super(attackDamage, attackSpeed, material, EFFECTIVE_BLOCKS, settings);
	}

	@Override
	public boolean isEffectiveOn(BlockState state) {
		return state.isOf(Blocks.SNOW) || state.isOf(Blocks.SNOW_BLOCK);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (context.getSide() == Direction.DOWN) {
			return ActionResult.PASS;
		} else {
			PlayerEntity playerEntity = context.getPlayer();
			BlockState blockState2 = (BlockState)PATH_BLOCKSTATES.get(blockState.getBlock());
			BlockState blockState3 = null;
			if (blockState2 != null && world.getBlockState(blockPos.up()).isAir()) {
				world.playSound(playerEntity, blockPos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
				blockState3 = blockState2;
			} else if (blockState.getBlock() instanceof CampfireBlock && (Boolean)blockState.get(CampfireBlock.LIT)) {
				if (!world.isClient()) {
					world.syncWorldEvent(null, 1009, blockPos, 0);
				}

				blockState3 = blockState.with(CampfireBlock.LIT, Boolean.valueOf(false));
			}

			if (blockState3 != null) {
				if (!world.isClient) {
					world.setBlockState(blockPos, blockState3, 11);
					if (playerEntity != null) {
						context.getStack().damage(1, playerEntity, p -> p.sendToolBreakStatus(context.getHand()));
					}
				}

				return ActionResult.SUCCESS;
			} else {
				return ActionResult.PASS;
			}
		}
	}
}
