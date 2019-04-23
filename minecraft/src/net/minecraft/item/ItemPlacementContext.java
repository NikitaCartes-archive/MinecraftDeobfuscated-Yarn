package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemPlacementContext extends ItemUsageContext {
	private final BlockPos placedPos;
	protected boolean canReplaceHitBlock = true;

	public ItemPlacementContext(ItemUsageContext itemUsageContext) {
		this(itemUsageContext.getWorld(), itemUsageContext.getPlayer(), itemUsageContext.getHand(), itemUsageContext.getItemStack(), itemUsageContext.hitResult);
	}

	protected ItemPlacementContext(World world, @Nullable PlayerEntity playerEntity, Hand hand, ItemStack itemStack, BlockHitResult blockHitResult) {
		super(world, playerEntity, hand, itemStack, blockHitResult);
		this.placedPos = blockHitResult.getBlockPos().offset(blockHitResult.getSide());
		this.canReplaceHitBlock = world.getBlockState(blockHitResult.getBlockPos()).canReplace(this);
	}

	public static ItemPlacementContext create(ItemPlacementContext itemPlacementContext, BlockPos blockPos, Direction direction) {
		return new ItemPlacementContext(
			itemPlacementContext.getWorld(),
			itemPlacementContext.getPlayer(),
			itemPlacementContext.getHand(),
			itemPlacementContext.getItemStack(),
			new BlockHitResult(
				new Vec3d(
					(double)blockPos.getX() + 0.5 + (double)direction.getOffsetX() * 0.5,
					(double)blockPos.getY() + 0.5 + (double)direction.getOffsetY() * 0.5,
					(double)blockPos.getZ() + 0.5 + (double)direction.getOffsetZ() * 0.5
				),
				direction,
				blockPos,
				false
			)
		);
	}

	@Override
	public BlockPos getBlockPos() {
		return this.canReplaceHitBlock ? super.getBlockPos() : this.placedPos;
	}

	public boolean canPlace() {
		return this.canReplaceHitBlock || this.getWorld().getBlockState(this.getBlockPos()).canReplace(this);
	}

	public boolean canReplaceHitBlock() {
		return this.canReplaceHitBlock;
	}

	public Direction getPlayerFacing() {
		return Direction.getEntityFacingOrder(this.player)[0];
	}

	public Direction[] getPlacementFacings() {
		Direction[] directions = Direction.getEntityFacingOrder(this.player);
		if (this.canReplaceHitBlock) {
			return directions;
		} else {
			Direction direction = this.getFacing();
			int i = 0;

			while (i < directions.length && directions[i] != direction.getOpposite()) {
				i++;
			}

			if (i > 0) {
				System.arraycopy(directions, 0, directions, 1, i);
				directions[0] = direction.getOpposite();
			}

			return directions;
		}
	}
}
