package net.minecraft.block.entity;

import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

public class BellBlockEntity extends BlockEntity implements Tickable {
	public int ringTicks;
	public boolean isRinging;
	public Direction direction;

	public BellBlockEntity() {
		super(BlockEntityType.BELL);
	}

	@Override
	public boolean onBlockAction(int i, int j) {
		if (i == 1) {
			this.direction = Direction.byId(j);
			this.ringTicks = 0;
			this.isRinging = true;
			return true;
		} else {
			return super.onBlockAction(i, j);
		}
	}

	@Override
	public void tick() {
		if (this.isRinging) {
			this.ringTicks++;
		}

		if (this.ringTicks >= 50) {
			this.isRinging = false;
			this.ringTicks = 0;
		}
	}

	public void activate(Direction direction) {
		this.direction = direction;
		if (this.isRinging) {
			this.ringTicks = 0;
		} else {
			this.isRinging = true;
		}

		if (!this.world.isClient) {
			this.world.addBlockAction(this.getPos(), this.getCachedState().getBlock(), 1, direction.getId());
		}
	}
}
