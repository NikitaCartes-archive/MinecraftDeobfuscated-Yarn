package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.generator.StrongholdGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class StrongholdFeature extends StructureFeature<DefaultFeatureConfig> {
	private boolean stateStillValid;
	private ChunkPos[] startPositions;
	private final List<StructureStart> starts = Lists.<StructureStart>newArrayList();
	private long lastSeed;

	public StrongholdFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	public boolean shouldStartAt(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
		if (this.lastSeed != chunkGenerator.getSeed()) {
			this.invalidateState();
		}

		if (!this.stateStillValid) {
			this.initialize(chunkGenerator);
			this.stateStillValid = true;
		}

		for (ChunkPos chunkPos : this.startPositions) {
			if (i == chunkPos.x && j == chunkPos.z) {
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
	public BlockPos locateStructure(World world, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, BlockPos blockPos, int i, boolean bl) {
		if (!chunkGenerator.getBiomeSource().hasStructureFeature(this)) {
			return null;
		} else {
			if (this.lastSeed != world.getSeed()) {
				this.invalidateState();
			}

			if (!this.stateStillValid) {
				this.initialize(chunkGenerator);
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

	private void initialize(ChunkGenerator<?> chunkGenerator) {
		this.lastSeed = chunkGenerator.getSeed();
		List<Biome> list = Lists.<Biome>newArrayList();

		for (Biome biome : Registry.BIOME) {
			if (biome != null && chunkGenerator.hasStructure(biome, Feature.STRONGHOLD)) {
				list.add(biome);
			}
		}

		int i = chunkGenerator.getConfig().getStrongholdDistance();
		int j = chunkGenerator.getConfig().getStrongholdCount();
		int k = chunkGenerator.getConfig().getStrongholdSpread();
		this.startPositions = new ChunkPos[j];
		int l = 0;

		for (StructureStart structureStart : this.starts) {
			if (l < this.startPositions.length) {
				this.startPositions[l++] = new ChunkPos(structureStart.getChunkX(), structureStart.getChunkZ());
			}
		}

		Random random = new Random();
		random.setSeed(chunkGenerator.getSeed());
		double d = random.nextDouble() * Math.PI * 2.0;
		int m = l;
		if (l < this.startPositions.length) {
			int n = 0;
			int o = 0;

			for (int p = 0; p < this.startPositions.length; p++) {
				double e = (double)(4 * i + i * o * 6) + (random.nextDouble() - 0.5) * (double)i * 2.5;
				int q = (int)Math.round(Math.cos(d) * e);
				int r = (int)Math.round(Math.sin(d) * e);
				BlockPos blockPos = chunkGenerator.getBiomeSource().locateBiome((q << 4) + 8, (r << 4) + 8, 112, list, random);
				if (blockPos != null) {
					q = blockPos.getX() >> 4;
					r = blockPos.getZ() >> 4;
				}

				if (p >= m) {
					this.startPositions[p] = new ChunkPos(q, r);
				}

				d += (Math.PI * 2) / (double)k;
				if (++n == k) {
					o++;
					n = 0;
					k += 2 * k / (o + 1);
					k = Math.min(k, this.startPositions.length - p);
					d += random.nextDouble() * Math.PI * 2.0;
				}
			}
		}
	}

	public static class Start extends StructureStart {
		public Start(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
			int k = 0;
			long l = chunkGenerator.getSeed();

			StrongholdGenerator.Start start;
			do {
				this.children.clear();
				this.boundingBox = MutableIntBoundingBox.empty();
				this.random.setStructureSeed(l + (long)(k++), i, j);
				StrongholdGenerator.method_14855();
				start = new StrongholdGenerator.Start(this.random, (i << 4) + 2, (j << 4) + 2);
				this.children.add(start);
				start.method_14918(start, this.children, this.random);
				List<StructurePiece> list = start.field_15282;

				while (!list.isEmpty()) {
					int m = this.random.nextInt(list.size());
					StructurePiece structurePiece = (StructurePiece)list.remove(m);
					structurePiece.method_14918(start, this.children, this.random);
				}

				this.setBoundingBoxFromChildren();
				this.method_14978(chunkGenerator.getSeaLevel(), this.random, 10);
			} while (this.children.isEmpty() || start.field_15283 == null);

			((StrongholdFeature)this.getFeature()).starts.add(this);
		}
	}
}
