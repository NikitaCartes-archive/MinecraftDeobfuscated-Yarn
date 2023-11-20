package net.minecraft.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class ShearsItem extends Item {
	public ShearsItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
		if (!world.isClient && !state.isIn(BlockTags.FIRE)) {
			stack.damage(1, miner, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
		}

		return !state.isIn(BlockTags.LEAVES)
				&& !state.isOf(Blocks.COBWEB)
				&& !state.isOf(Blocks.SHORT_GRASS)
				&& !state.isOf(Blocks.FERN)
				&& !state.isOf(Blocks.DEAD_BUSH)
				&& !state.isOf(Blocks.HANGING_ROOTS)
				&& !state.isOf(Blocks.VINE)
				&& !state.isOf(Blocks.TRIPWIRE)
				&& !state.isIn(BlockTags.WOOL)
			? super.postMine(stack, world, state, pos, miner)
			: true;
	}

	@Override
	public boolean isSuitableFor(BlockState state) {
		return state.isOf(Blocks.COBWEB) || state.isOf(Blocks.REDSTONE_WIRE) || state.isOf(Blocks.TRIPWIRE);
	}

	@Override
	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		if (state.isOf(Blocks.COBWEB) || state.isIn(BlockTags.LEAVES)) {
			return 15.0F;
		} else if (state.isIn(BlockTags.WOOL)) {
			return 5.0F;
		} else {
			return !state.isOf(Blocks.VINE) && !state.isOf(Blocks.GLOW_LICHEN) ? super.getMiningSpeedMultiplier(stack, state) : 2.0F;
		}
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() instanceof AbstractPlantStemBlock abstractPlantStemBlock && !abstractPlantStemBlock.hasMaxAge(blockState)) {
			PlayerEntity playerEntity = context.getPlayer();
			ItemStack itemStack = context.getStack();
			if (playerEntity instanceof ServerPlayerEntity) {
				Criteria.ITEM_USED_ON_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos, itemStack);
			}

			world.playSound(playerEntity, blockPos, SoundEvents.BLOCK_GROWING_PLANT_CROP, SoundCategory.BLOCKS, 1.0F, 1.0F);
			BlockState blockState2 = abstractPlantStemBlock.withMaxAge(blockState);
			world.setBlockState(blockPos, blockState2);
			world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(context.getPlayer(), blockState2));
			if (playerEntity != null) {
				itemStack.damage(1, playerEntity, player -> player.sendToolBreakStatus(context.getHand()));
			}

			return ActionResult.success(world.isClient);
		}

		return super.useOnBlock(context);
	}
}
