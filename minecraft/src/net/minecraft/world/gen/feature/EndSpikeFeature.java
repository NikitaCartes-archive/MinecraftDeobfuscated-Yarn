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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class EndSpikeFeature extends Feature<EndSpikeFeatureConfig> {
	private static final LoadingCache<Long, List<EndSpikeFeature.Spike>> CACHE = CacheBuilder.newBuilder()
		.expireAfterWrite(5L, TimeUnit.MINUTES)
		.build(new EndSpikeFeature.SpikeCache());

	public EndSpikeFeature(Function<Dynamic<?>, ? extends EndSpikeFeatureConfig> function) {
		super(function);
	}

	public static List<EndSpikeFeature.Spike> getSpikes(IWorld iWorld) {
		Random random = new Random(iWorld.getSeed());
		long l = random.nextLong() & 65535L;
		return CACHE.getUnchecked(l);
	}

	public boolean method_15887(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, EndSpikeFeatureConfig endSpikeFeatureConfig
	) {
		List<EndSpikeFeature.Spike> list = endSpikeFeatureConfig.getSpikes();
		if (list.isEmpty()) {
			list = getSpikes(iWorld);
		}

		for (EndSpikeFeature.Spike spike : list) {
			if (spike.isInChunk(blockPos)) {
				this.generateSpike(iWorld, random, endSpikeFeatureConfig, spike);
			}
		}

		return true;
	}

	private void generateSpike(IWorld iWorld, Random random, EndSpikeFeatureConfig endSpikeFeatureConfig, EndSpikeFeature.Spike spike) {
		int i = spike.getRadius();

		for (BlockPos blockPos : BlockPos.iterate(
			new BlockPos(spike.getCenterX() - i, 0, spike.getCenterZ() - i), new BlockPos(spike.getCenterX() + i, spike.getHeight() + 10, spike.getCenterZ() + i)
		)) {
			if (blockPos.getSquaredDistance((double)spike.getCenterX(), (double)blockPos.getY(), (double)spike.getCenterZ(), false) <= (double)(i * i + 1)
				&& blockPos.getY() < spike.getHeight()) {
				this.setBlockState(iWorld, blockPos, Blocks.OBSIDIAN.getDefaultState());
			} else if (blockPos.getY() > 65) {
				this.setBlockState(iWorld, blockPos, Blocks.AIR.getDefaultState());
			}
		}

		if (spike.isGuarded()) {
			int j = -2;
			int k = 2;
			int l = 3;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int m = -2; m <= 2; m++) {
				for (int n = -2; n <= 2; n++) {
					for (int o = 0; o <= 3; o++) {
						boolean bl = MathHelper.abs(m) == 2;
						boolean bl2 = MathHelper.abs(n) == 2;
						boolean bl3 = o == 3;
						if (bl || bl2 || bl3) {
							boolean bl4 = m == -2 || m == 2 || bl3;
							boolean bl5 = n == -2 || n == 2 || bl3;
							BlockState blockState = Blocks.IRON_BARS
								.getDefaultState()
								.with(PaneBlock.NORTH, Boolean.valueOf(bl4 && n != -2))
								.with(PaneBlock.SOUTH, Boolean.valueOf(bl4 && n != 2))
								.with(PaneBlock.WEST, Boolean.valueOf(bl5 && m != -2))
								.with(PaneBlock.EAST, Boolean.valueOf(bl5 && m != 2));
							this.setBlockState(iWorld, mutable.set(spike.getCenterX() + m, spike.getHeight() + o, spike.getCenterZ() + n), blockState);
						}
					}
				}
			}
		}

		EnderCrystalEntity enderCrystalEntity = EntityType.END_CRYSTAL.create(iWorld.getWorld());
		enderCrystalEntity.setBeamTarget(endSpikeFeatureConfig.getPos());
		enderCrystalEntity.setInvulnerable(endSpikeFeatureConfig.isCrystalInvulerable());
		enderCrystalEntity.setPositionAndAngles(
			(double)((float)spike.getCenterX() + 0.5F), (double)(spike.getHeight() + 1), (double)((float)spike.getCenterZ() + 0.5F), random.nextFloat() * 360.0F, 0.0F
		);
		iWorld.spawnEntity(enderCrystalEntity);
		this.setBlockState(iWorld, new BlockPos(spike.getCenterX(), spike.getHeight(), spike.getCenterZ()), Blocks.BEDROCK.getDefaultState());
	}

	public static class Spike {
		private final int centerX;
		private final int centerZ;
		private final int radius;
		private final int height;
		private final boolean guarded;
		private final Box boundingBox;

		public Spike(int i, int j, int k, int l, boolean bl) {
			this.centerX = i;
			this.centerZ = j;
			this.radius = k;
			this.height = l;
			this.guarded = bl;
			this.boundingBox = new Box((double)(i - k), 0.0, (double)(j - k), (double)(i + k), 256.0, (double)(j + k));
		}

		public boolean isInChunk(BlockPos blockPos) {
			return blockPos.getX() >> 4 == this.centerX >> 4 && blockPos.getZ() >> 4 == this.centerZ >> 4;
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

		public Box getBoundingBox() {
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
				dynamic.get("centerX").asInt(0),
				dynamic.get("centerZ").asInt(0),
				dynamic.get("radius").asInt(0),
				dynamic.get("height").asInt(0),
				dynamic.get("guarded").asBoolean(false)
			);
		}
	}

	static class SpikeCache extends CacheLoader<Long, List<EndSpikeFeature.Spike>> {
		private SpikeCache() {
		}

		public List<EndSpikeFeature.Spike> method_14507(Long long_) {
			List<Integer> list = (List<Integer>)IntStream.range(0, 10).boxed().collect(Collectors.toList());
			Collections.shuffle(list, new Random(long_));
			List<EndSpikeFeature.Spike> list2 = Lists.<EndSpikeFeature.Spike>newArrayList();

			for (int i = 0; i < 10; i++) {
				int j = MathHelper.floor(42.0 * Math.cos(2.0 * (-Math.PI + (Math.PI / 10) * (double)i)));
				int k = MathHelper.floor(42.0 * Math.sin(2.0 * (-Math.PI + (Math.PI / 10) * (double)i)));
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
