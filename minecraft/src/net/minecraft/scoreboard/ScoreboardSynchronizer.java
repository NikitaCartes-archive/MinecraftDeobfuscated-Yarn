package net.minecraft.scoreboard;

import net.minecraft.world.PersistentState;

public class ScoreboardSynchronizer implements Runnable {
	private final PersistentState field_66;

	public ScoreboardSynchronizer(PersistentState persistentState) {
		this.field_66 = persistentState;
	}

	public void run() {
		this.field_66.markDirty();
	}
}
