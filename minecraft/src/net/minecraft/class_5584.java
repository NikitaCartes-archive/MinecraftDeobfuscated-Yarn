package net.minecraft;

import net.minecraft.server.world.ChunkHolder;

public enum class_5584 {
	HIDDEN(false, false),
	TRACKED(true, false),
	TICKING(true, true);

	private final boolean tracked;
	private final boolean tick;

	private class_5584(boolean tracked, boolean tick) {
		this.tracked = tracked;
		this.tick = tick;
	}

	public boolean shouldTick() {
		return this.tick;
	}

	public boolean shouldTrack() {
		return this.tracked;
	}

	public static class_5584 method_31884(ChunkHolder.LevelType levelType) {
		if (levelType.isAfter(ChunkHolder.LevelType.ENTITY_TICKING)) {
			return TICKING;
		} else {
			return levelType.isAfter(ChunkHolder.LevelType.BORDER) ? TRACKED : HIDDEN;
		}
	}
}
