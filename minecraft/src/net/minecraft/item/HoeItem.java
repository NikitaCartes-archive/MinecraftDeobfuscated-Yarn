package net.minecraft.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import net.minecraft.class_5510;
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
		Blocks.NETHER_WART_BLOCK,
		Blocks.WARPED_WART_BLOCK,
		Blocks.HAY_BLOCK,
		Blocks.DRIED_KELP_BLOCK,
		Blocks.TARGET,
		Blocks.SHROOMLIGHT,
		Blocks.SPONGE,
		Blocks.WET_SPONGE,
		Blocks.JUNGLE_LEAVES,
		Blocks.OAK_LEAVES,
		Blocks.SPRUCE_LEAVES,
		Blocks.DARK_OAK_LEAVES,
		Blocks.ACACIA_LEAVES,
		Blocks.BIRCH_LEAVES
	);
	protected static final Map<Block, BlockState> TILLED_BLOCKS = Maps.<Block, BlockState>newHashMap(
		ImmutableMap.of(
			Blocks.GRASS_BLOCK,
			Blocks.FARMLAND.getDefaultState(),
			Blocks.GRASS_PATH,
			Blocks.FARMLAND.getDefaultState(),
			Blocks.DIRT,
			Blocks.FARMLAND.getDefaultState(),
			Blocks.COARSE_DIRT,
			Blocks.DIRT.getDefaultState()
		)
	);

	public HoeItem(ToolMaterial toolMaterial, Item.Settings settings) {
		super(toolMaterial, EFFECTIVE_BLOCKS, settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		if (context.getSide() != Direction.DOWN && world.getBlockState(blockPos.up()).isAir()) {
			BlockState blockState = (BlockState)TILLED_BLOCKS.get(world.getBlockState(blockPos).getBlock());
			if (blockState != null) {
				PlayerEntity playerEntity = context.getPlayer();
				world.playSound(playerEntity, blockPos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
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
	protected class_5510 method_31243() {
		return class_5510.field_26794;
	}
}
