package net.minecraft.world.dimension;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.source.BiomeAccessType;
import net.minecraft.world.biome.source.HorizontalVoronoiBiomeAccessType;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.VoronoiBiomeAccessType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;

public class DimensionType {
	public static final Codec<RegistryKey<DimensionType>> field_24751 = Identifier.field_25139
		.xmap(RegistryKey.createKeyFactory(Registry.DIMENSION_TYPE_KEY), RegistryKey::getValue);
	private static final Codec<DimensionType> CODEC = RecordCodecBuilder.create(
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
					Codec.BOOL.fieldOf("shrunk").forGetter(DimensionType::isShrunk),
					Codec.FLOAT.fieldOf("ambient_light").forGetter(dimensionType -> dimensionType.ambientLight)
				)
				.apply(instance, DimensionType::new)
	);
	public static final float[] field_24752 = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
	public static final RegistryKey<DimensionType> OVERWORLD_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("overworld"));
	public static final RegistryKey<DimensionType> THE_NETHER_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("the_nether"));
	public static final RegistryKey<DimensionType> THE_END_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("the_end"));
	private static final LinkedHashSet<RegistryKey<DimensionType>> DIMENSION_TYPES = Sets.newLinkedHashSet(
		ImmutableList.of(OVERWORLD_REGISTRY_KEY, THE_NETHER_REGISTRY_KEY, THE_END_REGISTRY_KEY)
	);
	private static final DimensionType OVERWORLD = new DimensionType(
		"", OptionalLong.empty(), true, false, false, true, false, false, HorizontalVoronoiBiomeAccessType.INSTANCE, Optional.of(OVERWORLD_REGISTRY_KEY), 0.0F
	);
	private static final DimensionType THE_NETHER = new DimensionType(
		"_nether", OptionalLong.of(18000L), false, true, true, false, true, false, VoronoiBiomeAccessType.INSTANCE, Optional.of(THE_NETHER_REGISTRY_KEY), 0.1F
	);
	private static final DimensionType THE_END = new DimensionType(
		"_end", OptionalLong.of(6000L), false, false, false, false, false, true, VoronoiBiomeAccessType.INSTANCE, Optional.of(THE_END_REGISTRY_KEY), 0.0F
	);
	private static final Map<RegistryKey<DimensionType>, DimensionType> field_24759 = ImmutableMap.of(
		OVERWORLD_REGISTRY_KEY, OVERWORLD, THE_NETHER_REGISTRY_KEY, THE_NETHER, THE_END_REGISTRY_KEY, THE_END
	);
	private static final Codec<DimensionType> field_24760 = field_24751.<DimensionType>flatXmap(
			registryKey -> (DataResult)Optional.ofNullable(field_24759.get(registryKey))
					.map(DataResult::success)
					.orElseGet(() -> DataResult.error("Unknown builtin dimension: " + registryKey)),
			dimensionType -> (DataResult)dimensionType.field_24765
					.map(DataResult::success)
					.orElseGet(() -> DataResult.error("Unknown builtin dimension: " + dimensionType))
		)
		.stable();
	public static final Codec<DimensionType> field_24756 = Codec.either(field_24760, CODEC)
		.flatXmap(
			either -> either.map(dimensionType -> DataResult.success(dimensionType, Lifecycle.stable()), DataResult::success),
			dimensionType -> dimensionType.field_24765.isPresent()
					? DataResult.success(Either.left(dimensionType), Lifecycle.stable())
					: DataResult.success(Either.right(dimensionType))
		);
	private final String suffix;
	private final OptionalLong fixedTime;
	private final boolean hasSkyLight;
	private final boolean hasCeiling;
	private final boolean ultrawarm;
	private final boolean natural;
	private final boolean shrunk;
	private final boolean hasEnderDragonFight;
	private final BiomeAccessType biomeAccessType;
	private final Optional<RegistryKey<DimensionType>> field_24765;
	private final float ambientLight;
	private final transient float[] field_24767;

	protected DimensionType(
		OptionalLong fixedTime, boolean hasSkylight, boolean hasCeiling, boolean ultrawarm, boolean natural, boolean shrunk, float ambientLight
	) {
		this("", fixedTime, hasSkylight, hasCeiling, ultrawarm, natural, shrunk, false, VoronoiBiomeAccessType.INSTANCE, Optional.empty(), ambientLight);
	}

	protected DimensionType(
		String suffix,
		OptionalLong fixedTime,
		boolean hasSkylight,
		boolean hasCeiling,
		boolean ultrawarm,
		boolean natural,
		boolean shrunk,
		boolean hasEnderDragonFight,
		BiomeAccessType biomeAccessType,
		Optional<RegistryKey<DimensionType>> optional,
		float ambientLight
	) {
		this.suffix = suffix;
		this.fixedTime = fixedTime;
		this.hasSkyLight = hasSkylight;
		this.hasCeiling = hasCeiling;
		this.ultrawarm = ultrawarm;
		this.natural = natural;
		this.shrunk = shrunk;
		this.hasEnderDragonFight = hasEnderDragonFight;
		this.biomeAccessType = biomeAccessType;
		this.field_24765 = optional;
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
	public static DataResult<RegistryKey<DimensionType>> method_28521(Dynamic<?> dynamic) {
		DataResult<Number> dataResult = dynamic.asNumber();
		if (dataResult.result().equals(Optional.of(-1))) {
			return DataResult.success(THE_NETHER_REGISTRY_KEY);
		} else if (dataResult.result().equals(Optional.of(0))) {
			return DataResult.success(OVERWORLD_REGISTRY_KEY);
		} else {
			return dataResult.result().equals(Optional.of(1))
				? DataResult.success(THE_END_REGISTRY_KEY)
				: Identifier.field_25139.<RegistryKey<DimensionType>>xmap(RegistryKey.createKeyFactory(Registry.DIMENSION_TYPE_KEY), RegistryKey::getValue).parse(dynamic);
		}
	}

	@Environment(EnvType.CLIENT)
	public static DimensionTracker.Modifiable addDefaults(DimensionTracker.Modifiable tracker) {
		tracker.add(OVERWORLD_REGISTRY_KEY, OVERWORLD);
		tracker.add(THE_NETHER_REGISTRY_KEY, THE_NETHER);
		tracker.add(THE_END_REGISTRY_KEY, THE_END);
		return tracker;
	}

	private static ChunkGenerator method_28533(long l) {
		return new SurfaceChunkGenerator(new TheEndBiomeSource(l), l, ChunkGeneratorType.Preset.END.getChunkGeneratorType());
	}

	private static ChunkGenerator method_28535(long l) {
		return new SurfaceChunkGenerator(MultiNoiseBiomeSource.class_5305.field_24723.method_28469(l), l, ChunkGeneratorType.Preset.NETHER.getChunkGeneratorType());
	}

	public static LinkedHashMap<RegistryKey<DimensionType>, Pair<DimensionType, ChunkGenerator>> method_28517(long l) {
		LinkedHashMap<RegistryKey<DimensionType>, Pair<DimensionType, ChunkGenerator>> linkedHashMap = Maps.newLinkedHashMap();
		linkedHashMap.put(THE_NETHER_REGISTRY_KEY, Pair.of(THE_NETHER, method_28535(l)));
		linkedHashMap.put(THE_END_REGISTRY_KEY, Pair.of(THE_END, method_28533(l)));
		return linkedHashMap;
	}

	public static DimensionType getDefaultDimensionType() {
		return OVERWORLD;
	}

	public static boolean method_28518(long l, LinkedHashMap<RegistryKey<DimensionType>, Pair<DimensionType, ChunkGenerator>> linkedHashMap) {
		List<Entry<RegistryKey<DimensionType>, Pair<DimensionType, ChunkGenerator>>> list = Lists.<Entry<RegistryKey<DimensionType>, Pair<DimensionType, ChunkGenerator>>>newArrayList(
			linkedHashMap.entrySet()
		);
		if (list.size() != 3) {
			return false;
		} else {
			Entry<RegistryKey<DimensionType>, Pair<DimensionType, ChunkGenerator>> entry = (Entry<RegistryKey<DimensionType>, Pair<DimensionType, ChunkGenerator>>)list.get(
				0
			);
			Entry<RegistryKey<DimensionType>, Pair<DimensionType, ChunkGenerator>> entry2 = (Entry<RegistryKey<DimensionType>, Pair<DimensionType, ChunkGenerator>>)list.get(
				1
			);
			Entry<RegistryKey<DimensionType>, Pair<DimensionType, ChunkGenerator>> entry3 = (Entry<RegistryKey<DimensionType>, Pair<DimensionType, ChunkGenerator>>)list.get(
				2
			);
			if (entry.getKey() != OVERWORLD_REGISTRY_KEY || entry2.getKey() != THE_NETHER_REGISTRY_KEY || entry3.getKey() != THE_END_REGISTRY_KEY) {
				return false;
			} else if (((Pair)entry.getValue()).getFirst() != OVERWORLD
				|| ((Pair)entry2.getValue()).getFirst() != THE_NETHER
				|| ((Pair)entry3.getValue()).getFirst() != THE_END) {
				return false;
			} else if (((Pair)entry2.getValue()).getSecond() instanceof SurfaceChunkGenerator && ((Pair)entry3.getValue()).getSecond() instanceof SurfaceChunkGenerator) {
				SurfaceChunkGenerator surfaceChunkGenerator = (SurfaceChunkGenerator)((Pair)entry2.getValue()).getSecond();
				SurfaceChunkGenerator surfaceChunkGenerator2 = (SurfaceChunkGenerator)((Pair)entry3.getValue()).getSecond();
				if (!surfaceChunkGenerator.method_28548(l, ChunkGeneratorType.Preset.NETHER)) {
					return false;
				} else if (!surfaceChunkGenerator2.method_28548(l, ChunkGeneratorType.Preset.END)) {
					return false;
				} else if (!(surfaceChunkGenerator.getBiomeSource() instanceof MultiNoiseBiomeSource)) {
					return false;
				} else {
					MultiNoiseBiomeSource multiNoiseBiomeSource = (MultiNoiseBiomeSource)surfaceChunkGenerator.getBiomeSource();
					if (!multiNoiseBiomeSource.method_28462(l)) {
						return false;
					} else if (!(surfaceChunkGenerator2.getBiomeSource() instanceof TheEndBiomeSource)) {
						return false;
					} else {
						TheEndBiomeSource theEndBiomeSource = (TheEndBiomeSource)surfaceChunkGenerator2.getBiomeSource();
						return theEndBiomeSource.method_28479(l);
					}
				}
			} else {
				return false;
			}
		}
	}

	public String getSuffix() {
		return this.suffix;
	}

	public static File getSaveDirectory(RegistryKey<?> registryKey, File root) {
		if (Objects.equals(registryKey, OVERWORLD_REGISTRY_KEY)) {
			return root;
		} else if (Objects.equals(registryKey, THE_END_REGISTRY_KEY)) {
			return new File(root, "DIM1");
		} else {
			return Objects.equals(registryKey, THE_NETHER_REGISTRY_KEY)
				? new File(root, "DIM-1")
				: new File(root, "dimensions/" + registryKey.getValue().getNamespace() + "/" + registryKey.getValue().getPath());
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

	public boolean isShrunk() {
		return this.shrunk;
	}

	public boolean hasEnderDragonFight() {
		return this.hasEnderDragonFight;
	}

	public BiomeAccessType getBiomeAccessType() {
		return this.biomeAccessType;
	}

	public float method_28528(long l) {
		double d = MathHelper.fractionalPart((double)this.fixedTime.orElse(l) / 24000.0 - 0.25);
		double e = 0.5 - Math.cos(d * Math.PI) / 2.0;
		return (float)(d * 2.0 + e) / 3.0F;
	}

	public int method_28531(long l) {
		return (int)(l / 24000L % 8L + 8L) % 8;
	}

	public float method_28516(int i) {
		return this.field_24767[i];
	}

	public boolean isOverworld() {
		return this == OVERWORLD;
	}

	public boolean isNether() {
		return this == THE_NETHER;
	}

	public boolean isEnd() {
		return this == THE_END;
	}

	public static LinkedHashMap<RegistryKey<DimensionType>, Pair<DimensionType, ChunkGenerator>> method_28524(
		Map<RegistryKey<DimensionType>, Pair<DimensionType, ChunkGenerator>> map
	) {
		LinkedHashMap<RegistryKey<DimensionType>, Pair<DimensionType, ChunkGenerator>> linkedHashMap = Maps.newLinkedHashMap();

		for (RegistryKey<DimensionType> registryKey : DIMENSION_TYPES) {
			Pair<DimensionType, ChunkGenerator> pair = (Pair<DimensionType, ChunkGenerator>)map.get(registryKey);
			if (pair != null) {
				linkedHashMap.put(registryKey, pair);
			}
		}

		for (Entry<RegistryKey<DimensionType>, Pair<DimensionType, ChunkGenerator>> entry : map.entrySet()) {
			if (!DIMENSION_TYPES.contains(entry.getKey())) {
				linkedHashMap.put(entry.getKey(), entry.getValue());
			}
		}

		return linkedHashMap;
	}
}
