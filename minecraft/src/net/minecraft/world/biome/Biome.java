package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.LightType;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public final class Biome {
	public static final Codec<Biome> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Biome.Weather.CODEC.forGetter(biome -> biome.weather),
					Biome.Category.CODEC.fieldOf("category").forGetter(biome -> biome.category),
					BiomeEffects.CODEC.fieldOf("effects").forGetter(biome -> biome.effects),
					GenerationSettings.CODEC.forGetter(biome -> biome.generationSettings),
					SpawnSettings.CODEC.forGetter(biome -> biome.spawnSettings)
				)
				.apply(instance, Biome::new)
	);
	public static final Codec<Biome> field_26633 = RecordCodecBuilder.create(
		instance -> instance.group(
					Biome.Weather.CODEC.forGetter(biome -> biome.weather),
					Biome.Category.CODEC.fieldOf("category").forGetter(biome -> biome.category),
					BiomeEffects.CODEC.fieldOf("effects").forGetter(biome -> biome.effects)
				)
				.apply(instance, (weather, category, effects) -> new Biome(weather, category, effects, GenerationSettings.INSTANCE, SpawnSettings.INSTANCE))
	);
	public static final Codec<RegistryEntry<Biome>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.BIOME_KEY, CODEC);
	public static final Codec<RegistryEntryList<Biome>> field_26750 = RegistryCodecs.entryList(Registry.BIOME_KEY, CODEC);
	private static final OctaveSimplexNoiseSampler TEMPERATURE_NOISE = new OctaveSimplexNoiseSampler(
		new ChunkRandom(new AtomicSimpleRandom(1234L)), ImmutableList.of(0)
	);
	static final OctaveSimplexNoiseSampler FROZEN_OCEAN_NOISE = new OctaveSimplexNoiseSampler(
		new ChunkRandom(new AtomicSimpleRandom(3456L)), ImmutableList.of(-2, -1, 0)
	);
	@Deprecated(
		forRemoval = true
	)
	public static final OctaveSimplexNoiseSampler FOLIAGE_NOISE = new OctaveSimplexNoiseSampler(
		new ChunkRandom(new AtomicSimpleRandom(2345L)), ImmutableList.of(0)
	);
	private static final int field_30978 = 1024;
	private final Biome.Weather weather;
	private final GenerationSettings generationSettings;
	private final SpawnSettings spawnSettings;
	private final Biome.Category category;
	private final BiomeEffects effects;
	private final ThreadLocal<Long2FloatLinkedOpenHashMap> temperatureCache = ThreadLocal.withInitial(() -> Util.make(() -> {
			Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(1024, 0.25F) {
				@Override
				protected void rehash(int i) {
				}
			};
			long2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
			return long2FloatLinkedOpenHashMap;
		}));

	Biome(Biome.Weather weather, Biome.Category category, BiomeEffects effects, GenerationSettings generationSettings, SpawnSettings spawnSettings) {
		this.weather = weather;
		this.generationSettings = generationSettings;
		this.spawnSettings = spawnSettings;
		this.category = category;
		this.effects = effects;
	}

	public int getSkyColor() {
		return this.effects.getSkyColor();
	}

	public SpawnSettings getSpawnSettings() {
		return this.spawnSettings;
	}

	public Biome.Precipitation getPrecipitation() {
		return this.weather.precipitation;
	}

	public boolean hasHighHumidity() {
		return this.getDownfall() > 0.85F;
	}

	private float computeTemperature(BlockPos pos) {
		float f = this.weather.temperatureModifier.getModifiedTemperature(pos, this.getTemperature());
		if (pos.getY() > 80) {
			float g = (float)(TEMPERATURE_NOISE.sample((double)((float)pos.getX() / 8.0F), (double)((float)pos.getZ() / 8.0F), false) * 8.0);
			return f - (g + (float)pos.getY() - 80.0F) * 0.05F / 40.0F;
		} else {
			return f;
		}
	}

	@Deprecated
	private float getTemperature(BlockPos blockPos) {
		long l = blockPos.asLong();
		Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = (Long2FloatLinkedOpenHashMap)this.temperatureCache.get();
		float f = long2FloatLinkedOpenHashMap.get(l);
		if (!Float.isNaN(f)) {
			return f;
		} else {
			float g = this.computeTemperature(blockPos);
			if (long2FloatLinkedOpenHashMap.size() == 1024) {
				long2FloatLinkedOpenHashMap.removeFirstFloat();
			}

			long2FloatLinkedOpenHashMap.put(l, g);
			return g;
		}
	}

	public boolean canSetIce(WorldView world, BlockPos blockPos) {
		return this.canSetIce(world, blockPos, true);
	}

	public boolean canSetIce(WorldView world, BlockPos pos, boolean doWaterCheck) {
		if (this.doesNotSnow(pos)) {
			return false;
		} else {
			if (pos.getY() >= world.getBottomY() && pos.getY() < world.getTopY() && world.getLightLevel(LightType.BLOCK, pos) < 10) {
				BlockState blockState = world.getBlockState(pos);
				FluidState fluidState = world.getFluidState(pos);
				if (fluidState.getFluid() == Fluids.WATER && blockState.getBlock() instanceof FluidBlock) {
					if (!doWaterCheck) {
						return true;
					}

					boolean bl = world.isWater(pos.west()) && world.isWater(pos.east()) && world.isWater(pos.north()) && world.isWater(pos.south());
					if (!bl) {
						return true;
					}
				}
			}

			return false;
		}
	}

	public boolean isCold(BlockPos pos) {
		return !this.doesNotSnow(pos);
	}

	public boolean doesNotSnow(BlockPos pos) {
		return this.getTemperature(pos) >= 0.15F;
	}

	public boolean shouldGenerateLowerFrozenOceanSurface(BlockPos pos) {
		return this.getTemperature(pos) > 0.1F;
	}

	public boolean isHot(BlockPos pos) {
		return this.getTemperature(pos) > 1.0F;
	}

	public boolean canSetSnow(WorldView world, BlockPos pos) {
		if (this.doesNotSnow(pos)) {
			return false;
		} else {
			if (pos.getY() >= world.getBottomY() && pos.getY() < world.getTopY() && world.getLightLevel(LightType.BLOCK, pos) < 10) {
				BlockState blockState = world.getBlockState(pos);
				if (blockState.isAir() && Blocks.SNOW.getDefaultState().canPlaceAt(world, pos)) {
					return true;
				}
			}

			return false;
		}
	}

	public GenerationSettings getGenerationSettings() {
		return this.generationSettings;
	}

	public int getFogColor() {
		return this.effects.getFogColor();
	}

	public int getGrassColorAt(double x, double z) {
		int i = (Integer)this.effects.getGrassColor().orElseGet(this::getDefaultGrassColor);
		return this.effects.getGrassColorModifier().getModifiedGrassColor(x, z, i);
	}

	private int getDefaultGrassColor() {
		double d = (double)MathHelper.clamp(this.weather.temperature, 0.0F, 1.0F);
		double e = (double)MathHelper.clamp(this.weather.downfall, 0.0F, 1.0F);
		return GrassColors.getColor(d, e);
	}

	public int getFoliageColor() {
		return (Integer)this.effects.getFoliageColor().orElseGet(this::getDefaultFoliageColor);
	}

	private int getDefaultFoliageColor() {
		double d = (double)MathHelper.clamp(this.weather.temperature, 0.0F, 1.0F);
		double e = (double)MathHelper.clamp(this.weather.downfall, 0.0F, 1.0F);
		return FoliageColors.getColor(d, e);
	}

	public final float getDownfall() {
		return this.weather.downfall;
	}

	public final float getTemperature() {
		return this.weather.temperature;
	}

	public BiomeEffects getEffects() {
		return this.effects;
	}

	public final int getWaterColor() {
		return this.effects.getWaterColor();
	}

	public final int getWaterFogColor() {
		return this.effects.getWaterFogColor();
	}

	public Optional<BiomeParticleConfig> getParticleConfig() {
		return this.effects.getParticleConfig();
	}

	public Optional<SoundEvent> getLoopSound() {
		return this.effects.getLoopSound();
	}

	public Optional<BiomeMoodSound> getMoodSound() {
		return this.effects.getMoodSound();
	}

	public Optional<BiomeAdditionsSound> getAdditionsSound() {
		return this.effects.getAdditionsSound();
	}

	public Optional<MusicSound> getMusic() {
		return this.effects.getMusic();
	}

	Biome.Category getCategory() {
		return this.category;
	}

	@Deprecated
	public static Biome.Category getCategory(RegistryEntry<Biome> biomeEntry) {
		return biomeEntry.value().getCategory();
	}

	public static class Builder {
		@Nullable
		private Biome.Precipitation precipitation;
		@Nullable
		private Biome.Category category;
		@Nullable
		private Float temperature;
		private Biome.TemperatureModifier temperatureModifier = Biome.TemperatureModifier.NONE;
		@Nullable
		private Float downfall;
		@Nullable
		private BiomeEffects specialEffects;
		@Nullable
		private SpawnSettings spawnSettings;
		@Nullable
		private GenerationSettings generationSettings;

		public static Biome.Builder copy(Biome biome) {
			return new Biome.Builder()
				.precipitation(biome.getPrecipitation())
				.category(biome.getCategory())
				.temperature(biome.getTemperature())
				.downfall(biome.getDownfall())
				.effects(biome.getEffects())
				.generationSettings(biome.getGenerationSettings())
				.spawnSettings(biome.getSpawnSettings());
		}

		public Biome.Builder precipitation(Biome.Precipitation precipitation) {
			this.precipitation = precipitation;
			return this;
		}

		public Biome.Builder category(Biome.Category category) {
			this.category = category;
			return this;
		}

		public Biome.Builder temperature(float temperature) {
			this.temperature = temperature;
			return this;
		}

		public Biome.Builder downfall(float downfall) {
			this.downfall = downfall;
			return this;
		}

		public Biome.Builder effects(BiomeEffects effects) {
			this.specialEffects = effects;
			return this;
		}

		public Biome.Builder spawnSettings(SpawnSettings spawnSettings) {
			this.spawnSettings = spawnSettings;
			return this;
		}

		public Biome.Builder generationSettings(GenerationSettings generationSettings) {
			this.generationSettings = generationSettings;
			return this;
		}

		public Biome.Builder temperatureModifier(Biome.TemperatureModifier temperatureModifier) {
			this.temperatureModifier = temperatureModifier;
			return this;
		}

		public Biome build() {
			if (this.precipitation != null
				&& this.category != null
				&& this.temperature != null
				&& this.downfall != null
				&& this.specialEffects != null
				&& this.spawnSettings != null
				&& this.generationSettings != null) {
				return new Biome(
					new Biome.Weather(this.precipitation, this.temperature, this.temperatureModifier, this.downfall),
					this.category,
					this.specialEffects,
					this.generationSettings,
					this.spawnSettings
				);
			} else {
				throw new IllegalStateException("You are missing parameters to build a proper biome\n" + this);
			}
		}

		public String toString() {
			return "BiomeBuilder{\nprecipitation="
				+ this.precipitation
				+ ",\nbiomeCategory="
				+ this.category
				+ ",\ntemperature="
				+ this.temperature
				+ ",\ntemperatureModifier="
				+ this.temperatureModifier
				+ ",\ndownfall="
				+ this.downfall
				+ ",\nspecialEffects="
				+ this.specialEffects
				+ ",\nmobSpawnSettings="
				+ this.spawnSettings
				+ ",\ngenerationSettings="
				+ this.generationSettings
				+ ",\n}";
		}
	}

	public static enum Category implements StringIdentifiable {
		NONE("none"),
		TAIGA("taiga"),
		EXTREME_HILLS("extreme_hills"),
		JUNGLE("jungle"),
		MESA("mesa"),
		PLAINS("plains"),
		SAVANNA("savanna"),
		ICY("icy"),
		THEEND("the_end"),
		BEACH("beach"),
		FOREST("forest"),
		OCEAN("ocean"),
		DESERT("desert"),
		RIVER("river"),
		SWAMP("swamp"),
		MUSHROOM("mushroom"),
		NETHER("nether"),
		UNDERGROUND("underground"),
		MOUNTAIN("mountain");

		public static final Codec<Biome.Category> CODEC = StringIdentifiable.createCodec(Biome.Category::values, Biome.Category::byName);
		private static final Map<String, Biome.Category> BY_NAME = (Map<String, Biome.Category>)Arrays.stream(values())
			.collect(Collectors.toMap(Biome.Category::getName, category -> category));
		private final String name;

		private Category(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public static Biome.Category byName(String name) {
			return (Biome.Category)BY_NAME.get(name);
		}

		@Override
		public String asString() {
			return this.name;
		}
	}

	public static enum Precipitation implements StringIdentifiable {
		NONE("none"),
		RAIN("rain"),
		SNOW("snow");

		public static final Codec<Biome.Precipitation> CODEC = StringIdentifiable.createCodec(Biome.Precipitation::values, Biome.Precipitation::byName);
		private static final Map<String, Biome.Precipitation> BY_NAME = (Map<String, Biome.Precipitation>)Arrays.stream(values())
			.collect(Collectors.toMap(Biome.Precipitation::getName, precipitation -> precipitation));
		private final String name;

		private Precipitation(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public static Biome.Precipitation byName(String name) {
			return (Biome.Precipitation)BY_NAME.get(name);
		}

		@Override
		public String asString() {
			return this.name;
		}
	}

	public static enum TemperatureModifier implements StringIdentifiable {
		NONE("none") {
			@Override
			public float getModifiedTemperature(BlockPos pos, float temperature) {
				return temperature;
			}
		},
		FROZEN("frozen") {
			@Override
			public float getModifiedTemperature(BlockPos pos, float temperature) {
				double d = Biome.FROZEN_OCEAN_NOISE.sample((double)pos.getX() * 0.05, (double)pos.getZ() * 0.05, false) * 7.0;
				double e = Biome.FOLIAGE_NOISE.sample((double)pos.getX() * 0.2, (double)pos.getZ() * 0.2, false);
				double f = d + e;
				if (f < 0.3) {
					double g = Biome.FOLIAGE_NOISE.sample((double)pos.getX() * 0.09, (double)pos.getZ() * 0.09, false);
					if (g < 0.8) {
						return 0.2F;
					}
				}

				return temperature;
			}
		};

		private final String name;
		public static final Codec<Biome.TemperatureModifier> CODEC = StringIdentifiable.createCodec(
			Biome.TemperatureModifier::values, Biome.TemperatureModifier::byName
		);
		private static final Map<String, Biome.TemperatureModifier> BY_NAME = (Map<String, Biome.TemperatureModifier>)Arrays.stream(values())
			.collect(Collectors.toMap(Biome.TemperatureModifier::getName, temperatureModifier -> temperatureModifier));

		public abstract float getModifiedTemperature(BlockPos pos, float temperature);

		TemperatureModifier(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		@Override
		public String asString() {
			return this.name;
		}

		public static Biome.TemperatureModifier byName(String name) {
			return (Biome.TemperatureModifier)BY_NAME.get(name);
		}
	}

	static class Weather {
		public static final MapCodec<Biome.Weather> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Biome.Precipitation.CODEC.fieldOf("precipitation").forGetter(weather -> weather.precipitation),
						Codec.FLOAT.fieldOf("temperature").forGetter(weather -> weather.temperature),
						Biome.TemperatureModifier.CODEC.optionalFieldOf("temperature_modifier", Biome.TemperatureModifier.NONE).forGetter(weather -> weather.temperatureModifier),
						Codec.FLOAT.fieldOf("downfall").forGetter(weather -> weather.downfall)
					)
					.apply(instance, Biome.Weather::new)
		);
		final Biome.Precipitation precipitation;
		final float temperature;
		final Biome.TemperatureModifier temperatureModifier;
		final float downfall;

		Weather(Biome.Precipitation precipitation, float temperature, Biome.TemperatureModifier temperatureModifier, float downfall) {
			this.precipitation = precipitation;
			this.temperature = temperature;
			this.temperatureModifier = temperatureModifier;
			this.downfall = downfall;
		}
	}
}
