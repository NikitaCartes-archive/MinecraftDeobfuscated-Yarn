package net.minecraft.world.gen.feature;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PaneBlock;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.config.feature.EndPillarFeatureConfig;

public class EndSpikeFeature extends Feature<EndPillarFeatureConfig> {
	private static final LoadingCache<Long, List<EndSpikeFeature.Spike>> CACHE = CacheBuilder.newBuilder()
		.expireAfterWrite(5L, TimeUnit.MINUTES)
		.build(new EndSpikeFeature.SpikeCache());

	public EndSpikeFeature(Function<Dynamic<?>, ? extends EndPillarFeatureConfig> function) {
		super(function);
	}

	public static List<EndSpikeFeature.Spike> getSpikes(IWorld iWorld) {
		Random random = new Random(iWorld.getSeed());
		long l = random.nextLong() & 65535L;
		return CACHE.getUnchecked(l);
	}

	public boolean method_15887(
		IWorld iWorld,
		ChunkGenerator<? extends ChunkGeneratorSettings> chunkGenerator,
		Random random,
		BlockPos blockPos,
		EndPillarFeatureConfig endPillarFeatureConfig
	) {
		List<EndSpikeFeature.Spike> list = endPillarFeatureConfig.getSpikes();
		if (list.isEmpty()) {
			list = getSpikes(iWorld);
		}

		for (EndSpikeFeature.Spike spike : list) {
			if (spike.method_13962(blockPos)) {
				this.method_15888(iWorld, random, blockPos, endPillarFeatureConfig, spike);
			}
		}

		return true;
	}

	private void method_15888(IWorld iWorld, Random random, BlockPos blockPos, EndPillarFeatureConfig endPillarFeatureConfig, EndSpikeFeature.Spike spike) {
		int i = spike.getRadius();

		for (BlockPos.Mutable mutable : BlockPos.iterateBoxPositionsMutable(
			new BlockPos(blockPos.getX() - i, 0, blockPos.getZ() - i), new BlockPos(blockPos.getX() + i, spike.getHeight() + 10, blockPos.getZ() + i)
		)) {
			if (mutable.squaredDistanceTo((double)blockPos.getX(), (double)mutable.getY(), (double)blockPos.getZ()) <= (double)(i * i + 1)
				&& mutable.getY() < spike.getHeight()) {
				this.method_13153(iWorld, mutable, Blocks.field_10540.getDefaultState());
			} else if (mutable.getY() > 65) {
				this.method_13153(iWorld, mutable, Blocks.field_10124.getDefaultState());
			}
		}

		if (spike.isGuarded()) {
			int j = -2;
			int k = 2;
			int l = 3;
			BlockPos.Mutable mutable2 = new BlockPos.Mutable();

			for (int m = -2; m <= 2; m++) {
				for (int n = -2; n <= 2; n++) {
					for (int o = 0; o <= 3; o++) {
						boolean bl = MathHelper.abs(m) == 2;
						boolean bl2 = MathHelper.abs(n) == 2;
						boolean bl3 = o == 3;
						if (bl || bl2 || bl3) {
							boolean bl4 = m == -2 || m == 2 || bl3;
							boolean bl5 = n == -2 || n == 2 || bl3;
							BlockState blockState = Blocks.field_10576
								.getDefaultState()
								.with(PaneBlock.NORTH, Boolean.valueOf(bl4 && n != -2))
								.with(PaneBlock.SOUTH, Boolean.valueOf(bl4 && n != 2))
								.with(PaneBlock.WEST, Boolean.valueOf(bl5 && m != -2))
								.with(PaneBlock.EAST, Boolean.valueOf(bl5 && m != 2));
							this.method_13153(iWorld, mutable2.set(blockPos.getX() + m, spike.getHeight() + o, blockPos.getZ() + n), blockState);
						}
					}
				}
			}
		}

		EnderCrystalEntity enderCrystalEntity = new EnderCrystalEntity(iWorld.method_8410());
		enderCrystalEntity.setBeamTarget(endPillarFeatureConfig.getPos());
		enderCrystalEntity.setInvulnerable(endPillarFeatureConfig.isCrystalInvulerable());
		enderCrystalEntity.setPositionAndAngles(
			(double)((float)blockPos.getX() + 0.5F), (double)(spike.getHeight() + 1), (double)((float)blockPos.getZ() + 0.5F), random.nextFloat() * 360.0F, 0.0F
		);
		iWorld.spawnEntity(enderCrystalEntity);
		this.method_13153(iWorld, new BlockPos(blockPos.getX(), spike.getHeight(), blockPos.getZ()), Blocks.field_9987.getDefaultState());
	}

