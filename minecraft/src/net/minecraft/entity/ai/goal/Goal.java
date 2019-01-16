package net.minecraft.entity.ai.goal;

public abstract class Goal {
	private int controlBits;

	public abstract boolean canStart();

	public boolean shouldContinue() {
		return this.canStart();
	}

	public boolean canStop() {
		return true;
	}

	public void start() {
	}

	public void onRemove() {
	}

	public void tick() {
	}

	public void setControlBits(int i) {
		this.controlBits = i;
	}

	public int getControlBits() {
		return this.controlBits;
	}
}
