package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeAccessType;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.biome.source.DirectBiomeAccessType;
import net.minecraft.world.biome.source.HorizontalVoronoiBiomeAccessType;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceConfig;
import net.minecraft.world.biome.source.VoronoiBiomeAccessType;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.CavesChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.FloatingIslandsChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;

public class class_5014 {
	private static final BiomeAccessType[] field_23483 = new BiomeAccessType[]{
		VoronoiBiomeAccessType.INSTANCE, HorizontalVoronoiBiomeAccessType.INSTANCE, DirectBiomeAccessType.INSTANCE
	};
	private static final Int2ObjectMap<IntFunction<DimensionType>> field_23484 = new Int2ObjectOpenHashMap<>();

	private static IntFunction<DimensionType> method_26506(
		BiFunction<World, DimensionType, ? extends Dimension> biFunction, boolean bl, BiomeAccessType biomeAccessType
	) {
		return i -> new DimensionType(i, "_" + i, "DIM" + i, biFunction, bl, biomeAccessType);
	}

	private static IntFunction<DimensionType> method_26508(
		BiFunction<World, DimensionType, ? extends Dimension> biFunction, boolean bl, BiomeAccessType biomeAccessType
	) {
		return i -> new DimensionType(i, "_" + i, "DIM" + i, biFunction, bl, biomeAccessType) {
				@Override
				public boolean method_26523() {
					return true;
				}
			};
	}

	public static DimensionType method_26504(int i) {
		IntFunction<DimensionType> intFunction = field_23484.get(i);
		if (intFunction != null) {
			return (DimensionType)intFunction.apply(i);
		} else {
			ChunkRandom chunkRandom = new ChunkRandom((long)i);
			BiomeAccessType biomeAccessType = field_23483[chunkRandom.nextInt(field_23483.length)];
			boolean bl = chunkRandom.nextBoolean();
			int j = chunkRandom.nextInt();
			return new DimensionType(i, "_" + i, "DIM" + i, (world, dimensionType) -> class_5014.class_5015.method_26511(world, dimensionType, j), bl, biomeAccessType);
		}
	}

