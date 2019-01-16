package net.minecraft.scoreboard;

import net.minecraft.world.PersistentState;

public class ScoreboardSynchronizer implements Runnable {
	private final PersistentState compound;

	public ScoreboardSynchronizer(PersistentState persistentState) {
		this.compound = persistentState;
	}

	public void run() {
		this.compound.markDirty();
	}
}
