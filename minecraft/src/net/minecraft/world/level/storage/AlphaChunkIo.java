package net.minecraft.world.level.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.ChunkNibbleArray;

public class AlphaChunkIo {
	public static AlphaChunkIo.AlphaChunk readAlphaChunk(CompoundTag tag) {
		int i = tag.getInt("xPos");
		int j = tag.getInt("zPos");
		AlphaChunkIo.AlphaChunk alphaChunk = new AlphaChunkIo.AlphaChunk(i, j);
		alphaChunk.blocks = tag.getByteArray("Blocks");
		alphaChunk.data = new AlphaChunkDataArray(tag.getByteArray("Data"), 7);
		alphaChunk.skyLight = new AlphaChunkDataArray(tag.getByteArray("SkyLight"), 7);
		alphaChunk.blockLight = new AlphaChunkDataArray(tag.getByteArray("BlockLight"), 7);
		alphaChunk.heightMap = tag.getByteArray("HeightMap");
		alphaChunk.terrainPopulated = tag.getBoolean("TerrainPopulated");
		alphaChunk.entities = tag.getList("Entities", 10);
		alphaChunk.blockEntities = tag.getList("TileEntities", 10);
		alphaChunk.blockTicks = tag.getList("TileTicks", 10);

		try {
			alphaChunk.lastUpdate = tag.getLong("LastUpdate");
		} catch (ClassCastException var5) {
			alphaChunk.lastUpdate = (long)tag.getInt("LastUpdate");
		}

		return alphaChunk;
	}

	public static void convertAlphaChunk(AlphaChunkIo.AlphaChunk alphaChunk, CompoundTag tag, BiomeSource biomeSource) {
		tag.putInt("xPos", alphaChunk.x);
		tag.putInt("zPos", alphaChunk.z);
		tag.putLong("LastUpdate", alphaChunk.lastUpdate);
		int[] is = new int[alphaChunk.heightMap.length];

		for (int i = 0; i < alphaChunk.heightMap.length; i++) {
			is[i] = alphaChunk.heightMap[i];
		}

		tag.putIntArray("HeightMap", is);
		tag.putBoolean("TerrainPopulated", alphaChunk.terrainPopulated);
		ListTag listTag = new ListTag();

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

				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putByte("Y", (byte)(j & 0xFF));
				compoundTag.putByteArray("Blocks", bs);
				compoundTag.putByteArray("Data", chunkNibbleArray.asByteArray());
				compoundTag.putByteArray("SkyLight", chunkNibbleArray2.asByteArray());
				compoundTag.putByteArray("BlockLight", chunkNibbleArray3.asByteArray());
				listTag.add(compoundTag);
			}
		}

		tag.put("Sections", listTag);
		tag.putIntArray("Biomes", new BiomeArray(new ChunkPos(alphaChunk.x, alphaChunk.z), biomeSource).toIntArray());
		tag.put("Entities", alphaChunk.entities);
		tag.put("TileEntities", alphaChunk.blockEntities);
		if (alphaChunk.blockTicks != null) {
			tag.put("TileTicks", alphaChunk.blockTicks);
		}

		tag.putBoolean("convertedFromAlphaFormat", true);
	}

	public static class AlphaChunk {
		public long lastUpdate;
		public boolean terrainPopulated;
		public byte[] heightMap;
		public AlphaChunkDataArray blockLight;
		public AlphaChunkDataArray skyLight;
		public AlphaChunkDataArray data;
		public byte[] blocks;
		public ListTag entities;
		public ListTag blockEntities;
		public ListTag blockTicks;
		public final int x;
		public final int z;

		public AlphaChunk(int x, int z) {
			this.x = x;
			this.z = z;
		}
	}
}
