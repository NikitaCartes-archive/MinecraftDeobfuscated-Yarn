package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemPlacementContext extends ItemUsageContext {
	private final BlockPos field_7903;
	protected boolean field_7904 = true;

	public ItemPlacementContext(ItemUsageContext itemUsageContext) {
		this(itemUsageContext.method_8045(), itemUsageContext.getPlayer(), itemUsageContext.getItemStack(), itemUsageContext.field_17543);
	}

	protected ItemPlacementContext(World world, @Nullable PlayerEntity playerEntity, ItemStack itemStack, BlockHitResult blockHitResult) {
		super(world, playerEntity, itemStack, blockHitResult);
		this.field_7903 = blockHitResult.method_17777().method_10093(blockHitResult.method_17780());
		this.field_7904 = world.method_8320(blockHitResult.method_17777()).method_11587(this);
	}

	public static ItemPlacementContext method_16355(ItemPlacementContext itemPlacementContext, BlockPos blockPos, Direction direction) {
		return new ItemPlacementContext(
			itemPlacementContext.method_8045(),
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
	public BlockPos method_8037() {
		return this.field_7904 ? super.method_8037() : this.field_7903;
	}

	public boolean canPlace() {
		return this.field_7904 || this.method_8045().method_8320(this.method_8037()).method_11587(this);
	}

	public boolean method_7717() {
		return this.field_7904;
	}

	public Direction method_7715() {
		return Direction.getEntityFacingOrder(this.player)[0];
	}

	public Direction[] method_7718() {
		Direction[] directions = Direction.getEntityFacingOrder(this.player);
		if (this.field_7904) {
			return directions;
		} else {
			Direction direction = this.method_8038();
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
