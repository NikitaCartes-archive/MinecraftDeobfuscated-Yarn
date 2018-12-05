package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ItemPlacementContext extends ItemUsageContext {
	private final BlockPos placedPos;
	protected boolean field_7904 = true;

	public ItemPlacementContext(ItemUsageContext itemUsageContext) {
		this(
			itemUsageContext.getWorld(),
			itemUsageContext.getPlayer(),
			itemUsageContext.getItemStack(),
			itemUsageContext.getPos(),
			itemUsageContext.method_8038(),
			itemUsageContext.getHitX(),
			itemUsageContext.getHitY(),
			itemUsageContext.getHitZ()
		);
	}

	protected ItemPlacementContext(
		World world, @Nullable PlayerEntity playerEntity, ItemStack itemStack, BlockPos blockPos, Direction direction, float f, float g, float h
	) {
		super(world, playerEntity, itemStack, blockPos, direction, f, g, h);
		this.placedPos = this.pos.method_10093(this.field_8943);
		this.field_7904 = this.getWorld().getBlockState(this.pos).method_11587(this);
	}

	private ItemPlacementContext(ItemPlacementContext itemPlacementContext, BlockPos blockPos, Direction direction) {
		super(
			itemPlacementContext.getWorld(),
			itemPlacementContext.getPlayer(),
			itemPlacementContext.getItemStack(),
			blockPos.method_10093(direction.getOpposite()),
			direction,
			itemPlacementContext.getHitX(),
			itemPlacementContext.getHitY(),
			itemPlacementContext.getHitZ()
		);
		this.placedPos = blockPos;
		this.field_7904 = false;
	}

	public static ItemPlacementContext method_16355(ItemPlacementContext itemPlacementContext, BlockPos.Mutable mutable, Direction direction) {
		return new ItemPlacementContext(itemPlacementContext, mutable, direction);
	}

	@Override
	public BlockPos getPos() {
		return this.field_7904 ? this.pos : this.placedPos;
	}

	public boolean canPlace() {
		return this.field_7904 || this.getWorld().getBlockState(this.getPos()).method_11587(this);
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
			int i = 0;

			while (i < directions.length && directions[i] != this.field_8943.getOpposite()) {
				i++;
			}

			if (i > 0) {
				System.arraycopy(directions, 0, directions, 1, i);
				directions[0] = this.field_8943.getOpposite();
			}

			return directions;
		}
	}
}
