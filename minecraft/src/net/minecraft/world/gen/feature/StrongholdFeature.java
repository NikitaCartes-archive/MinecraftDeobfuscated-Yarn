package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.class_3443;
import net.minecraft.class_3449;
import net.minecraft.class_3485;
import net.minecraft.sortme.structures.StrongholdGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.config.feature.DefaultFeatureConfig;

public class StrongholdFeature extends StructureFeature<DefaultFeatureConfig> {
	private boolean field_13851;
	private ChunkPos[] field_13852;
	private final List<class_3449> field_13853 = Lists.<class_3449>newArrayList();
	private long field_13854;

	public StrongholdFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	public boolean method_14026(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
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
	public StructureFeature.class_3774 method_14016() {
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
	public BlockPos locateStructure(World world, ChunkGenerator<? extends ChunkGeneratorSettings> chunkGenerator, BlockPos blockPos, int i, boolean bl) {
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
			BlockPos.Mutable mutable = new BlockPos.Mutable(0, 0, 0);
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

		int i = chunkGenerator.getSettings().method_12563();
		int j = chunkGenerator.getSettings().method_12561();
		int k = chunkGenerator.getSettings().method_12565();
		this.field_13852 = new ChunkPos[j];
		int l = 0;

		for (class_3449 lv : this.field_13853) {
			if (l < this.field_13852.length) {
				this.field_13852[l++] = new ChunkPos(lv.method_14967(), lv.method_14966());
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
				BlockPos blockPos = chunkGenerator.getBiomeSource().method_8762((q << 4) + 8, (r << 4) + 8, 112, list, random);
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

	public static class class_3189 extends class_3449 {
		public class_3189(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void method_16655(ChunkGenerator<?> chunkGenerator, class_3485 arg, int i, int j, Biome biome) {
			int k = 0;
			long l = chunkGenerator.getSeed();

			StrongholdGenerator.class_3434 lv;
			do {
				this.field_15330 = MutableIntBoundingBox.maxSize();
				this.field_16715.method_12663(l + (long)(k++), i, j);
				StrongholdGenerator.method_14855();
				lv = new StrongholdGenerator.class_3434(this.field_16715, (i << 4) + 2, (j << 4) + 2);
				this.children.add(lv);
				lv.method_14918(lv, this.children, this.field_16715);
				List<class_3443> list = lv.field_15282;

				while (!list.isEmpty()) {
					int m = this.field_16715.nextInt(list.size());
					class_3443 lv2 = (class_3443)list.remove(m);
					lv2.method_14918(lv, this.children, this.field_16715);
				}

				this.method_14969();
				this.method_14978(chunkGenerator.method_16398(), this.field_16715, 10);
			} while (this.children.isEmpty() || lv.field_15283 == null);

			((StrongholdFeature)this.method_16656()).field_13853.add(this);
		}
	}
}
