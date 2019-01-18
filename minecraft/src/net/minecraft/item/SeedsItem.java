package net.minecraft.item;

import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class SeedsItem extends Item {
	private final BlockState crop;
	private final SoundEvent field_17542;

	public SeedsItem(Block block, SoundEvent soundEvent, Item.Settings settings) {
		super(settings);
		this.crop = block.getDefaultState();
		this.field_17542 = soundEvent;
	}

	public SeedsItem(Block block, Item.Settings settings) {
		this(block, SoundEvents.field_17611, settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		IWorld iWorld = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getBlockPos().up();
		if (itemUsageContext.getFacing() == Direction.UP && iWorld.isAir(blockPos) && this.crop.canPlaceAt(iWorld, blockPos)) {
			iWorld.setBlockState(blockPos, this.crop, 11);
			iWorld.playSound(null, blockPos, this.field_17542, SoundCategory.field_15245, 1.0F, 1.0F);
			ItemStack itemStack = itemUsageContext.getItemStack();
			PlayerEntity playerEntity = itemUsageContext.getPlayer();
			if (playerEntity instanceof ServerPlayerEntity) {
				Criterions.PLACED_BLOCK.handle((ServerPlayerEntity)playerEntity, blockPos, itemStack);
			}

			itemStack.subtractAmount(1);
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.FAILURE;
		}
	}
}
