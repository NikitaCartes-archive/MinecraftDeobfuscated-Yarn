package net.minecraft.world.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.file.Path;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

public class DimensionType {
	public static final int SIZE_BITS_Y = BlockPos.SIZE_BITS_Y;
	public static final int field_33411 = 16;
	public static final int MAX_HEIGHT = (1 << SIZE_BITS_Y) - 32;
	public static final int MAX_COLUMN_HEIGHT = (MAX_HEIGHT >> 1) - 1;
	public static final int MIN_HEIGHT = MAX_COLUMN_HEIGHT - MAX_HEIGHT + 1;
	public static final int field_35478 = MAX_COLUMN_HEIGHT << 4;
	public static final int field_35479 = MIN_HEIGHT << 4;
	public static final Identifier OVERWORLD_ID = new Identifier("overworld");
	public static final Identifier THE_NETHER_ID = new Identifier("the_nether");
	public static final Identifier THE_END_ID = new Identifier("the_end");
	public static final Codec<DimensionType> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.LONG
							.optionalFieldOf("fixed_time")
							.xmap(
								optional -> (OptionalLong)optional.map(OptionalLong::of).orElseGet(OptionalLong::empty),
								optionalLong -> optionalLong.isPresent() ? Optional.of(optionalLong.getAsLong()) : Optional.empty()
							)
							.forGetter(dimensionType -> dimensionType.fixedTime),
						Codec.BOOL.fieldOf("has_skylight").forGetter(DimensionType::hasSkyLight),
						Codec.BOOL.fieldOf("has_ceiling").forGetter(DimensionType::hasCeiling),
						Codec.BOOL.fieldOf("ultrawarm").forGetter(DimensionType::isUltrawarm),
						Codec.BOOL.fieldOf("natural").forGetter(DimensionType::isNatural),
						Codec.doubleRange(1.0E-5F, 3.0E7).fieldOf("coordinate_scale").forGetter(DimensionType::getCoordinateScale),
						Codec.BOOL.fieldOf("piglin_safe").forGetter(DimensionType::isPiglinSafe),
						Codec.BOOL.fieldOf("bed_works").forGetter(DimensionType::isBedWorking),
						Codec.BOOL.fieldOf("respawn_anchor_works").forGetter(DimensionType::isRespawnAnchorWorking),
						Codec.BOOL.fieldOf("has_raids").forGetter(DimensionType::hasRaids),
						Codec.intRange(MIN_HEIGHT, MAX_COLUMN_HEIGHT).fieldOf("min_y").forGetter(DimensionType::getMinimumY),
						Codec.intRange(16, MAX_HEIGHT).fieldOf("height").forGetter(DimensionType::getHeight),
						Codec.intRange(0, MAX_HEIGHT).fieldOf("logical_height").forGetter(DimensionType::getLogicalHeight),
						TagKey.stringCodec(Registry.BLOCK_KEY).fieldOf("infiniburn").forGetter(dimensionType -> dimensionType.infiniburn),
						Identifier.CODEC.fieldOf("effects").orElse(OVERWORLD_ID).forGetter(dimensionType -> dimensionType.effects),
						Codec.FLOAT.fieldOf("ambient_light").forGetter(dimensionType -> dimensionType.ambientLight)
					)
					.apply(instance, DimensionType::new)
		)
		.comapFlatMap(DimensionType::checkHeight, Function.identity());
	private static final int field_31440 = 8;
	public static final float[] MOON_SIZES = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
	public static final RegistryKey<DimensionType> OVERWORLD_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("overworld"));
	public static final RegistryKey<DimensionType> THE_NETHER_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("the_nether"));
	public static final RegistryKey<DimensionType> THE_END_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("the_end"));
	protected static final DimensionType OVERWORLD = create(
		OptionalLong.empty(), true, false, false, true, 1.0, false, false, true, false, true, -64, 384, 384, BlockTags.INFINIBURN_OVERWORLD, OVERWORLD_ID, 0.0F
	);
	protected static final DimensionType THE_NETHER = create(
		OptionalLong.of(18000L), false, true, true, false, 8.0, false, true, false, true, false, 0, 256, 128, BlockTags.INFINIBURN_NETHER, THE_NETHER_ID, 0.1F
	);
	protected static final DimensionType THE_END = create(
		OptionalLong.of(6000L), false, false, false, false, 1.0, true, false, false, false, true, 0, 256, 256, BlockTags.INFINIBURN_END, THE_END_ID, 0.0F
	);
	public static final RegistryKey<DimensionType> OVERWORLD_CAVES_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("overworld_caves"));
	protected static final DimensionType OVERWORLD_CAVES = create(
		OptionalLong.empty(), true, true, false, true, 1.0, false, false, true, false, true, -64, 384, 384, BlockTags.INFINIBURN_OVERWORLD, OVERWORLD_ID, 0.0F
	);
	public static final Codec<RegistryEntry<DimensionType>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.DIMENSION_TYPE_KEY, CODEC);
	private final OptionalLong fixedTime;
	private final boolean hasSkyLight;
	private final boolean hasCeiling;
	private final boolean ultrawarm;
	private final boolean natural;
	private final double coordinateScale;
	private final boolean hasEnderDragonFight;
	private final boolean piglinSafe;
	private final boolean bedWorks;
	private final boolean respawnAnchorWorks;
	private final boolean hasRaids;
	private final int minimumY;
	private final int height;
	private final int logicalHeight;
	private final TagKey<Block> infiniburn;
	private final Identifier effects;
	private final float ambientLight;
	private final transient float[] brightnessByLightLevel;

	private static DataResult<DimensionType> checkHeight(DimensionType type) {
		if (type.getHeight() < 16) {
			return DataResult.error("height has to be at least 16");
		} else if (type.getMinimumY() + type.getHeight() > MAX_COLUMN_HEIGHT + 1) {
			return DataResult.error("min_y + height cannot be higher than: " + (MAX_COLUMN_HEIGHT + 1));
		} else if (type.getLogicalHeight() > type.getHeight()) {
			return DataResult.error("logical_height cannot be higher than height");
		} else if (type.getHeight() % 16 != 0) {
			return DataResult.error("height has to be multiple of 16");
		} else {
			return type.getMinimumY() % 16 != 0 ? DataResult.error("min_y has to be a multiple of 16") : DataResult.success(type);
		}
	}

	private DimensionType(
		OptionalLong fixedTime,
		boolean hasSkylight,
		boolean hasCeiling,
		boolean ultrawarm,
		boolean natural,
		double coordinateScale,
		boolean piglinSafe,
		boolean bedWorks,
		boolean respawnAnchorWorks,
		boolean hasRaids,
		int minimumY,
		int height,
		int logicalHeight,
		TagKey<Block> tagKey,
		Identifier effects,
		float ambientLight
	) {
		this(
			fixedTime,
			hasSkylight,
			hasCeiling,
			ultrawarm,
			natural,
			coordinateScale,
			false,
			piglinSafe,
			bedWorks,
			respawnAnchorWorks,
			hasRaids,
			minimumY,
			height,
			logicalHeight,
			tagKey,
			effects,
			ambientLight
		);
	}

	public static DimensionType create(
		OptionalLong fixedTime,
		boolean hasSkylight,
		boolean hasCeiling,
		boolean ultrawarm,
		boolean natural,
		double coordinateScale,
		boolean hasEnderDragonFight,
		boolean piglinSafe,
		boolean bedWorks,
		boolean respawnAnchorWorks,
		boolean hasRaids,
		int minimumY,
		int height,
		int logicalHeight,
		TagKey<Block> tagKey,
		Identifier effects,
		float ambientLight
	) {
		DimensionType dimensionType = new DimensionType(
			fixedTime,
			hasSkylight,
			hasCeiling,
			ultrawarm,
			natural,
			coordinateScale,
			hasEnderDragonFight,
			piglinSafe,
			bedWorks,
			respawnAnchorWorks,
			hasRaids,
			minimumY,
			height,
			logicalHeight,
			tagKey,
			effects,
			ambientLight
		);
		checkHeight(dimensionType).error().ifPresent(partialResult -> {
			throw new IllegalStateException(partialResult.message());
		});
		return dimensionType;
	}

	@Deprecated
	private DimensionType(
		OptionalLong fixedTime,
		boolean hasSkylight,
		boolean hasCeiling,
		boolean ultrawarm,
		boolean natural,
		double coordinateScale,
		boolean hasEnderDragonFight,
		boolean piglinSafe,
		boolean bedWorks,
		boolean respawnAnchorWorks,
		boolean hasRaids,
		int minimumY,
		int height,
		int logicalHeight,
		TagKey<Block> tagKey,
		Identifier effects,
		float ambientLight
	) {
		this.fixedTime = fixedTime;
		this.hasSkyLight = hasSkylight;
		this.hasCeiling = hasCeiling;
		this.ultrawarm = ultrawarm;
		this.natural = natural;
		this.coordinateScale = coordinateScale;
		this.hasEnderDragonFight = hasEnderDragonFight;
		this.piglinSafe = piglinSafe;
		this.bedWorks = bedWorks;
		this.respawnAnchorWorks = respawnAnchorWorks;
		this.hasRaids = hasRaids;
		this.minimumY = minimumY;
		this.height = height;
		this.logicalHeight = logicalHeight;
		this.infiniburn = tagKey;
		this.effects = effects;
		this.ambientLight = ambientLight;
		this.brightnessByLightLevel = computeBrightnessByLightLevel(ambientLight);
	}

	private static float[] computeBrightnessByLightLevel(float ambientLight) {
		float[] fs = new float[16];

		for (int i = 0; i <= 15; i++) {
			float f = (float)i / 15.0F;
			float g = f / (4.0F - 3.0F * f);
			fs[i] = MathHelper.lerp(ambientLight, g, 1.0F);
		}

		return fs;
	}

	@Deprecated
	public static DataResult<RegistryKey<World>> worldFromDimensionNbt(Dynamic<?> nbt) {
		Optional<Number> optional = nbt.asNumber().result();
		if (optional.isPresent()) {
			int i = ((Number)optional.get()).intValue();
			if (i == -1) {
				return DataResult.success(World.NETHER);
			}

			if (i == 0) {
				return DataResult.success(World.OVERWORLD);
			}

			if (i == 1) {
				return DataResult.success(World.END);
			}
		}

		return World.CODEC.parse(nbt);
	}

	public static DynamicRegistryManager.Mutable addRegistryDefaults(DynamicRegistryManager.Mutable registryManager) {
		MutableRegistry<DimensionType> mutableRegistry = registryManager.getMutable(Registry.DIMENSION_TYPE_KEY);
		mutableRegistry.add(OVERWORLD_REGISTRY_KEY, OVERWORLD, Lifecycle.stable());
		mutableRegistry.add(OVERWORLD_CAVES_REGISTRY_KEY, OVERWORLD_CAVES, Lifecycle.stable());
		mutableRegistry.add(THE_NETHER_REGISTRY_KEY, THE_NETHER, Lifecycle.stable());
		mutableRegistry.add(THE_END_REGISTRY_KEY, THE_END, Lifecycle.stable());
		return registryManager;
	}

	public static Registry<DimensionOptions> createDefaultDimensionOptions(DynamicRegistryManager registryManager, long seed) {
		return createDefaultDimensionOptions(registryManager, seed, true);
	}

	public static Registry<DimensionOptions> createDefaultDimensionOptions(DynamicRegistryManager registryManager, long seed, boolean bl) {
		MutableRegistry<DimensionOptions> mutableRegistry = new SimpleRegistry<>(Registry.DIMENSION_KEY, Lifecycle.experimental(), null);
		Registry<DimensionType> registry = registryManager.get(Registry.DIMENSION_TYPE_KEY);
		Registry<Biome> registry2 = registryManager.get(Registry.BIOME_KEY);
		Registry<ChunkGeneratorSettings> registry3 = registryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY);
		Registry<DoublePerlinNoiseSampler.NoiseParameters> registry4 = registryManager.get(Registry.NOISE_WORLDGEN);
		mutableRegistry.add(
			DimensionOptions.NETHER,
			new DimensionOptions(
				registry.getOrCreateEntry(THE_NETHER_REGISTRY_KEY),
				new NoiseChunkGenerator(
					registry4, MultiNoiseBiomeSource.Preset.NETHER.getBiomeSource(registry2, bl), seed, registry3.getOrCreateEntry(ChunkGeneratorSettings.NETHER)
				)
			),
			Lifecycle.stable()
		);
		mutableRegistry.add(
			DimensionOptions.END,
			new DimensionOptions(
				registry.getOrCreateEntry(THE_END_REGISTRY_KEY),
				new NoiseChunkGenerator(registry4, new TheEndBiomeSource(registry2, seed), seed, registry3.getOrCreateEntry(ChunkGeneratorSettings.END))
			),
			Lifecycle.stable()
		);
		return mutableRegistry;
	}

	public static double getCoordinateScaleFactor(DimensionType fromDimension, DimensionType toDimension) {
		double d = fromDimension.getCoordinateScale();
		double e = toDimension.getCoordinateScale();
		return d / e;
	}

	@Deprecated
	public String getSuffix() {
		return this.equals(THE_END) ? "_end" : "";
	}

	public static Path getSaveDirectory(RegistryKey<World> worldRef, Path worldDirectory) {
		if (worldRef == World.OVERWORLD) {
			return worldDirectory;
		} else if (worldRef == World.END) {
			return worldDirectory.resolve("DIM1");
		} else {
			return worldRef == World.NETHER
				? worldDirectory.resolve("DIM-1")
				: worldDirectory.resolve("dimensions").resolve(worldRef.getValue().getNamespace()).resolve(worldRef.getValue().getPath());
		}
	}

	public boolean hasSkyLight() {
		return this.hasSkyLight;
	}

	public boolean hasCeiling() {
		return this.hasCeiling;
	}

	public boolean isUltrawarm() {
		return this.ultrawarm;
	}

	public boolean isNatural() {
		return this.natural;
	}

	public double getCoordinateScale() {
		return this.coordinateScale;
	}

	public boolean isPiglinSafe() {
		return this.piglinSafe;
	}

	public boolean isBedWorking() {
		return this.bedWorks;
	}

	public boolean isRespawnAnchorWorking() {
		return this.respawnAnchorWorks;
	}

	public boolean hasRaids() {
		return this.hasRaids;
	}

	public int getMinimumY() {
		return this.minimumY;
	}

	public int getHeight() {
		return this.height;
	}

	public int getLogicalHeight() {
		return this.logicalHeight;
	}

	public boolean hasEnderDragonFight() {
		return this.hasEnderDragonFight;
	}

	public boolean hasFixedTime() {
		return this.fixedTime.isPresent();
	}

	public float getSkyAngle(long time) {
		double d = MathHelper.fractionalPart((double)this.fixedTime.orElse(time) / 24000.0 - 0.25);
		double e = 0.5 - Math.cos(d * Math.PI) / 2.0;
		return (float)(d * 2.0 + e) / 3.0F;
	}

	/**
	 * Gets the moon phase index of Minecraft's moon.
	 * 
	 * <p>This is typically used to determine the size of the moon that should be rendered.
	 * 
	 * @param time the time to calculate the index from
	 */
	public int getMoonPhase(long time) {
		return (int)(time / 24000L % 8L + 8L) % 8;
	}

	public float getBrightness(int lightLevel) {
		return this.brightnessByLightLevel[lightLevel];
	}

	public TagKey<Block> getInfiniburnBlocks() {
		return this.infiniburn;
	}

	/**
	 * {@return the ID of this dimension's {@linkplain net.minecraft.client.render.DimensionEffects effects}}
	 * 
	 * @see net.minecraft.client.render.DimensionEffects#byDimensionType(DimensionType)
	 */
	public Identifier getEffects() {
		return this.effects;
	}

	public boolean equals(DimensionType dimensionType) {
		return this == dimensionType
			? true
			: this.hasSkyLight == dimensionType.hasSkyLight
				&& this.hasCeiling == dimensionType.hasCeiling
				&& this.ultrawarm == dimensionType.ultrawarm
				&& this.natural == dimensionType.natural
				&& this.coordinateScale == dimensionType.coordinateScale
				&& this.hasEnderDragonFight == dimensionType.hasEnderDragonFight
				&& this.piglinSafe == dimensionType.piglinSafe
				&& this.bedWorks == dimensionType.bedWorks
				&& this.respawnAnchorWorks == dimensionType.respawnAnchorWorks
				&& this.hasRaids == dimensionType.hasRaids
				&& this.minimumY == dimensionType.minimumY
				&& this.height == dimensionType.height
				&& this.logicalHeight == dimensionType.logicalHeight
				&& Float.compare(dimensionType.ambientLight, this.ambientLight) == 0
				&& this.fixedTime.equals(dimensionType.fixedTime)
				&& this.infiniburn.equals(dimensionType.infiniburn)
				&& this.effects.equals(dimensionType.effects);
	}
}
