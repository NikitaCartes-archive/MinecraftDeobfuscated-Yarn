package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import java.util.Comparator;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public abstract class class_5164 extends SurfaceBuilder<TernarySurfaceConfig> {
	private long field_23920;
	private ImmutableMap<BlockState, OctavePerlinNoiseSampler> field_23921 = ImmutableMap.of();
	private ImmutableMap<BlockState, OctavePerlinNoiseSampler> field_23922 = ImmutableMap.of();
	private OctavePerlinNoiseSampler field_23923;

	public class_5164(Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
		super(function);
	}

	public void generate(
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
		int n = l + 1;
		int o = i & 15;
		int p = j & 15;
		int q = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		int r = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		double e = 0.03125;
		boolean bl = this.field_23923.sample((double)i * 0.03125, 109.0, (double)j * 0.03125) * 75.0 + random.nextDouble() > 0.0;
		BlockState blockState3 = (BlockState)((Entry)this.field_23922
				.entrySet()
				.stream()
				.max(Comparator.comparing(entry -> ((OctavePerlinNoiseSampler)entry.getValue()).sample((double)i, (double)l, (double)j)))
				.get())
			.getKey();
		BlockState blockState4 = (BlockState)((Entry)this.field_23921
				.entrySet()
				.stream()
				.max(Comparator.comparing(entry -> ((OctavePerlinNoiseSampler)entry.getValue()).sample((double)i, (double)l, (double)j)))
				.get())
			.getKey();
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		BlockState blockState5 = chunk.getBlockState(mutable.set(o, 128, p));

		for (int s = 127; s >= 0; s--) {
			mutable.set(o, s, p);
			BlockState blockState6 = chunk.getBlockState(mutable);
			if (blockState5.getBlock() == blockState.getBlock() && (blockState6.isAir() || blockState6 == blockState2)) {
				for (int t = 0; t < q; t++) {
					mutable.move(Direction.UP);
					if (chunk.getBlockState(mutable).getBlock() != blockState.getBlock()) {
						break;
					}

					chunk.setBlockState(mutable, blockState3, false);
				}

				mutable.set(o, s, p);
			}

			if ((blockState5.isAir() || blockState5 == blockState2) && blockState6.getBlock() == blockState.getBlock()) {
				for (int t = 0; t < r && chunk.getBlockState(mutable).getBlock() == blockState.getBlock(); t++) {
					if (bl && s >= n - 4 && s <= n + 1) {
						chunk.setBlockState(mutable, this.method_27135(), false);
					} else {
						chunk.setBlockState(mutable, blockState4, false);
					}

					mutable.move(Direction.DOWN);
				}
			}

			blockState5 = blockState6;
		}
	}

	@Override
	public void initSeed(long seed) {
		if (this.field_23920 != seed || this.field_23923 == null || this.field_23921.isEmpty() || this.field_23922.isEmpty()) {
			this.field_23921 = method_27131(this.method_27129(), seed);
			this.field_23922 = method_27131(this.method_27133(), seed + (long)this.field_23921.size());
			this.field_23923 = new OctavePerlinNoiseSampler(new ChunkRandom(seed + (long)this.field_23921.size() + (long)this.field_23922.size()), ImmutableList.of(0));
		}

		this.field_23920 = seed;
	}

	private static ImmutableMap<BlockState, OctavePerlinNoiseSampler> method_27131(ImmutableList<BlockState> immutableList, long l) {
		Builder<BlockState, OctavePerlinNoiseSampler> builder = new Builder<>();

		for (BlockState blockState : immutableList) {
			builder.put(blockState, new OctavePerlinNoiseSampler(new ChunkRandom(l), ImmutableList.of(-4)));
			l++;
		}

		return builder.build();
	}

	protected abstract ImmutableList<BlockState> method_27129();

	protected abstract ImmutableList<BlockState> method_27133();

	protected abstract BlockState method_27135();
}
