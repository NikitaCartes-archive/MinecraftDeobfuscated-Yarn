package net.minecraft.world.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.file.Path;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class DimensionType {
	public static final int SIZE_BITS_Y = BlockPos.SIZE_BITS_Y;
	public static final int field_33411 = 16;
	public static final int MAX_HEIGHT = (1 << SIZE_BITS_Y) - 32;
	public static final int MAX_COLUMN_HEIGHT = (MAX_HEIGHT >> 1) - 1;
	public static final int MIN_HEIGHT = MAX_COLUMN_HEIGHT - MAX_HEIGHT + 1;
	public static final int field_35478 = MAX_COLUMN_HEIGHT << 4;
	public static final int field_35479 = MIN_HEIGHT << 4;
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
						Identifier.CODEC.fieldOf("effects").orElse(DimensionTypes.OVERWORLD_ID).forGetter(dimensionType -> dimensionType.effects),
						Codec.FLOAT.fieldOf("ambient_light").forGetter(dimensionType -> dimensionType.ambientLight)
					)
					.apply(instance, DimensionType::new)
		)
		.comapFlatMap(DimensionType::checkHeight, Function.identity());
	private static final int field_31440 = 8;
	public static final float[] MOON_SIZES = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
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

	public static double getCoordinateScaleFactor(DimensionType fromDimension, DimensionType toDimension) {
		double d = fromDimension.getCoordinateScale();
		double e = toDimension.getCoordinateScale();
		return d / e;
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
}
