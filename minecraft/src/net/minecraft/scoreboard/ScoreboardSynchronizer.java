package net.minecraft.scoreboard;

import net.minecraft.nbt.PersistedState;

public class ScoreboardSynchronizer implements Runnable {
	private final PersistedState compound;

	public ScoreboardSynchronizer(PersistedState persistedState) {
		this.compound = persistedState;
	}

	public void run() {
		this.compound.markDirty();
	}
}
