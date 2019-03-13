package net.minecraft.world.gen.surfacebuilder;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class WoodedBadlandsSurfaceBuilder extends BadlandsSurfaceBuilder {
	private static final BlockState WHITE_TERRACOTTA = Blocks.field_10611.method_9564();
	private static final BlockState ORANGE_TERRACOTTA = Blocks.field_10184.method_9564();
	private static final BlockState TERRACOTTA = Blocks.field_10415.method_9564();

	public WoodedBadlandsSurfaceBuilder(Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
		super(function);
	}

	@Override
	public void method_15208(
		Random random,
		Chunk chunk,
		Biome biome,
		int i,
		int j,
		int k,
		double d,
		BlockState blockState,
		BlockState blockState2,
		int l,
		long m,
		TernarySurfaceConfig ternarySurfaceConfig
	) {
		int n = i & 15;
		int o = j & 15;
		BlockState blockState3 = WHITE_TERRACOTTA;
		BlockState blockState4 = biome.method_8722().getUnderMaterial();
		int p = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		boolean bl = Math.cos(d / 3.0 * Math.PI) > 0.0;
		int q = -1;
		boolean bl2 = false;
		int r = 0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int s = k; s >= 0; s--) {
			if (r < 15) {
				mutable.set(n, s, o);
				BlockState blockState5 = chunk.method_8320(mutable);
				if (blockState5.isAir()) {
					q = -1;
				} else if (blockState5.getBlock() == blockState.getBlock()) {
					if (q == -1) {
						bl2 = false;
						if (p <= 0) {
							blockState3 = Blocks.field_10124.method_9564();
							blockState4 = blockState;
						} else if (s >= l - 4 && s <= l + 1) {
							blockState3 = WHITE_TERRACOTTA;
							blockState4 = biome.method_8722().getUnderMaterial();
						}

						if (s < l && (blockState3 == null || blockState3.isAir())) {
							blockState3 = blockState2;
						}

						q = p + Math.max(0, s - l);
						if (s < l - 1) {
							chunk.method_12010(mutable, blockState4, false);
							if (blockState4 == WHITE_TERRACOTTA) {
								chunk.method_12010(mutable, ORANGE_TERRACOTTA, false);
							}
						} else if (s > 86 + p * 2) {
							if (bl) {
								chunk.method_12010(mutable, Blocks.field_10253.method_9564(), false);
							} else {
								chunk.method_12010(mutable, Blocks.field_10219.method_9564(), false);
							}
						} else if (s <= l + 3 + p) {
							chunk.method_12010(mutable, biome.method_8722().getTopMaterial(), false);
							bl2 = true;
						} else {
							BlockState blockState6;
							if (s < 64 || s > 127) {
								blockState6 = ORANGE_TERRACOTTA;
							} else if (bl) {
								blockState6 = TERRACOTTA;
							} else {
								blockState6 = this.method_15207(i, s, j);
							}

							chunk.method_12010(mutable, blockState6, false);
						}
					} else if (q > 0) {
						q--;
						if (bl2) {
							chunk.method_12010(mutable, ORANGE_TERRACOTTA, false);
						} else {
							chunk.method_12010(mutable, this.method_15207(i, s, j), false);
						}
					}

					r++;
				}
			}
		}
	}
}
