package net.minecraft.enchantment;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.MathHelper;

public interface EnchantmentLevelBasedValueType {
	Codec<EnchantmentLevelBasedValueType> BASE_CODEC = Registries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE
		.getCodec()
		.dispatch(EnchantmentLevelBasedValueType::getCodec, codec -> codec);
	Codec<EnchantmentLevelBasedValueType> CODEC = Codec.either(EnchantmentLevelBasedValueType.Constant.CODEC, BASE_CODEC)
		.xmap(
			either -> either.map(type -> type, type -> type),
			type -> type instanceof EnchantmentLevelBasedValueType.Constant constant ? Either.left(constant) : Either.right(type)
		);

	static MapCodec<? extends EnchantmentLevelBasedValueType> registerAndGetDefault(Registry<MapCodec<? extends EnchantmentLevelBasedValueType>> registry) {
		Registry.register(registry, "clamped", EnchantmentLevelBasedValueType.Clamped.CODEC);
		Registry.register(registry, "fraction", EnchantmentLevelBasedValueType.Fraction.CODEC);
		Registry.register(registry, "levels_squared", EnchantmentLevelBasedValueType.LevelsSquared.CODEC);
		return Registry.register(registry, "linear", EnchantmentLevelBasedValueType.Linear.CODEC);
	}

	static EnchantmentLevelBasedValueType.Constant constant(float value) {
		return new EnchantmentLevelBasedValueType.Constant(value);
	}

	static EnchantmentLevelBasedValueType.Linear linear(float base, float perLevelAboveFirst) {
		return new EnchantmentLevelBasedValueType.Linear(base, perLevelAboveFirst);
	}

	static EnchantmentLevelBasedValueType.Linear linear(float base) {
		return linear(base, base);
	}

	float getValue(int level);

	MapCodec<? extends EnchantmentLevelBasedValueType> getCodec();

	public static record Clamped(EnchantmentLevelBasedValueType value, float min, float max) implements EnchantmentLevelBasedValueType {
		public static final MapCodec<EnchantmentLevelBasedValueType.Clamped> CODEC = RecordCodecBuilder.<EnchantmentLevelBasedValueType.Clamped>mapCodec(
				instance -> instance.group(
							EnchantmentLevelBasedValueType.CODEC.fieldOf("value").forGetter(EnchantmentLevelBasedValueType.Clamped::value),
							Codec.FLOAT.fieldOf("min").forGetter(EnchantmentLevelBasedValueType.Clamped::min),
							Codec.FLOAT.fieldOf("max").forGetter(EnchantmentLevelBasedValueType.Clamped::max)
						)
						.apply(instance, EnchantmentLevelBasedValueType.Clamped::new)
			)
			.validate(
				type -> type.max <= type.min ? DataResult.error(() -> "Max must be larger than min, min: " + type.min + ", max: " + type.max) : DataResult.success(type)
			);

		@Override
		public float getValue(int level) {
			return MathHelper.clamp(this.value.getValue(level), this.min, this.max);
		}

		@Override
		public MapCodec<EnchantmentLevelBasedValueType.Clamped> getCodec() {
			return CODEC;
		}
	}

	public static record Constant(float value) implements EnchantmentLevelBasedValueType {
		public static final Codec<EnchantmentLevelBasedValueType.Constant> CODEC = Codec.FLOAT
			.xmap(EnchantmentLevelBasedValueType.Constant::new, EnchantmentLevelBasedValueType.Constant::value);
		public static final MapCodec<EnchantmentLevelBasedValueType.Constant> TYPE_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(Codec.FLOAT.fieldOf("value").forGetter(EnchantmentLevelBasedValueType.Constant::value))
					.apply(instance, EnchantmentLevelBasedValueType.Constant::new)
		);

		@Override
		public float getValue(int level) {
			return this.value;
		}

		@Override
		public MapCodec<EnchantmentLevelBasedValueType.Constant> getCodec() {
			return TYPE_CODEC;
		}
	}

	public static record Fraction(EnchantmentLevelBasedValueType numerator, EnchantmentLevelBasedValueType denominator) implements EnchantmentLevelBasedValueType {
		public static final MapCodec<EnchantmentLevelBasedValueType.Fraction> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						EnchantmentLevelBasedValueType.CODEC.fieldOf("numerator").forGetter(EnchantmentLevelBasedValueType.Fraction::numerator),
						EnchantmentLevelBasedValueType.CODEC.fieldOf("denominator").forGetter(EnchantmentLevelBasedValueType.Fraction::denominator)
					)
					.apply(instance, EnchantmentLevelBasedValueType.Fraction::new)
		);

		@Override
		public float getValue(int level) {
			return this.numerator.getValue(level) / this.denominator.getValue(level);
		}

		@Override
		public MapCodec<EnchantmentLevelBasedValueType.Fraction> getCodec() {
			return CODEC;
		}
	}

	public static record LevelsSquared(float added) implements EnchantmentLevelBasedValueType {
		public static final MapCodec<EnchantmentLevelBasedValueType.LevelsSquared> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(Codec.FLOAT.fieldOf("added").forGetter(EnchantmentLevelBasedValueType.LevelsSquared::added))
					.apply(instance, EnchantmentLevelBasedValueType.LevelsSquared::new)
		);

		@Override
		public float getValue(int level) {
			return (float)MathHelper.square(level) + this.added;
		}

		@Override
		public MapCodec<EnchantmentLevelBasedValueType.LevelsSquared> getCodec() {
			return CODEC;
		}
	}

	public static record Linear(float base, float perLevelAboveFirst) implements EnchantmentLevelBasedValueType {
		public static final MapCodec<EnchantmentLevelBasedValueType.Linear> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Codec.FLOAT.fieldOf("base").forGetter(EnchantmentLevelBasedValueType.Linear::base),
						Codec.FLOAT.fieldOf("per_level_above_first").forGetter(EnchantmentLevelBasedValueType.Linear::perLevelAboveFirst)
					)
					.apply(instance, EnchantmentLevelBasedValueType.Linear::new)
		);

		@Override
		public float getValue(int level) {
			return this.base + this.perLevelAboveFirst * (float)(level - 1);
		}

		@Override
		public MapCodec<EnchantmentLevelBasedValueType.Linear> getCodec() {
			return CODEC;
		}
	}
}
