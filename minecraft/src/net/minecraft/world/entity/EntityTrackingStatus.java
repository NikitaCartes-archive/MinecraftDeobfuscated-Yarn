package net.minecraft.world.entity;

import net.minecraft.server.world.ChunkLevelType;

/**
 * The status of entity tracking sections within entity managers.
 * 
 * @see EntityTrackingSection
 */
public enum EntityTrackingStatus {
	HIDDEN(false, false),
	TRACKED(true, false),
	TICKING(true, true);

	private final boolean tracked;
	private final boolean tick;

	private EntityTrackingStatus(boolean tracked, boolean tick) {
		this.tracked = tracked;
		this.tick = tick;
	}

	public boolean shouldTick() {
		return this.tick;
	}

	public boolean shouldTrack() {
		return this.tracked;
	}

	public static EntityTrackingStatus fromLevelType(ChunkLevelType levelType) {
		if (levelType.isAfter(ChunkLevelType.ENTITY_TICKING)) {
			return TICKING;
		} else {
			return levelType.isAfter(ChunkLevelType.FULL) ? TRACKED : HIDDEN;
		}
	}
}