	public static class Spike {
		private final int centerX;
		private final int centerZ;
		private final int radius;
		private final int height;
		private final boolean guarded;
		private final BoundingBox boundingBox;

		public Spike(int i, int j, int k, int l, boolean bl) {
			this.centerX = i;
			this.centerZ = j;
			this.radius = k;
			this.height = l;
			this.guarded = bl;
			this.boundingBox = new BoundingBox((double)(i - k), 0.0, (double)(j - k), (double)(i + k), 256.0, (double)(j + k));
		}

		public boolean method_13962(BlockPos blockPos) {
			int i = this.centerX - this.radius;
			int j = this.centerZ - this.radius;
			return blockPos.getX() == (i & -16) && blockPos.getZ() == (j & -16);
		}

		public int getCenterX() {
			return this.centerX;
		}

		public int getCenterZ() {
			return this.centerZ;
		}

		public int getRadius() {
			return this.radius;
		}

		public int getHeight() {
			return this.height;
		}

		public boolean isGuarded() {
			return this.guarded;
		}

		public BoundingBox getBoundingBox() {
			return this.boundingBox;
		}

		public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
			Builder<T, T> builder = ImmutableMap.builder();
			builder.put(dynamicOps.createString("centerX"), dynamicOps.createInt(this.centerX));
			builder.put(dynamicOps.createString("centerZ"), dynamicOps.createInt(this.centerZ));
			builder.put(dynamicOps.createString("radius"), dynamicOps.createInt(this.radius));
			builder.put(dynamicOps.createString("height"), dynamicOps.createInt(this.height));
			builder.put(dynamicOps.createString("guarded"), dynamicOps.createBoolean(this.guarded));
			return new Dynamic<>(dynamicOps, dynamicOps.createMap(builder.build()));
		}

		public static <T> EndSpikeFeature.Spike deserialize(Dynamic<T> dynamic) {
			return new EndSpikeFeature.Spike(
				dynamic.getInt("centerX"), dynamic.getInt("centerZ"), dynamic.getInt("radius"), dynamic.getInt("height"), dynamic.getBoolean("guarded")
			);
		}
	}

	static class SpikeCache extends CacheLoader<Long, List<EndSpikeFeature.Spike>> {
		private SpikeCache() {
		}

		public List<EndSpikeFeature.Spike> method_14507(Long long_) throws Exception {
			List<Integer> list = (List<Integer>)IntStream.range(0, 10).boxed().collect(Collectors.toList());
			Collections.shuffle(list, new Random(long_));
			List<EndSpikeFeature.Spike> list2 = Lists.<EndSpikeFeature.Spike>newArrayList();

			for (int i = 0; i < 10; i++) {
				int j = (int)(42.0 * Math.cos(2.0 * (-Math.PI + (Math.PI / 10) * (double)i)));
				int k = (int)(42.0 * Math.sin(2.0 * (-Math.PI + (Math.PI / 10) * (double)i)));
				int l = (Integer)list.get(i);
				int m = 2 + l / 3;
				int n = 76 + l * 3;
				boolean bl = l == 1 || l == 2;
				list2.add(new EndSpikeFeature.Spike(j, k, m, n, bl));
			}

			return list2;
		}
	}
}