	static {
		field_23484.put(741472677, method_26506(class_5017::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(236157810, method_26506(class_5019::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(1896587401, method_26506(class_5021::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(726931095, method_26506(class_5027::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(233542201, method_26506(class_5029::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(669175628, method_26506(class_5031::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(1929426645, method_26506(class_5034::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(378547252, method_26506(class_5036::new, true, HorizontalVoronoiBiomeAccessType.INSTANCE));
		field_23484.put(94341406, method_26506(class_5037::new, true, HorizontalVoronoiBiomeAccessType.INSTANCE));
		field_23484.put(1174283440, method_26506(class_5038::new, true, HorizontalVoronoiBiomeAccessType.INSTANCE));
		field_23484.put(1210674279, method_26506(class_5039::new, true, HorizontalVoronoiBiomeAccessType.INSTANCE));
		field_23484.put(344885676, method_26506(class_5040::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(31674686, method_26508(class_5042::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(2114493792, method_26506(class_5044.method_26547(new Vector3f(1.0F, 0.0F, 0.0F)), true, HorizontalVoronoiBiomeAccessType.INSTANCE));
		field_23484.put(1143264807, method_26506(class_5044.method_26547(new Vector3f(0.0F, 1.0F, 0.0F)), true, HorizontalVoronoiBiomeAccessType.INSTANCE));
		field_23484.put(1049823113, method_26506(class_5044.method_26547(new Vector3f(0.0F, 0.0F, 1.0F)), true, HorizontalVoronoiBiomeAccessType.INSTANCE));
		field_23484.put(1011847535, method_26506(class_5045::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(1902968744, method_26506(class_5047::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(264458659, method_26506(class_5049::new, true, HorizontalVoronoiBiomeAccessType.INSTANCE));
		field_23484.put(1201319931, method_26506(class_5051::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(1113696725, method_26506(class_5053::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(1384344230, method_26506(class_5055::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(214387762, method_26506(class_5057::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(1098962767, method_26506(class_5059::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(927632079, method_26506(class_5061::new, true, HorizontalVoronoiBiomeAccessType.INSTANCE));
		field_23484.put(307219718, method_26506(class_5063::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(545072168, method_26506(class_5065::new, true, HorizontalVoronoiBiomeAccessType.INSTANCE));
		field_23484.put(1834117187, method_26506(class_5067::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(661885389, method_26506(class_5069::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(1036032341, method_26506(class_5071::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(484336196, method_26506(class_5073::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(1059552697, method_26506(class_5075::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(907661935, method_26506(class_5077::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(1141490659, method_26506(class_5079::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(1028465021, method_26506(class_5081::new, true, HorizontalVoronoiBiomeAccessType.INSTANCE));
		field_23484.put(2003598857, method_26506(class_5083::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(985130845, method_26506(class_5085::new, true, HorizontalVoronoiBiomeAccessType.INSTANCE));
		field_23484.put(107712651, method_26506(class_5087::new, true, DirectBiomeAccessType.INSTANCE));
		field_23484.put(251137100, method_26506(class_5089::new, true, HorizontalVoronoiBiomeAccessType.INSTANCE));
		field_23484.put(1537997313, method_26506(class_5091::new, true, HorizontalVoronoiBiomeAccessType.INSTANCE));
		field_23484.put(1916276638, method_26506(class_5093::new, true, HorizontalVoronoiBiomeAccessType.INSTANCE));
		field_23484.put(894945615, method_26506(class_5095::new, true, HorizontalVoronoiBiomeAccessType.INSTANCE));
		field_23484.put(1791460938, method_26506(class_5097::new, true, DirectBiomeAccessType.INSTANCE));
	}

	static class class_5015 extends Dimension {
		private final boolean field_23485;
		private final float field_23486;
		private final double field_23487;
		private final boolean field_23488;
		private final boolean field_23489;
		private final float field_23490;
		private final Vector3f field_23491;
		private final float field_23492;
		private final Vector3f field_23493;
		private final Vec3d field_23494;
		private final Vec3d field_23495;
		private final boolean field_23496;
		private final Supplier<ChunkGenerator> field_23497;
		@Nullable
		private final Object2FloatMap<Direction> field_23498;
		private final int field_23499;
		@Nullable
		private final Vector3f[] field_23500;
		@Nullable
		private final Vector3f[] field_23501;

		public static Dimension method_26511(World world, DimensionType dimensionType, int i) {
			return new class_5014.class_5015(world, dimensionType, new ChunkRandom((long)i));
		}

		private class_5015(World world, DimensionType dimensionType, ChunkRandom chunkRandom) {
			super(world, dimensionType, chunkRandom.nextFloat());
			this.waterVaporizes = chunkRandom.nextInt(5) == 0;
			this.isNether = chunkRandom.nextBoolean();
			this.field_23485 = chunkRandom.nextBoolean();
			this.field_23486 = chunkRandom.nextFloat();
			this.field_23488 = chunkRandom.nextBoolean();
			this.field_23489 = chunkRandom.nextInt(8) == 0;
			this.field_23487 = Math.max(100.0, chunkRandom.nextGaussian() * 3.0 * 24000.0);
			this.field_23494 = new Vec3d(chunkRandom.nextDouble(), chunkRandom.nextDouble(), chunkRandom.nextDouble());
			this.field_23495 = new Vec3d(chunkRandom.nextDouble(), chunkRandom.nextDouble(), chunkRandom.nextDouble());
			this.field_23496 = chunkRandom.nextBoolean();
			this.field_23490 = (float)Math.max(5.0, 30.0 * (1.0 + 4.0 * chunkRandom.nextGaussian()));
			this.field_23492 = (float)Math.max(5.0, 20.0 * (1.0 + 4.0 * chunkRandom.nextGaussian()));
			this.field_23491 = this.method_26513(chunkRandom);
			this.field_23493 = this.method_26513(chunkRandom);
			this.field_23499 = chunkRandom.nextInt(255);
			MultiNoiseBiomeSourceConfig multiNoiseBiomeSourceConfig = new MultiNoiseBiomeSourceConfig((long)chunkRandom.nextInt());
			Map<Biome, List<Biome.MixedNoisePoint>> map = (Map<Biome, List<Biome.MixedNoisePoint>>)IntStream.range(2, chunkRandom.nextInt(15))
				.mapToObj(i -> Math.abs(chunkRandom.nextInt()))
				.collect(Collectors.toMap(Registry.BIOME::get, integer -> Biome.MixedNoisePoint.method_26459(chunkRandom)));

			while (chunkRandom.nextBoolean()) {
				map.put(Biomes.method_26470(chunkRandom), Biome.MixedNoisePoint.method_26459(chunkRandom));
			}

			multiNoiseBiomeSourceConfig.withBiomes(map);
			BiomeSource biomeSource = BiomeSourceType.MULTI_NOISE.applyConfig(multiNoiseBiomeSourceConfig);
			if (chunkRandom.nextInt(7) == 0) {
				this.field_23498 = new Object2FloatOpenHashMap<>();

				for (Direction direction : Direction.values()) {
					this.field_23498.put(direction, (float)MathHelper.clamp((double)super.method_26497(direction, true) + chunkRandom.nextGaussian(), 0.0, 1.0));
				}
			} else {
				this.field_23498 = null;
			}

			if (chunkRandom.nextInt(4) == 0) {
				this.field_23500 = this.method_26520(chunkRandom);
			} else {
				this.field_23500 = null;
			}

			if (chunkRandom.nextInt(3) == 0) {
				this.field_23501 = this.method_26518(chunkRandom);
			} else {
				this.field_23501 = null;
			}

			this.field_23497 = method_26512(world, chunkRandom, biomeSource);
		}

		private Vector3f method_26513(ChunkRandom chunkRandom) {
			return chunkRandom.nextBoolean() ? new Vector3f(chunkRandom.nextFloat(), chunkRandom.nextFloat(), chunkRandom.nextFloat()) : new Vector3f(1.0F, 1.0F, 1.0F);
		}

		private Vector3f[] method_26518(Random random) {
			int i = random.nextInt(6) + 2;
			Vector3f[] vector3fs = new Vector3f[i];

			for (int j = 0; j < i; j++) {
				vector3fs[j] = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
			}

			return vector3fs;
		}

		private static float method_26517(OctavePerlinNoiseSampler octavePerlinNoiseSampler, int i, int j) {
			return (float)octavePerlinNoiseSampler.sample((double)i, (double)j, 0.0);
		}

		private Vector3f[] method_26520(ChunkRandom chunkRandom) {
			Vector3f[] vector3fs = new Vector3f[256];
			OctavePerlinNoiseSampler octavePerlinNoiseSampler = new OctavePerlinNoiseSampler(chunkRandom, IntStream.rangeClosed(-3, 0));
			OctavePerlinNoiseSampler octavePerlinNoiseSampler2 = new OctavePerlinNoiseSampler(chunkRandom, IntStream.rangeClosed(-2, 4));
			OctavePerlinNoiseSampler octavePerlinNoiseSampler3 = new OctavePerlinNoiseSampler(chunkRandom, IntStream.rangeClosed(-5, 0));

			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					Vector3f vector3f = new Vector3f(
						method_26517(octavePerlinNoiseSampler, i, j), method_26517(octavePerlinNoiseSampler2, i, j), method_26517(octavePerlinNoiseSampler3, i, j)
					);
					vector3fs[i * 16 + j] = vector3f;
				}
			}

			return vector3fs;
		}

		private static Supplier<ChunkGenerator> method_26512(World world, Random random, BiomeSource biomeSource) {
			int i = random.nextInt();
			switch (random.nextInt(3)) {
				case 0:
					return () -> ChunkGeneratorType.SURFACE.create(world, biomeSource, new OverworldChunkGeneratorConfig(new ChunkRandom((long)i)));
				case 1:
					return () -> ChunkGeneratorType.CAVES.create(world, biomeSource, new CavesChunkGeneratorConfig(new ChunkRandom((long)i)));
				default:
					return () -> ChunkGeneratorType.FLOATING_ISLANDS.create(world, biomeSource, new FloatingIslandsChunkGeneratorConfig(new ChunkRandom((long)i)));
			}
		}

		@Override
		public ChunkGenerator<?> createChunkGenerator() {
			return (ChunkGenerator<?>)this.field_23497.get();
		}

		@Nullable
		@Override
		public BlockPos getSpawningBlockInChunk(ChunkPos chunkPos, boolean checkMobSpawnValidity) {
			return OverworldDimension.method_26526(this.world, chunkPos, checkMobSpawnValidity);
		}

		@Nullable
		@Override
		public BlockPos getTopSpawningBlockPosition(int x, int z, boolean checkMobSpawnValidity) {
			return OverworldDimension.method_26525(this.world, x, z, checkMobSpawnValidity);
		}

		@Override
		public float getSkyAngle(long timeOfDay, float tickDelta) {
			return this.field_23485 ? this.field_23486 : OverworldDimension.method_26524(timeOfDay, this.field_23487);
		}

		@Override
		public boolean hasVisibleSky() {
			return this.field_23488;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public Vec3d modifyFogColor(Vec3d vec3d, float tickDelta) {
			return vec3d.multiply(
				(double)tickDelta * this.field_23494.x + this.field_23495.x,
				(double)tickDelta * this.field_23494.y + this.field_23495.y,
				(double)tickDelta * this.field_23494.z + this.field_23495.z
			);
		}

		@Override
		public boolean canPlayersSleep() {
			return false;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public boolean isFogThick(int x, int z) {
			return this.field_23496;
		}

		@Override
		public float method_26497(Direction direction, boolean bl) {
			return this.field_23498 != null && bl ? this.field_23498.getFloat(direction) : super.method_26497(direction, bl);
		}

		@Environment(EnvType.CLIENT)
		@Override
		public void method_26493(int i, int j, Vector3f vector3f) {
			if (this.field_23500 != null) {
				vector3f.add(this.field_23500[j * 16 + i]);
				vector3f.clamp(0.0F, 1.0F);
			}
		}

		@Environment(EnvType.CLIENT)
		@Override
		public Vector3f method_26495(BlockState blockState, BlockPos blockPos) {
			if (this.field_23501 == null) {
				return super.method_26495(blockState, blockPos);
			} else {
				int i = Block.STATE_IDS.getId(blockState);
				return this.field_23501[i % this.field_23501.length];
			}
		}

		@Environment(EnvType.CLIENT)
		@Override
		public <T extends LivingEntity> Vector3f method_26494(T livingEntity) {
			if (this.field_23501 == null) {
				return super.method_26494(livingEntity);
			} else {
				int i = Registry.ENTITY_TYPE.getRawId(livingEntity.getType());
				return this.field_23501[i % this.field_23501.length];
			}
		}

		@Override
		public <T> Dynamic<T> method_26496(DynamicOps<T> dynamicOps) {
			T object = dynamicOps.createMap(
				(Map<T, T>)Stream.of(Direction.values())
					.collect(
						ImmutableMap.toImmutableMap(
							direction -> dynamicOps.createString(direction.getName()), direction -> dynamicOps.createDouble((double)this.method_26497(direction, true))
						)
					)
			);
			return super.method_26496(dynamicOps)
				.merge(
					new Dynamic<>(
						dynamicOps,
						dynamicOps.createMap(
							ImmutableMap.<T, T>builder()
								.put(dynamicOps.createString("foggy"), dynamicOps.createBoolean(this.field_23496))
								.put(
									dynamicOps.createString("fogA"),
									dynamicOps.createList(Stream.of(this.field_23494.x, this.field_23494.y, this.field_23494.z).map(dynamicOps::createDouble))
								)
								.put(
									dynamicOps.createString("fogB"),
									dynamicOps.createList(Stream.of(this.field_23495.x, this.field_23495.y, this.field_23495.z).map(dynamicOps::createDouble))
								)
								.put(dynamicOps.createString("tickPerDay"), dynamicOps.createDouble(this.field_23487))
								.put(dynamicOps.createString("shade"), object)
								.build()
						)
					)
				);
		}

		@Environment(EnvType.CLIENT)
		@Override
		public boolean method_26499() {
			return this.field_23489;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public float method_26500() {
			return this.field_23490;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public Vector3f method_26502() {
			return this.field_23491;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public float method_26501() {
			return this.field_23492;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public Vector3f method_26503() {
			return this.field_23493;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public float getCloudHeight() {
			return (float)this.field_23499;
		}
	}
}
