package net.minecraft.world.level.storage;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.ChunkNibbleArray;

public class AlphaChunkIo {
	private static final int field_31416 = 7;
	private static final HeightLimitView world = new HeightLimitView() {
		@Override
		public int getBottomY() {
			return 0;
		}

		@Override
		public int getHeight() {
			return 128;
		}
	};

	public static AlphaChunkIo.AlphaChunk readAlphaChunk(NbtCompound nbt) {
		int i = nbt.getInt("xPos");
		int j = nbt.getInt("zPos");
		AlphaChunkIo.AlphaChunk alphaChunk = new AlphaChunkIo.AlphaChunk(i, j);
		alphaChunk.blocks = nbt.getByteArray("Blocks");
		alphaChunk.data = new AlphaChunkDataArray(nbt.getByteArray("Data"), 7);
		alphaChunk.skyLight = new AlphaChunkDataArray(nbt.getByteArray("SkyLight"), 7);
		alphaChunk.blockLight = new AlphaChunkDataArray(nbt.getByteArray("BlockLight"), 7);
		alphaChunk.heightMap = nbt.getByteArray("HeightMap");
		alphaChunk.terrainPopulated = nbt.getBoolean("TerrainPopulated");
		alphaChunk.entities = nbt.getList("Entities", NbtElement.COMPOUND_TYPE);
		alphaChunk.blockEntities = nbt.getList("TileEntities", NbtElement.COMPOUND_TYPE);
		alphaChunk.blockTicks = nbt.getList("TileTicks", NbtElement.COMPOUND_TYPE);

		try {
			alphaChunk.lastUpdate = nbt.getLong("LastUpdate");
		} catch (ClassCastException var5) {
			alphaChunk.lastUpdate = (long)nbt.getInt("LastUpdate");
		}

		return alphaChunk;
	}

	public static void convertAlphaChunk(DynamicRegistryManager.Impl impl, AlphaChunkIo.AlphaChunk alphaChunk, NbtCompound nbt, BiomeSource biomeSource) {
		nbt.putInt("xPos", alphaChunk.x);
		nbt.putInt("zPos", alphaChunk.z);
		nbt.putLong("LastUpdate", alphaChunk.lastUpdate);
		int[] is = new int[alphaChunk.heightMap.length];

		for (int i = 0; i < alphaChunk.heightMap.length; i++) {
			is[i] = alphaChunk.heightMap[i];
		}

		nbt.putIntArray("HeightMap", is);
		nbt.putBoolean("TerrainPopulated", alphaChunk.terrainPopulated);
		NbtList nbtList = new NbtList();

		for (int j = 0; j < 8; j++) {
			boolean bl = true;

			for (int k = 0; k < 16 && bl; k++) {
				for (int l = 0; l < 16 && bl; l++) {
					for (int m = 0; m < 16; m++) {
						int n = k << 11 | m << 7 | l + (j << 4);
						int o = alphaChunk.blocks[n];
						if (o != 0) {
							bl = false;
							break;
						}
					}
				}
			}

			if (!bl) {
				byte[] bs = new byte[4096];
				ChunkNibbleArray chunkNibbleArray = new ChunkNibbleArray();
				ChunkNibbleArray chunkNibbleArray2 = new ChunkNibbleArray();
				ChunkNibbleArray chunkNibbleArray3 = new ChunkNibbleArray();

				for (int o = 0; o < 16; o++) {
					for (int p = 0; p < 16; p++) {
						for (int q = 0; q < 16; q++) {
							int r = o << 11 | q << 7 | p + (j << 4);
							int s = alphaChunk.blocks[r];
							bs[p << 8 | q << 4 | o] = (byte)(s & 0xFF);
							chunkNibbleArray.set(o, p, q, alphaChunk.data.get(o, p + (j << 4), q));
							chunkNibbleArray2.set(o, p, q, alphaChunk.skyLight.get(o, p + (j << 4), q));
							chunkNibbleArray3.set(o, p, q, alphaChunk.blockLight.get(o, p + (j << 4), q));
						}
					}
				}

				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putByte("Y", (byte)(j & 0xFF));
				nbtCompound.putByteArray("Blocks", bs);
				nbtCompound.putByteArray("Data", chunkNibbleArray.asByteArray());
				nbtCompound.putByteArray("SkyLight", chunkNibbleArray2.asByteArray());
				nbtCompound.putByteArray("BlockLight", chunkNibbleArray3.asByteArray());
				nbtList.add(nbtCompound);
			}
		}

		nbt.put("Sections", nbtList);
		nbt.putIntArray("Biomes", new BiomeArray(impl.get(Registry.BIOME_KEY), world, new ChunkPos(alphaChunk.x, alphaChunk.z), biomeSource).toIntArray());
		nbt.put("Entities", alphaChunk.entities);
		nbt.put("TileEntities", alphaChunk.blockEntities);
		if (alphaChunk.blockTicks != null) {
			nbt.put("TileTicks", alphaChunk.blockTicks);
		}

		nbt.putBoolean("convertedFromAlphaFormat", true);
	}

	public static class AlphaChunk {
		public long lastUpdate;
		public boolean terrainPopulated;
		public byte[] heightMap;
		public AlphaChunkDataArray blockLight;
		public AlphaChunkDataArray skyLight;
		public AlphaChunkDataArray data;
		public byte[] blocks;
		public NbtList entities;
		public NbtList blockEntities;
		public NbtList blockTicks;
		public final int x;
		public final int z;

		public AlphaChunk(int x, int z) {
			this.x = x;
			this.z = z;
		}
	}
}
