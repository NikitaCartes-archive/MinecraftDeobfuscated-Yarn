package net.minecraft.world.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.File;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccessType;
import net.minecraft.world.biome.source.HorizontalVoronoiBiomeAccessType;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.VoronoiBiomeAccessType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

public class DimensionType {
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
					Codec.intRange(0, 256).fieldOf("logical_height").forGetter(DimensionType::getLogicalHeight),
					Identifier.CODEC.fieldOf("infiniburn").forGetter(dimensionType -> dimensionType.infiniburn),
					Identifier.CODEC.fieldOf("effects").orElse(OVERWORLD_ID).forGetter(dimensionType -> dimensionType.skyProperties),
					Codec.FLOAT.fieldOf("ambient_light").forGetter(dimensionType -> dimensionType.ambientLight)
				)
				.apply(instance, DimensionType::new)
	);
	public static final float[] MOON_SIZES = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
	public static final RegistryKey<DimensionType> OVERWORLD_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("overworld"));
	public static final RegistryKey<DimensionType> THE_NETHER_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("the_nether"));
	public static final RegistryKey<DimensionType> THE_END_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("the_end"));
	protected static final DimensionType OVERWORLD = new DimensionType(
		OptionalLong.empty(),
		true,
		false,
		false,
		true,
		1.0,
		false,
		false,
		true,
		false,
		true,
		256,
		HorizontalVoronoiBiomeAccessType.INSTANCE,
		BlockTags.INFINIBURN_OVERWORLD.getId(),
		OVERWORLD_ID,
		0.0F
	);
	protected static final DimensionType THE_NETHER = new DimensionType(
		OptionalLong.of(18000L),
		false,
		true,
		true,
		false,
		8.0,
		false,
		true,
		false,
		true,
		false,
		128,
		VoronoiBiomeAccessType.INSTANCE,
		BlockTags.INFINIBURN_NETHER.getId(),
		THE_NETHER_ID,
		0.1F
	);
	protected static final DimensionType THE_END = new DimensionType(
		OptionalLong.of(6000L),
		false,
		false,
		false,
		false,
		1.0,
		true,
		false,
		false,
		false,
		true,
		256,
		VoronoiBiomeAccessType.INSTANCE,
		BlockTags.INFINIBURN_END.getId(),
		THE_END_ID,
		0.0F
	);
	public static final RegistryKey<DimensionType> OVERWORLD_CAVES_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("overworld_caves"));
	protected static final DimensionType OVERWORLD_CAVES = new DimensionType(
		OptionalLong.empty(),
		true,
		true,
		false,
		true,
		1.0,
		false,
		false,
		true,
		false,
		true,
		256,
		HorizontalVoronoiBiomeAccessType.INSTANCE,
		BlockTags.INFINIBURN_OVERWORLD.getId(),
		OVERWORLD_ID,
		0.0F
	);
	public static final Codec<Supplier<DimensionType>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.DIMENSION_TYPE_KEY, CODEC);
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
	private final int logicalHeight;
	private final BiomeAccessType biomeAccessType;
	private final Identifier infiniburn;
	private final Identifier skyProperties;
	private final float ambientLight;
	private final transient float[] field_24767;

	protected DimensionType(
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
		int logicalHeight,
		Identifier infiniburn,
		Identifier skyProperties,
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
			logicalHeight,
			VoronoiBiomeAccessType.INSTANCE,
			infiniburn,
			skyProperties,
			ambientLight
		);
	}

	protected DimensionType(
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
		int logicalHeight,
		BiomeAccessType biomeAccessType,
		Identifier infiniburn,
		Identifier skyProperties,
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
		this.logicalHeight = logicalHeight;
		this.biomeAccessType = biomeAccessType;
		this.infiniburn = infiniburn;
		this.skyProperties = skyProperties;
		this.ambientLight = ambientLight;
		this.field_24767 = method_28515(ambientLight);
	}

	private static float[] method_28515(float f) {
		float[] fs = new float[16];

		for (int i = 0; i <= 15; i++) {
			float g = (float)i / 15.0F;
			float h = g / (4.0F - 3.0F * g);
			fs[i] = MathHelper.lerp(f, h, 1.0F);
		}

		return fs;
	}

	@Deprecated
	public static DataResult<RegistryKey<World>> method_28521(Dynamic<?> dynamic) {
		Optional<Number> optional = dynamic.asNumber().result();
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

		return World.CODEC.parse(dynamic);
	}

	public static DynamicRegistryManager.Impl addRegistryDefaults(DynamicRegistryManager.Impl registryManager) {
		MutableRegistry<DimensionType> mutableRegistry = registryManager.get(Registry.DIMENSION_TYPE_KEY);
		mutableRegistry.add(OVERWORLD_REGISTRY_KEY, OVERWORLD, Lifecycle.stable());
		mutableRegistry.add(OVERWORLD_CAVES_REGISTRY_KEY, OVERWORLD_CAVES, Lifecycle.stable());
		mutableRegistry.add(THE_NETHER_REGISTRY_KEY, THE_NETHER, Lifecycle.stable());
		mutableRegistry.add(THE_END_REGISTRY_KEY, THE_END, Lifecycle.stable());
		return registryManager;
	}

	private static ChunkGenerator createEndGenerator(Registry<Biome> biomeRegistry, Registry<ChunkGeneratorSettings> chunkGeneratorSettingsRegistry, long seed) {
		return new NoiseChunkGenerator(
			new TheEndBiomeSource(biomeRegistry, seed), seed, () -> chunkGeneratorSettingsRegistry.method_31140(ChunkGeneratorSettings.END)
		);
	}

	private static ChunkGenerator createNetherGenerator(Registry<Biome> biomeRegistry, Registry<ChunkGeneratorSettings> chunkGeneratorSettingsRegistry, long seed) {
		return new NoiseChunkGenerator(
			MultiNoiseBiomeSource.Preset.NETHER.getBiomeSource(biomeRegistry, seed),
			seed,
			() -> chunkGeneratorSettingsRegistry.method_31140(ChunkGeneratorSettings.NETHER)
		);
	}

	public static SimpleRegistry<DimensionOptions> method_28517(
		Registry<DimensionType> registry, Registry<Biome> registry2, Registry<ChunkGeneratorSettings> registry3, long l
	) {
		SimpleRegistry<DimensionOptions> simpleRegistry = new SimpleRegistry<>(Registry.DIMENSION_OPTIONS, Lifecycle.experimental());
		simpleRegistry.add(
			DimensionOptions.NETHER,
			new DimensionOptions(() -> registry.method_31140(THE_NETHER_REGISTRY_KEY), createNetherGenerator(registry2, registry3, l)),
			Lifecycle.stable()
		);
		simpleRegistry.add(
			DimensionOptions.END,
			new DimensionOptions(() -> registry.method_31140(THE_END_REGISTRY_KEY), createEndGenerator(registry2, registry3, l)),
			Lifecycle.stable()
		);
		return simpleRegistry;
	}

	public static double method_31109(DimensionType dimensionType, DimensionType dimensionType2) {
		double d = dimensionType.getCoordinateScale();
		double e = dimensionType2.getCoordinateScale();
		return d / e;
	}

	@Deprecated
	public String getSuffix() {
		return this.equals(THE_END) ? "_end" : "";
	}

	public static File getSaveDirectory(RegistryKey<World> worldRef, File root) {
		if (worldRef == World.OVERWORLD) {
			return root;
		} else if (worldRef == World.END) {
			return new File(root, "DIM1");
		} else {
			return worldRef == World.NETHER
				? new File(root, "DIM-1")
				: new File(root, "dimensions/" + worldRef.getValue().getNamespace() + "/" + worldRef.getValue().getPath());
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

	public int getLogicalHeight() {
		return this.logicalHeight;
	}

	public boolean hasEnderDragonFight() {
		return this.hasEnderDragonFight;
	}

	public BiomeAccessType getBiomeAccessType() {
		return this.biomeAccessType;
	}

	public boolean hasFixedTime() {
		return this.fixedTime.isPresent();
	}

	public float method_28528(long time) {
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

	public float method_28516(int i) {
		return this.field_24767[i];
	}

	public Tag<Block> getInfiniburnBlocks() {
		Tag<Block> tag = BlockTags.getTagGroup().getTag(this.infiniburn);
		return (Tag<Block>)(tag != null ? tag : BlockTags.INFINIBURN_OVERWORLD);
	}

	@Environment(EnvType.CLIENT)
	public Identifier getSkyProperties() {
		return this.skyProperties;
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
				&& this.logicalHeight == dimensionType.logicalHeight
				&& Float.compare(dimensionType.ambientLight, this.ambientLight) == 0
				&& this.fixedTime.equals(dimensionType.fixedTime)
				&& this.biomeAccessType.equals(dimensionType.biomeAccessType)
				&& this.infiniburn.equals(dimensionType.infiniburn)
				&& this.skyProperties.equals(dimensionType.skyProperties);
	}
}
