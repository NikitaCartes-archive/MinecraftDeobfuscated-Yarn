package net.minecraft.scoreboard;

import net.minecraft.world.PersistentState;

public class ScoreboardSynchronizer implements Runnable {
	private final PersistentState compound;

	public ScoreboardSynchronizer(PersistentState compound) {
		this.compound = compound;
	}

	public void run() {
		this.compound.markDirty();
	}
}
