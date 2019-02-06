package net.minecraft.village;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class VillageDoor {
	private final BlockPos position;
	private final BlockPos insidePosition;
	private final Direction facing;
	private int lastTimeSeenByVillager;
	private boolean invalid;
	private int entityInsideTicks;

	public VillageDoor(BlockPos blockPos, int i, int j, int k) {
		this(blockPos, getFacing(i, j), k);
	}

	private static Direction getFacing(int i, int j) {
		if (i < 0) {
			return Direction.WEST;
		} else if (i > 0) {
			return Direction.EAST;
		} else {
			return j < 0 ? Direction.NORTH : Direction.SOUTH;
		}
	}

	public VillageDoor(BlockPos blockPos, Direction direction, int i) {
		this.position = blockPos.toImmutable();
		this.facing = direction;
		this.insidePosition = blockPos.offset(direction, 2);
		this.lastTimeSeenByVillager = i;
	}

	public int squaredDistance(int i, int j, int k) {
		return (int)this.position.squaredDistanceTo((double)i, (double)j, (double)k);
	}

	public int squaredDistanceTo(BlockPos blockPos) {
		return (int)blockPos.squaredDistanceTo(this.getPosition());
	}

	public int squaredDistanceFromInsideTo(BlockPos blockPos) {
		return (int)this.insidePosition.squaredDistanceTo(blockPos);
	}

	public boolean isInside(BlockPos blockPos) {
		int i = blockPos.getX() - this.position.getX();
		int j = blockPos.getZ() - this.position.getY();
		return i * this.facing.getOffsetX() + j * this.facing.getOffsetZ() >= 0;
	}

	public void clearEntityInsideTicks() {
		this.entityInsideTicks = 0;
	}

	public void entityInsideTick() {
		this.entityInsideTicks++;
	}

	public int getEntityInsideTicks() {
		return this.entityInsideTicks;
	}

	public BlockPos getPosition() {
		return this.position;
	}

	public BlockPos getInsidePosition() {
		return this.insidePosition;
	}

	public int insideDirectionX() {
		return this.facing.getOffsetX() * 2;
	}

	public int insideDirectionY() {
		return this.facing.getOffsetZ() * 2;
	}

	public int getLastTimeSeenByVillager() {
		return this.lastTimeSeenByVillager;
	}

	public void setLastTimeSeenByVillager(int i) {
		this.lastTimeSeenByVillager = i;
	}

	public boolean isInvalid() {
		return this.invalid;
	}

	public void setInvalid(boolean bl) {
		this.invalid = bl;
	}

	public Direction getFacing() {
		return this.facing;
	}
}
