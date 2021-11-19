package net.minecraft.world.biome.source;

/**
 * Utility class for converting between biome coordinates and block or chunk
 * coordinates.
 * 
 * <p>Modders should use this class as Mojang may change the biome coordinate to
 * block or chunk coordinate ratio in the future again.
 * 
 * @apiNote A biome voxel comprises of 4×4×4 block voxels, as that's how biomes
 * are stored in game after 19w36a. Each chunk section has 4×4×4 biome
 * voxels as a result.
 */
public final class BiomeCoords {
	public static final int field_33089 = 2;
	public static final int field_33090 = 4;
	public static final int field_34830 = 3;
	private static final int field_33091 = 2;

	private BiomeCoords() {
	}

	/**
	 * Converts a block x, y, or z to a biome x, y, or z.
	 * 
	 * @implSpec This implementation returns {@code blockCoord / 4}.
	 * 
	 * @param blockCoord a block x, y, or z
	 */
	public static int fromBlock(int blockCoord) {
		return blockCoord >> 2;
	}

	public static int method_39920(int i) {
		return i & 3;
	}

	/**
	 * Converts a biome x, y, or z to a block x, y, or z.
	 * 
	 * @implSpec This implementation returns {@code blockCoord * 4}.
	 * 
	 * @param biomeCoord a biome x, y, or z
	 */
	public static int toBlock(int biomeCoord) {
		return biomeCoord << 2;
	}

	/**
	 * Converts a chunk x or z to a biome x or z.
	 * 
	 * @implSpec This implementation returns {@code chunkCoord * 4}.
	 * 
	 * @param chunkCoord a chunk x or z
	 */
	public static int fromChunk(int chunkCoord) {
		return chunkCoord << 2;
	}

	/**
	 * Converts a biome x or z to a chunk x or z.
	 * 
	 * @implSpec This implementation returns {@code biomeCoord / 4}.
	 * 
	 * @param biomeCoord a biome x or z
	 */
	public static int toChunk(int biomeCoord) {
		return biomeCoord >> 2;
	}
}
