package net.minecraft.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import net.minecraft.class_5508;
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
		Blocks.field_10506,
		Blocks.field_22090
	);
	protected static final Map<Block, BlockState> PATH_STATES = Maps.<Block, BlockState>newHashMap(
		ImmutableMap.of(Blocks.field_10219, Blocks.field_10194.getDefaultState())
	);

	public ShovelItem(ToolMaterial toolMaterial, Item.Settings settings) {
		super(toolMaterial, EFFECTIVE_BLOCKS, settings);
	}

	@Override
	public boolean isEffectiveOn(BlockState state) {
		return state.isOf(Blocks.field_10477) || state.isOf(Blocks.field_10491);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (context.getSide() == Direction.field_11033) {
			return ActionResult.PASS;
		} else {
			PlayerEntity playerEntity = context.getPlayer();
			BlockState blockState2 = (BlockState)PATH_STATES.get(blockState.getBlock());
			BlockState blockState3 = null;
			if (blockState2 != null && world.getBlockState(blockPos.up()).isAir()) {
				world.playSound(playerEntity, blockPos, SoundEvents.field_14616, SoundCategory.field_15245, 1.0F, 1.0F);
				blockState3 = blockState2;
			} else if (blockState.getBlock() instanceof CampfireBlock && (Boolean)blockState.get(CampfireBlock.LIT)) {
				if (!world.isClient()) {
					world.syncWorldEvent(null, 1009, blockPos, 0);
				}

				CampfireBlock.extinguish(world, blockPos, blockState);
				blockState3 = blockState.with(CampfireBlock.LIT, Boolean.valueOf(false));
			}

			if (blockState3 != null) {
				if (!world.isClient) {
					world.setBlockState(blockPos, blockState3, 11);
					if (playerEntity != null) {
						context.getStack().damage(1, playerEntity, p -> p.sendToolBreakStatus(context.getHand()));
					}
				}

				return ActionResult.success(world.isClient);
			} else {
				return ActionResult.PASS;
			}
		}
	}

	@Override
	protected class_5508 method_31212() {
		return class_5508.field_26767;
	}
}
