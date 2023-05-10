package net.minecraft.server.world;

public enum ChunkLevelType {
	INACCESSIBLE,
	FULL,
	BLOCK_TICKING,
	ENTITY_TICKING;

	public boolean isAfter(ChunkLevelType levelType) {
		return this.ordinal() >= levelType.ordinal();
	}
}
