package net.minecraft.item;

import net.minecraft.advancement.criterion.CriterionCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class FoodCropItem extends FoodItem {
	private final BlockState crop;

	public FoodCropItem(int i, float f, Block block, Item.Settings settings) {
		super(i, f, false, settings);
		this.crop = block.getDefaultState();
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		IWorld iWorld = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getPos().up();
		if (itemUsageContext.method_8038() == Direction.UP && iWorld.isAir(blockPos) && this.crop.canPlaceAt(iWorld, blockPos)) {
			iWorld.setBlockState(blockPos, this.crop, 11);
			PlayerEntity playerEntity = itemUsageContext.getPlayer();
			ItemStack itemStack = itemUsageContext.getItemStack();
			if (playerEntity instanceof ServerPlayerEntity) {
				CriterionCriterions.PLACED_BLOCK.handle((ServerPlayerEntity)playerEntity, blockPos, itemStack);
			}

			itemStack.subtractAmount(1);
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.PASS;
		}
	}
}
