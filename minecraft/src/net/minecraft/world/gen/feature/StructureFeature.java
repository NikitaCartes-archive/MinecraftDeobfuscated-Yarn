package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.class_2810;
import net.minecraft.class_2919;
import net.minecraft.class_3443;
import net.minecraft.class_3449;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.config.feature.FeatureConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class StructureFeature<C extends FeatureConfig> extends Feature<C> {
	private static final Logger field_13879 = LogManager.getLogger();

	public StructureFeature(Function<Dynamic<?>, ? extends C> function) {
		super(function, false);
	}

	@Override
	public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorSettings> chunkGenerator, Random random, BlockPos blockPos, C featureConfig) {
		if (!iWorld.getLevelProperties().hasStructures()) {
			return false;
		} else {
			int i = blockPos.getX() >> 4;
			int j = blockPos.getZ() >> 4;
			int k = i << 4;
			int l = j << 4;
			boolean bl = false;
			LongIterator var11 = iWorld.getChunk(i, j).method_12180(this.getName()).iterator();

			while (var11.hasNext()) {
				Long long_ = (Long)var11.next();
				ChunkPos chunkPos = new ChunkPos(long_);
				class_3449 lv = iWorld.getChunk(chunkPos.x, chunkPos.z).method_12181(this.getName());
				if (lv != null && lv != class_3449.field_16713) {
					lv.method_14974(iWorld, random, new MutableIntBoundingBox(k, l, k + 15, l + 15), new ChunkPos(i, j));
					bl = true;
				}
			}

			return bl;
		}
	}

	protected class_3449 method_14025(IWorld iWorld, BlockPos blockPos, boolean bl) {
		for (class_3449 lv : this.method_14017(iWorld, blockPos.getX() >> 4, blockPos.getZ() >> 4)) {
			if (lv.hasChildren() && lv.method_14968().contains(blockPos)) {
				if (!bl) {
					return lv;
				}

				for (class_3443 lv2 : lv.method_14963()) {
					if (lv2.method_14935().contains(blockPos)) {
						return lv;
					}
				}
			}
		}

		return class_3449.field_16713;
	}

	public boolean method_14023(IWorld iWorld, BlockPos blockPos) {
		return this.method_14025(iWorld, blockPos, false).hasChildren();
	}

	public boolean method_14024(IWorld iWorld, BlockPos blockPos) {
		return this.method_14025(iWorld, blockPos, true).hasChildren();
	}

	@Nullable
	public BlockPos locateStructure(World world, ChunkGenerator<? extends ChunkGeneratorSettings> chunkGenerator, BlockPos blockPos, int i, boolean bl) {
		if (!chunkGenerator.getBiomeSource().hasStructureFeature(this)) {
			return null;
		} else {
			int j = blockPos.getX() >> 4;
			int k = blockPos.getZ() >> 4;
			int l = 0;

			for (class_2919 lv = new class_2919(); l <= i; l++) {
				for (int m = -l; m <= l; m++) {
					boolean bl2 = m == -l || m == l;

					for (int n = -l; n <= l; n++) {
						boolean bl3 = n == -l || n == l;
						if (bl2 || bl3) {
							ChunkPos chunkPos = this.method_14018(chunkGenerator, lv, j, k, m, n);
							class_3449 lv2 = world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.field_16423).method_12181(this.getName());
							if (lv2 != class_3449.field_16713) {
								if (bl && lv2.method_14979()) {
									lv2.incrementReferences();
									return lv2.method_14962();
								}

								if (!bl) {
									return lv2.method_14962();
								}
							}

							if (l == 0) {
								break;
							}
						}
					}

					if (l == 0) {
						break;
					}
				}
			}

			return null;
		}
	}

	private List<class_3449> method_14017(IWorld iWorld, int i, int j) {
		List<class_3449> list = Lists.<class_3449>newArrayList();
		Chunk chunk = iWorld.getChunk(i, j, ChunkStatus.field_12798);
		LongIterator longIterator = chunk.method_12180(this.getName()).iterator();

		while (longIterator.hasNext()) {
			long l = longIterator.nextLong();
			class_2810 lv = iWorld.getChunk(ChunkPos.longX(l), ChunkPos.longZ(l), ChunkStatus.field_12798);
			class_3449 lv2 = lv.method_12181(this.getName());
			if (lv2 != null) {
				list.add(lv2);
			}
		}

		return list;
	}

	protected ChunkPos method_14018(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
		return new ChunkPos(i + k, j + l);
	}

	public abstract boolean method_14026(ChunkGenerator<?> chunkGenerator, Random random, int i, int j);

	public abstract StructureFeature.class_3774 method_14016();

	public abstract String getName();

	public abstract int method_14021();

	public interface class_3774 {
		class_3449 create(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l);
	}
}
