package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class StrongholdFeature extends StructureFeature<DefaultFeatureConfig> {
	private boolean stateStillValid;
	private ChunkPos[] startPositions;
	private final List<StructureStart> starts = Lists.<StructureStart>newArrayList();
	private long lastSeed;

	public StrongholdFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	public boolean method_27217(BiomeAccess biomeAccess, ChunkGenerator chunkGenerator, long l, ChunkRandom chunkRandom, int i, int j, Biome biome) {
		ChunkPos chunkPos = this.method_27218(chunkGenerator.getConfig(), l, chunkRandom, i, j);
		return this.shouldStartAt(biomeAccess, chunkGenerator, l, chunkRandom, i, j, biome, chunkPos);
	}

	@Override
	protected boolean shouldStartAt(
		BiomeAccess biomeAccess, ChunkGenerator chunkGenerator, long l, ChunkRandom chunkRandom, int i, int j, Biome biome, ChunkPos chunkPos
	) {
		if (this.lastSeed != l) {
			this.invalidateState();
		}

		if (!this.stateStillValid) {
			this.initialize(chunkGenerator, l);
			this.stateStillValid = true;
		}

		for (ChunkPos chunkPos2 : this.startPositions) {
			if (i == chunkPos2.x && j == chunkPos2.z) {
				return true;
			}
		}

		return false;
	}

	private void invalidateState() {
		this.stateStillValid = false;
		this.startPositions = null;
		this.starts.clear();
	}

	@Override
	public StructureFeature.StructureStartFactory getStructureStartFactory() {
		return StrongholdFeature.Start::new;
	}

	@Override
	public String getName() {
		return "Stronghold";
	}

	@Override
	public int getRadius() {
		return 8;
	}

	@Nullable
	@Override
	public BlockPos locateStructure(ServerWorld serverWorld, ChunkGenerator chunkGenerator, BlockPos blockPos, int i, boolean skipExistingChunks) {
		if (!chunkGenerator.hasStructure(this)) {
			return null;
		} else {
			if (this.lastSeed != serverWorld.getSeed()) {
				this.invalidateState();
			}

			if (!this.stateStillValid) {
				this.initialize(chunkGenerator, serverWorld.getSeed());
				this.stateStillValid = true;
			}

			BlockPos blockPos2 = null;
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			double d = Double.MAX_VALUE;

			for (ChunkPos chunkPos : this.startPositions) {
				mutable.set((chunkPos.x << 4) + 8, 32, (chunkPos.z << 4) + 8);
				double e = mutable.getSquaredDistance(blockPos);
				if (blockPos2 == null) {
					blockPos2 = new BlockPos(mutable);
					d = e;
				} else if (e < d) {
					blockPos2 = new BlockPos(mutable);
					d = e;
				}
			}

			return blockPos2;
		}
	}

	private void initialize(ChunkGenerator chunkGenerator, long l) {
		this.lastSeed = l;
		List<Biome> list = Lists.<Biome>newArrayList();

		for (Biome biome : Registry.BIOME) {
			if (biome != null && chunkGenerator.hasStructure(biome, this)) {
				list.add(biome);
			}
		}

		int i = chunkGenerator.getConfig().getStrongholdDistance();
		int j = chunkGenerator.getConfig().getStrongholdCount();
		int k = chunkGenerator.getConfig().getStrongholdSpread();
		this.startPositions = new ChunkPos[j];
		int m = 0;

		for (StructureStart structureStart : this.starts) {
			if (m < this.startPositions.length) {
				this.startPositions[m++] = new ChunkPos(structureStart.getChunkX(), structureStart.getChunkZ());
			}
		}

		Random random = new Random();
		random.setSeed(l);
		double d = random.nextDouble() * Math.PI * 2.0;
		int n = m;
		if (m < this.startPositions.length) {
			int o = 0;
			int p = 0;

			for (int q = 0; q < this.startPositions.length; q++) {
				double e = (double)(4 * i + i * p * 6) + (random.nextDouble() - 0.5) * (double)i * 2.5;
				int r = (int)Math.round(Math.cos(d) * e);
				int s = (int)Math.round(Math.sin(d) * e);
				BlockPos blockPos = chunkGenerator.getBiomeSource().locateBiome((r << 4) + 8, chunkGenerator.getSeaLevel(), (s << 4) + 8, 112, list, random);
				if (blockPos != null) {
					r = blockPos.getX() >> 4;
					s = blockPos.getZ() >> 4;
				}

				if (q >= n) {
					this.startPositions[q] = new ChunkPos(r, s);
				}

				d += (Math.PI * 2) / (double)k;
				if (++o == k) {
					p++;
					o = 0;
					k += 2 * k / (p + 1);
					k = Math.min(k, this.startPositions.length - q);
					d += random.nextDouble() * Math.PI * 2.0;
				}
			}
		}
	}

	public static class Start extends StructureStart {
		private final long field_24559;

		public Start(StructureFeature<?> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
			this.field_24559 = l;
		}

		@Override
		public void init(ChunkGenerator chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
			int i = 0;

			StrongholdGenerator.Start start;
			do {
				this.children.clear();
				this.boundingBox = BlockBox.empty();
				this.random.setCarverSeed(this.field_24559 + (long)(i++), x, z);
				StrongholdGenerator.init();
				start = new StrongholdGenerator.Start(this.random, (x << 4) + 2, (z << 4) + 2);
				this.children.add(start);
				start.placeJigsaw(start, this.children, this.random);
				List<StructurePiece> list = start.field_15282;

				while (!list.isEmpty()) {
					int j = this.random.nextInt(list.size());
					StructurePiece structurePiece = (StructurePiece)list.remove(j);
					structurePiece.placeJigsaw(start, this.children, this.random);
				}

				this.setBoundingBoxFromChildren();
				this.method_14978(chunkGenerator.getSeaLevel(), this.random, 10);
			} while (this.children.isEmpty() || start.field_15283 == null);

			((StrongholdFeature)this.getFeature()).starts.add(this);
		}
	}
}
