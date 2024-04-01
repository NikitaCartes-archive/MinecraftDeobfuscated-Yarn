package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class FloatatoItem extends BlockItem {
	private static final float field_50587 = 3.0F;

	public FloatatoItem(Block block, Item.Settings settings) {
		super(block, settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		Vec3d vec3d = user.getEyePos();
		Vec3d vec3d2 = user.getRotationVector(user.getPitch(), user.getYaw());
		Vec3d vec3d3 = vec3d.add(vec3d2.multiply(user.getBlockInteractionRange()));
		BlockHitResult blockHitResult = world.raycast(
			new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.SOURCE_ONLY, user)
		);
		ItemStack itemStack = user.getStackInHand(hand);
		if (blockHitResult.getType() == HitResult.Type.MISS) {
			Vec3d vec3d4 = vec3d.add(vec3d2.multiply(3.0));
			ActionResult actionResult = super.place(
				new ItemPlacementContext(user, hand, itemStack, new BlockHitResult(vec3d4, user.getHorizontalFacing(), BlockPos.ofFloored(vec3d4), false))
			);
			return new TypedActionResult<>(actionResult, itemStack);
		} else {
			ActionResult actionResult2 = super.useOnBlock(new ItemUsageContext(user, hand, blockHitResult));
			return new TypedActionResult<>(actionResult2, itemStack);
		}
	}
}
