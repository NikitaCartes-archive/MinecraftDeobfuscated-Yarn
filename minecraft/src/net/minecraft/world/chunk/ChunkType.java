package net.minecraft.world.chunk;

/**
 * Specifies the type of a chunk
 */
public enum ChunkType {
	/**
	 * A chunk which is incomplete and not loaded to the world yet.
	 */
	PROTOCHUNK,
	/**
	 * A chunk which is complete and bound to a world.
	 */
	LEVELCHUNK;
}
