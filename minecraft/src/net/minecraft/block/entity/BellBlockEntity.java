package net.minecraft.block.entity;

import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

public class BellBlockEntity extends BlockEntity implements Tickable {
	public int ringTicks;
	public boolean isRinging;
	public Direction field_17097;

	public BellBlockEntity() {
		super(BlockEntityType.BELL);
	}

	@Override
	public boolean onBlockAction(int i, int j) {
		if (i == 1) {
			this.field_17097 = Direction.byId(j);
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

	public void method_17031(Direction direction) {
		this.field_17097 = direction;
		if (this.isRinging) {
			this.ringTicks = 0;
		} else {
			this.isRinging = true;
		}

		if (!this.world.isClient) {
			this.world.method_8427(this.method_11016(), this.method_11010().getBlock(), 1, direction.getId());
		}
	}
}
