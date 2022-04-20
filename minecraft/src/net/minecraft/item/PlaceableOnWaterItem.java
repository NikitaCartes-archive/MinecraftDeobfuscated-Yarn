package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class PlaceableOnWaterItem extends BlockItem {
	public PlaceableOnWaterItem(Block block, Item.Settings settings) {
		super(block, settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		return ActionResult.PASS;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		BlockHitResult blockHitResult = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
		BlockHitResult blockHitResult2 = blockHitResult.withBlockPos(blockHitResult.getBlockPos().up());
		ActionResult actionResult = super.useOnBlock(new ItemUsageContext(user, hand, blockHitResult2));
		return new TypedActionResult<>(actionResult, user.getStackInHand(hand));
	}
}
