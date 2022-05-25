package net.minecraft.world;

/**
 * An enum holding the presence of a certain structure start in a chunk.
 * 
 * @see StructureLocator
 */
public enum StructurePresence {
	/**
	 * The structure start is present in the chunk.
	 */
	START_PRESENT,
	/**
	 * The structure start is not present in the chunk, or the
	 * start was already referenced and the {@code skipReferencedStructures}
	 * is set to {@code true}.
	 */
	START_NOT_PRESENT,
	/**
	 * The chunk is not loaded.
	 */
	CHUNK_LOAD_NEEDED;
}
