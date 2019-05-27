package net.minecraft.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ShovelItem extends MiningToolItem {
	private static final Set<Block> EFFECTIVE_BLOCKS = Sets.<Block>newHashSet(
		Blocks.field_10460,
		Blocks.field_10566,
		Blocks.field_10253,
		Blocks.field_10520,
		Blocks.field_10362,
		Blocks.field_10219,
		Blocks.field_10255,
		Blocks.field_10402,
		Blocks.field_10102,
		Blocks.field_10534,
		Blocks.field_10491,
		Blocks.field_10477,
		Blocks.field_10114,
		Blocks.field_10194,
		Blocks.field_10197,
		Blocks.field_10022,
		Blocks.field_10300,
		Blocks.field_10321,
		Blocks.field_10145,
		Blocks.field_10133,
		Blocks.field_10522,
		Blocks.field_10353,
		Blocks.field_10628,
		Blocks.field_10233,
		Blocks.field_10404,
		Blocks.field_10456,
		Blocks.field_10023,
		Blocks.field_10529,
		Blocks.field_10287,
		Blocks.field_10506
	);
	protected static final Map<Block, BlockState> PATH_BLOCKSTATES = Maps.<Block, BlockState>newHashMap(
		ImmutableMap.of(Blocks.field_10219, Blocks.field_10194.getDefaultState())
	);

	public ShovelItem(ToolMaterial toolMaterial, float f, float g, Item.Settings settings) {
		super(f, g, toolMaterial, EFFECTIVE_BLOCKS, settings);
	}

	@Override
	public boolean isEffectiveOn(BlockState blockState) {
		Block block = blockState.getBlock();
		return block == Blocks.field_10477 || block == Blocks.field_10491;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getBlockPos();
		if (itemUsageContext.getSide() != Direction.field_11033 && world.getBlockState(blockPos.up()).isAir()) {
			BlockState blockState = (BlockState)PATH_BLOCKSTATES.get(world.getBlockState(blockPos).getBlock());
			if (blockState != null) {
				PlayerEntity playerEntity = itemUsageContext.getPlayer();
				world.playSound(playerEntity, blockPos, SoundEvents.field_14616, SoundCategory.field_15245, 1.0F, 1.0F);
				if (!world.isClient) {
					world.setBlockState(blockPos, blockState, 11);
					if (playerEntity != null) {
						itemUsageContext.getStack().damage(1, playerEntity, playerEntityx -> playerEntityx.sendToolBreakStatus(itemUsageContext.getHand()));
					}
				}

				return ActionResult.field_5812;
			}
		}

		return ActionResult.field_5811;
	}
}
