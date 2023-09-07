package net.minecraft.world.gen.feature;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class EndSpikeFeature extends Feature<EndSpikeFeatureConfig> {
	public static final int COUNT = 10;
	private static final int DISTANCE_FROM_ORIGIN = 42;
	private static final LoadingCache<Long, List<EndSpikeFeature.Spike>> CACHE = CacheBuilder.newBuilder()
		.expireAfterWrite(5L, TimeUnit.MINUTES)
		.build(new EndSpikeFeature.SpikeCache());

	public EndSpikeFeature(Codec<EndSpikeFeatureConfig> codec) {
		super(codec);
	}

	public static List<EndSpikeFeature.Spike> getSpikes(StructureWorldAccess world) {
		Random random = Random.create(world.getSeed());
		long l = random.nextLong() & 65535L;
		return CACHE.getUnchecked(l);
	}

	@Override
	public boolean generate(FeatureContext<EndSpikeFeatureConfig> context) {
		EndSpikeFeatureConfig endSpikeFeatureConfig = context.getConfig();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		Random random = context.getRandom();
		BlockPos blockPos = context.getOrigin();
		List<EndSpikeFeature.Spike> list = endSpikeFeatureConfig.getSpikes();
		if (list.isEmpty()) {
			list = getSpikes(structureWorldAccess);
		}

		for (EndSpikeFeature.Spike spike : list) {
			if (spike.isInChunk(blockPos)) {
				this.generateSpike(structureWorldAccess, random, endSpikeFeatureConfig, spike);
			}
		}

		return true;
	}

	private void generateSpike(ServerWorldAccess world, Random random, EndSpikeFeatureConfig config, EndSpikeFeature.Spike spike) {
		int i = spike.getRadius();

		for (BlockPos blockPos : BlockPos.iterate(
			new BlockPos(spike.getCenterX() - i, world.getBottomY(), spike.getCenterZ() - i),
			new BlockPos(spike.getCenterX() + i, spike.getHeight() + 10, spike.getCenterZ() + i)
		)) {
			if (blockPos.getSquaredDistance((double)spike.getCenterX(), (double)blockPos.getY(), (double)spike.getCenterZ()) <= (double)(i * i + 1)
				&& blockPos.getY() < spike.getHeight()) {
				this.setBlockState(world, blockPos, Blocks.OBSIDIAN.getDefaultState());
			} else if (blockPos.getY() > 65) {
				this.setBlockState(world, blockPos, Blocks.AIR.getDefaultState());
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
							this.setBlockState(world, mutable.set(spike.getCenterX() + m, spike.getHeight() + o, spike.getCenterZ() + n), blockState);
						}
					}
				}
			}
		}

		EndCrystalEntity endCrystalEntity = EntityType.END_CRYSTAL.create(world.toServerWorld());
		if (endCrystalEntity != null) {
			endCrystalEntity.setBeamTarget(config.getPos());
			endCrystalEntity.setInvulnerable(config.isCrystalInvulnerable());
			endCrystalEntity.refreshPositionAndAngles(
				(double)spike.getCenterX() + 0.5, (double)(spike.getHeight() + 1), (double)spike.getCenterZ() + 0.5, random.nextFloat() * 360.0F, 0.0F
			);
			world.spawnEntity(endCrystalEntity);
			BlockPos blockPosx = endCrystalEntity.getBlockPos();
			this.setBlockState(world, blockPosx.down(), Blocks.BEDROCK.getDefaultState());
			this.setBlockState(world, blockPosx, FireBlock.getState(world, blockPosx));
		}
	}

	public static class Spike {
		public static final Codec<EndSpikeFeature.Spike> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.INT.fieldOf("centerX").orElse(0).forGetter(spike -> spike.centerX),
						Codec.INT.fieldOf("centerZ").orElse(0).forGetter(spike -> spike.centerZ),
						Codec.INT.fieldOf("radius").orElse(0).forGetter(spike -> spike.radius),
						Codec.INT.fieldOf("height").orElse(0).forGetter(spike -> spike.height),
						Codec.BOOL.fieldOf("guarded").orElse(false).forGetter(spike -> spike.guarded)
					)
					.apply(instance, EndSpikeFeature.Spike::new)
		);
		private final int centerX;
		private final int centerZ;
		private final int radius;
		private final int height;
		private final boolean guarded;
		private final Box boundingBox;

		public Spike(int centerX, int centerZ, int radius, int height, boolean guarded) {
			this.centerX = centerX;
			this.centerZ = centerZ;
			this.radius = radius;
			this.height = height;
			this.guarded = guarded;
			this.boundingBox = new Box(
				(double)(centerX - radius),
				(double)DimensionType.MIN_HEIGHT,
				(double)(centerZ - radius),
				(double)(centerX + radius),
				(double)DimensionType.MAX_COLUMN_HEIGHT,
				(double)(centerZ + radius)
			);
		}

		public boolean isInChunk(BlockPos pos) {
			return ChunkSectionPos.getSectionCoord(pos.getX()) == ChunkSectionPos.getSectionCoord(this.centerX)
				&& ChunkSectionPos.getSectionCoord(pos.getZ()) == ChunkSectionPos.getSectionCoord(this.centerZ);
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
	}

	static class SpikeCache extends CacheLoader<Long, List<EndSpikeFeature.Spike>> {
		public List<EndSpikeFeature.Spike> load(Long long_) {
			IntArrayList intArrayList = Util.shuffle(IntStream.range(0, 10), Random.create(long_));
			List<EndSpikeFeature.Spike> list = Lists.<EndSpikeFeature.Spike>newArrayList();

			for (int i = 0; i < 10; i++) {
				int j = MathHelper.floor(42.0 * Math.cos(2.0 * (-Math.PI + (Math.PI / 10) * (double)i)));
				int k = MathHelper.floor(42.0 * Math.sin(2.0 * (-Math.PI + (Math.PI / 10) * (double)i)));
				int l = intArrayList.get(i);
				int m = 2 + l / 3;
				int n = 76 + l * 3;
				boolean bl = l == 1 || l == 2;
				list.add(new EndSpikeFeature.Spike(j, k, m, n, bl));
			}

			return list;
		}
	}
}
