package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemPlacementContext extends ItemUsageContext {
	private final BlockPos placedPos;
	protected boolean field_7904 = true;

	public ItemPlacementContext(ItemUsageContext itemUsageContext) {
		this(itemUsageContext.getWorld(), itemUsageContext.getPlayer(), itemUsageContext.getItemStack(), itemUsageContext.field_17543);
	}

	protected ItemPlacementContext(World world, @Nullable PlayerEntity playerEntity, ItemStack itemStack, BlockHitResult blockHitResult) {
		super(world, playerEntity, itemStack, blockHitResult);
		this.placedPos = blockHitResult.getBlockPos().offset(blockHitResult.getSide());
		this.field_7904 = world.getBlockState(blockHitResult.getBlockPos()).method_11587(this);
	}

	public static ItemPlacementContext create(ItemPlacementContext itemPlacementContext, BlockPos blockPos, Direction direction) {
		return new ItemPlacementContext(
			itemPlacementContext.getWorld(),
			itemPlacementContext.getPlayer(),
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
		return this.field_7904 ? super.getBlockPos() : this.placedPos;
	}

	public boolean canPlace() {
		return this.field_7904 || this.getWorld().getBlockState(this.getBlockPos()).method_11587(this);
	}

	public boolean method_7717() {
		return this.field_7904;
	}

	public Direction getPlayerFacing() {
		return Direction.getEntityFacingOrder(this.player)[0];
	}

	public Direction[] getPlacementFacings() {
		Direction[] directions = Direction.getEntityFacingOrder(this.player);
		if (this.field_7904) {
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
