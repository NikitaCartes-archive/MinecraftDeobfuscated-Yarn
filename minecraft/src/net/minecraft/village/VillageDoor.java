package net.minecraft.village;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class VillageDoor {
	private final BlockPos position;
	private final BlockPos field_6712;
	private final Direction field_6711;
	private int lastTimeSeenByVillager;
	private boolean field_6710;
	private int entityInsideTicks;

	public VillageDoor(BlockPos blockPos, int i, int j, int k) {
		this(blockPos, method_6427(i, j), k);
	}

	private static Direction method_6427(int i, int j) {
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
		this.field_6711 = direction;
		this.field_6712 = blockPos.method_10079(direction, 2);
		this.lastTimeSeenByVillager = i;
	}

	public int squaredDistance(int i, int j, int k) {
		return (int)this.position.squaredDistanceTo((double)i, (double)j, (double)k);
	}

	public int squaredDistanceTo(BlockPos blockPos) {
		return (int)blockPos.squaredDistanceTo(this.getPosition());
	}

	public int method_6417(BlockPos blockPos) {
		return (int)this.field_6712.squaredDistanceTo(blockPos);
	}

	public boolean method_6425(BlockPos blockPos) {
		int i = blockPos.getX() - this.position.getX();
		int j = blockPos.getZ() - this.position.getY();
		return i * this.field_6711.getOffsetX() + j * this.field_6711.getOffsetZ() >= 0;
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

	public BlockPos method_6422() {
		return this.field_6712;
	}

	public int insideDirectionX() {
		return this.field_6711.getOffsetX() * 2;
	}

	public int insideDirectionY() {
		return this.field_6711.getOffsetZ() * 2;
	}

	public int getLastTimeSeenByVillager() {
		return this.lastTimeSeenByVillager;
	}

	public void setLastTimeSeenByVillager(int i) {
		this.lastTimeSeenByVillager = i;
	}

	public boolean method_6413() {
		return this.field_6710;
	}

	public void method_6418(boolean bl) {
		this.field_6710 = bl;
	}

	public Direction method_6424() {
		return this.field_6711;
	}
}
