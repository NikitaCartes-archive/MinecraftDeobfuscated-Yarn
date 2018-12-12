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
	}
}
