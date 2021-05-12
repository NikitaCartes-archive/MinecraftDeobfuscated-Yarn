package net.minecraft.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HoeItem extends MiningToolItem {
	protected static final Map<Block, BlockState> TILLED_BLOCKS = Maps.<Block, BlockState>newHashMap(
		ImmutableMap.of(
			Blocks.GRASS_BLOCK,
			Blocks.FARMLAND.getDefaultState(),
			Blocks.DIRT_PATH,
			Blocks.FARMLAND.getDefaultState(),
			Blocks.DIRT,
			Blocks.FARMLAND.getDefaultState(),
			Blocks.COARSE_DIRT,
			Blocks.DIRT.getDefaultState()
		)
	);

	protected HoeItem(ToolMaterial material, int attackDamage, float attackSpeed, Item.Settings settings) {
		super((float)attackDamage, attackSpeed, material, BlockTags.HOE_MINEABLE, settings);
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
					world.setBlockState(blockPos, blockState, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
					if (playerEntity != null) {
						context.getStack().damage(1, playerEntity, p -> p.sendToolBreakStatus(context.getHand()));
					}
				}

				return ActionResult.success(world.isClient);
			}
		}

		return ActionResult.PASS;
	}
}
