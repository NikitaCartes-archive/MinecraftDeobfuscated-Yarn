package net.minecraft.item;

import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class FoodCropItem extends FoodItem {
	protected final BlockState crop;
	protected final SoundEvent field_17541;

	public FoodCropItem(int i, float f, Block block, SoundEvent soundEvent, Item.Settings settings) {
		super(i, f, false, settings);
		this.crop = block.getDefaultState();
		this.field_17541 = soundEvent;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		IWorld iWorld = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getPos().up();
		if (itemUsageContext.getFacing() == Direction.UP && iWorld.isAir(blockPos) && this.crop.canPlaceAt(iWorld, blockPos)) {
			iWorld.setBlockState(blockPos, this.crop, 11);
			iWorld.playSound(null, blockPos, this.field_17541, SoundCategory.field_15245, 1.0F, 1.0F);
			PlayerEntity playerEntity = itemUsageContext.getPlayer();
			ItemStack itemStack = itemUsageContext.getItemStack();
			if (playerEntity instanceof ServerPlayerEntity) {
				Criterions.PLACED_BLOCK.handle((ServerPlayerEntity)playerEntity, blockPos, itemStack);
			}

			itemStack.subtractAmount(1);
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.PASS;
		}
	}
}
