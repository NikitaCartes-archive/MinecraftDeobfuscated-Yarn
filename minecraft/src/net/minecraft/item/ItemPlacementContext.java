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
	private final BlockPos placementPos;
	protected boolean canReplaceExisting = true;

	public ItemPlacementContext(PlayerEntity player, Hand hand, ItemStack stack, BlockHitResult hitResult) {
		this(player.world, player, hand, stack, hitResult);
	}

	public ItemPlacementContext(ItemUsageContext context) {
		this(context.getWorld(), context.getPlayer(), context.getHand(), context.getStack(), context.getHitResult());
	}

	protected ItemPlacementContext(World world, @Nullable PlayerEntity playerEntity, Hand hand, ItemStack itemStack, BlockHitResult blockHitResult) {
		super(world, playerEntity, hand, itemStack, blockHitResult);
		this.placementPos = blockHitResult.getBlockPos().offset(blockHitResult.getSide());
		this.canReplaceExisting = world.getBlockState(blockHitResult.getBlockPos()).canReplace(this);
	}

	public static ItemPlacementContext offset(ItemPlacementContext context, BlockPos pos, Direction side) {
		return new ItemPlacementContext(
			context.getWorld(),
			context.getPlayer(),
			context.getHand(),
			context.getStack(),
			new BlockHitResult(
				new Vec3d(
					(double)pos.getX() + 0.5 + (double)side.getOffsetX() * 0.5,
					(double)pos.getY() + 0.5 + (double)side.getOffsetY() * 0.5,
					(double)pos.getZ() + 0.5 + (double)side.getOffsetZ() * 0.5
				),
				side,
				pos,
				false
			)
		);
	}

	@Override
	public BlockPos getBlockPos() {
		return this.canReplaceExisting ? super.getBlockPos() : this.placementPos;
	}

	public boolean canPlace() {
		return this.canReplaceExisting || this.getWorld().getBlockState(this.getBlockPos()).canReplace(this);
	}

	public boolean canReplaceExisting() {
		return this.canReplaceExisting;
	}

	public Direction getPlayerLookDirection() {
		return Direction.getEntityFacingOrder(this.getPlayer())[0];
	}

	public Direction getVerticalPlayerLookDirection() {
		return Direction.getLookDirectionForAxis(this.getPlayer(), Direction.Axis.Y);
	}

	public Direction[] getPlacementDirections() {
		Direction[] directions = Direction.getEntityFacingOrder(this.getPlayer());
		if (this.canReplaceExisting) {
			return directions;
		} else {
			Direction direction = this.getSide();
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
