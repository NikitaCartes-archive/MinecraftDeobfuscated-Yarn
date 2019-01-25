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
	private boolean field_13851;
	private ChunkPos[] field_13852;
	private final List<StructureStart> field_13853 = Lists.<StructureStart>newArrayList();
	private long field_13854;

	public StrongholdFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	public boolean shouldStartAt(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
		if (this.field_13854 != chunkGenerator.getSeed()) {
			this.method_13986();
		}

		if (!this.field_13851) {
			this.method_13985(chunkGenerator);
			this.field_13851 = true;
		}

		for (ChunkPos chunkPos : this.field_13852) {
			if (i == chunkPos.x && j == chunkPos.z) {
				return true;
			}
		}

		return false;
	}

	private void method_13986() {
		this.field_13851 = false;
		this.field_13852 = null;
		this.field_13853.clear();
	}

	@Override
	public StructureFeature.StructureStartFactory getStructureStartFactory() {
		return StrongholdFeature.class_3189::new;
	}

	@Override
	public String getName() {
		return "Stronghold";
	}

	@Override
	public int method_14021() {
		return 8;
	}

	@Nullable
	@Override
	public BlockPos locateStructure(World world, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, BlockPos blockPos, int i, boolean bl) {
		if (!chunkGenerator.getBiomeSource().hasStructureFeature(this)) {
			return null;
		} else {
			if (this.field_13854 != world.getSeed()) {
				this.method_13986();
			}

			if (!this.field_13851) {
				this.method_13985(chunkGenerator);
				this.field_13851 = true;
			}

			BlockPos blockPos2 = null;
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			double d = Double.MAX_VALUE;

			for (ChunkPos chunkPos : this.field_13852) {
				mutable.set((chunkPos.x << 4) + 8, 32, (chunkPos.z << 4) + 8);
				double e = mutable.squaredDistanceTo(blockPos);
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

	private void method_13985(ChunkGenerator<?> chunkGenerator) {
		this.field_13854 = chunkGenerator.getSeed();
		List<Biome> list = Lists.<Biome>newArrayList();

		for (Biome biome : Registry.BIOME) {
			if (biome != null && chunkGenerator.hasStructure(biome, Feature.STRONGHOLD)) {
				list.add(biome);
			}
		}

		int i = chunkGenerator.getSettings().getStrongholdDistance();
		int j = chunkGenerator.getSettings().getStrongholdCount();
		int k = chunkGenerator.getSettings().getStrongholdSpread();
		this.field_13852 = new ChunkPos[j];
		int l = 0;

		for (StructureStart structureStart : this.field_13853) {
			if (l < this.field_13852.length) {
				this.field_13852[l++] = new ChunkPos(structureStart.getChunkX(), structureStart.getChunkZ());
			}
		}

		Random random = new Random();
		random.setSeed(chunkGenerator.getSeed());
		double d = random.nextDouble() * Math.PI * 2.0;
		int m = l;
		if (l < this.field_13852.length) {
			int n = 0;
			int o = 0;

			for (int p = 0; p < this.field_13852.length; p++) {
				double e = (double)(4 * i + i * o * 6) + (random.nextDouble() - 0.5) * (double)i * 2.5;
				int q = (int)Math.round(Math.cos(d) * e);
				int r = (int)Math.round(Math.sin(d) * e);
				BlockPos blockPos = chunkGenerator.getBiomeSource().locateBiome((q << 4) + 8, (r << 4) + 8, 112, list, random);
				if (blockPos != null) {
					q = blockPos.getX() >> 4;
					r = blockPos.getZ() >> 4;
				}

				if (p >= m) {
					this.field_13852[p] = new ChunkPos(q, r);
				}

				d += (Math.PI * 2) / (double)k;
				if (++n == k) {
					o++;
					n = 0;
					k += 2 * k / (o + 1);
					k = Math.min(k, this.field_13852.length - p);
					d += random.nextDouble() * Math.PI * 2.0;
				}
			}
		}
	}

	public static class class_3189 extends StructureStart {
		public class_3189(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
			int k = 0;
			long l = chunkGenerator.getSeed();

			StrongholdGenerator.Start start;
			do {
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
				this.method_14978(chunkGenerator.method_16398(), this.random, 10);
			} while (this.children.isEmpty() || start.field_15283 == null);

			((StrongholdFeature)this.getFeature()).field_13853.add(this);
		}
	}
}
