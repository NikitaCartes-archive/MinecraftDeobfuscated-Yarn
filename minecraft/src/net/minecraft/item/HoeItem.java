package net.minecraft.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import net.minecraft.class_5508;
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

public class HoeItem extends MiningToolItem {
	private static final Set<Block> EFFECTIVE_BLOCKS = ImmutableSet.of(
		Blocks.field_10541,
		Blocks.field_22115,
		Blocks.field_10359,
		Blocks.field_10342,
		Blocks.field_22422,
		Blocks.field_22122,
		Blocks.field_10258,
		Blocks.field_10562,
		Blocks.field_10335,
		Blocks.field_10503,
		Blocks.field_9988,
		Blocks.field_10035,
		Blocks.field_10098,
		Blocks.field_10539
	);
	protected static final Map<Block, BlockState> TILLED_BLOCKS = Maps.<Block, BlockState>newHashMap(
		ImmutableMap.of(
			Blocks.field_10219,
			Blocks.field_10362.getDefaultState(),
			Blocks.field_10194,
			Blocks.field_10362.getDefaultState(),
			Blocks.field_10566,
			Blocks.field_10362.getDefaultState(),
			Blocks.field_10253,
			Blocks.field_10566.getDefaultState()
		)
	);

	public HoeItem(ToolMaterial toolMaterial, Item.Settings settings) {
		super(toolMaterial, EFFECTIVE_BLOCKS, settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		if (context.getSide() != Direction.field_11033 && world.getBlockState(blockPos.up()).isAir()) {
			BlockState blockState = (BlockState)TILLED_BLOCKS.get(world.getBlockState(blockPos).getBlock());
			if (blockState != null) {
				PlayerEntity playerEntity = context.getPlayer();
				world.playSound(playerEntity, blockPos, SoundEvents.field_14846, SoundCategory.field_15245, 1.0F, 1.0F);
				if (!world.isClient) {
					world.setBlockState(blockPos, blockState, 11);
					if (playerEntity != null) {
						context.getStack().damage(1, playerEntity, p -> p.sendToolBreakStatus(context.getHand()));
					}
				}

				return ActionResult.success(world.isClient);
			}
		}

		return ActionResult.PASS;
	}

	@Override
	protected class_5508 method_31212() {
		return class_5508.field_26766;
	}
}
