package net.minecraft.world.level.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.ChunkNibbleArray;

public class AlphaChunkIo {
	public static AlphaChunkIo.AlphaChunk readAlphaChunk(CompoundTag compoundTag) {
		int i = compoundTag.getInt("xPos");
		int j = compoundTag.getInt("zPos");
		AlphaChunkIo.AlphaChunk alphaChunk = new AlphaChunkIo.AlphaChunk(i, j);
		alphaChunk.field_13048 = compoundTag.getByteArray("Blocks");
		alphaChunk.field_13044 = new AlphaChunkDataArray(compoundTag.getByteArray("Data"), 7);
		alphaChunk.field_13039 = new AlphaChunkDataArray(compoundTag.getByteArray("SkyLight"), 7);
		alphaChunk.field_13038 = new AlphaChunkDataArray(compoundTag.getByteArray("BlockLight"), 7);
		alphaChunk.field_13045 = compoundTag.getByteArray("HeightMap");
		alphaChunk.field_13042 = compoundTag.getBoolean("TerrainPopulated");
		alphaChunk.field_13037 = compoundTag.getList("Entities", 10);
		alphaChunk.field_13040 = compoundTag.getList("TileEntities", 10);
		alphaChunk.field_13041 = compoundTag.getList("TileTicks", 10);

		try {
			alphaChunk.field_13043 = compoundTag.getLong("LastUpdate");
		} catch (ClassCastException var5) {
			alphaChunk.field_13043 = (long)compoundTag.getInt("LastUpdate");
		}

		return alphaChunk;
	}

	public static void convertAlphaChunk(AlphaChunkIo.AlphaChunk alphaChunk, CompoundTag compoundTag, BiomeSource biomeSource) {
		compoundTag.putInt("xPos", alphaChunk.field_13047);
		compoundTag.putInt("zPos", alphaChunk.field_13046);
		compoundTag.putLong("LastUpdate", alphaChunk.field_13043);
		int[] is = new int[alphaChunk.field_13045.length];

		for (int i = 0; i < alphaChunk.field_13045.length; i++) {
			is[i] = alphaChunk.field_13045[i];
		}

		compoundTag.putIntArray("HeightMap", is);
		compoundTag.putBoolean("TerrainPopulated", alphaChunk.field_13042);
		ListTag listTag = new ListTag();

		for (int j = 0; j < 8; j++) {
			boolean bl = true;

			for (int k = 0; k < 16 && bl; k++) {
				for (int l = 0; l < 16 && bl; l++) {
					for (int m = 0; m < 16; m++) {
						int n = k << 11 | m << 7 | l + (j << 4);
						int o = alphaChunk.field_13048[n];
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
							int s = alphaChunk.field_13048[r];
							bs[p << 8 | q << 4 | o] = (byte)(s & 0xFF);
							chunkNibbleArray.set(o, p, q, alphaChunk.field_13044.get(o, p + (j << 4), q));
							chunkNibbleArray2.set(o, p, q, alphaChunk.field_13039.get(o, p + (j << 4), q));
							chunkNibbleArray3.set(o, p, q, alphaChunk.field_13038.get(o, p + (j << 4), q));
						}
					}
				}

				CompoundTag compoundTag2 = new CompoundTag();
				compoundTag2.putByte("Y", (byte)(j & 0xFF));
				compoundTag2.putByteArray("Blocks", bs);
				compoundTag2.putByteArray("Data", chunkNibbleArray.asByteArray());
				compoundTag2.putByteArray("SkyLight", chunkNibbleArray2.asByteArray());
				compoundTag2.putByteArray("BlockLight", chunkNibbleArray3.asByteArray());
				listTag.add((Tag)compoundTag2);
			}
		}

		compoundTag.put("Sections", listTag);
		byte[] cs = new byte[256];
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int k = 0; k < 16; k++) {
			for (int l = 0; l < 16; l++) {
				mutable.set(alphaChunk.field_13047 << 4 | k, 0, alphaChunk.field_13046 << 4 | l);
				cs[l << 4 | k] = (byte)(Registry.BIOME.getRawId(biomeSource.getBiome(mutable)) & 0xFF);
			}
		}

		compoundTag.putByteArray("Biomes", cs);
		compoundTag.put("Entities", alphaChunk.field_13037);
		compoundTag.put("TileEntities", alphaChunk.field_13040);
		if (alphaChunk.field_13041 != null) {
			compoundTag.put("TileTicks", alphaChunk.field_13041);
		}

		compoundTag.putBoolean("convertedFromAlphaFormat", true);
	}

	public static class AlphaChunk {
		public long field_13043;
		public boolean field_13042;
		public byte[] field_13045;
		public AlphaChunkDataArray field_13038;
		public AlphaChunkDataArray field_13039;
		public AlphaChunkDataArray field_13044;
		public byte[] field_13048;
		public ListTag field_13037;
		public ListTag field_13040;
		public ListTag field_13041;
		public final int field_13047;
		public final int field_13046;

		public AlphaChunk(int i, int j) {
			this.field_13047 = i;
			this.field_13046 = j;
		}
	}
}
