package net.minecraft.enchantment;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.MathHelper;

public interface EnchantmentLevelBasedValue {
	Codec<EnchantmentLevelBasedValue> BASE_CODEC = Registries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE
		.getCodec()
		.dispatch(EnchantmentLevelBasedValue::getCodec, codec -> codec);
	Codec<EnchantmentLevelBasedValue> CODEC = Codec.either(EnchantmentLevelBasedValue.Constant.CODEC, BASE_CODEC)
		.xmap(
			either -> either.map(value -> value, value -> value),
			value -> value instanceof EnchantmentLevelBasedValue.Constant constant ? Either.left(constant) : Either.right(value)
		);

	static MapCodec<? extends EnchantmentLevelBasedValue> registerAndGetDefault(Registry<MapCodec<? extends EnchantmentLevelBasedValue>> registry) {
		Registry.register(registry, "clamped", EnchantmentLevelBasedValue.Clamped.CODEC);
		Registry.register(registry, "fraction", EnchantmentLevelBasedValue.Fraction.CODEC);
		Registry.register(registry, "levels_squared", EnchantmentLevelBasedValue.LevelsSquared.CODEC);
		return Registry.register(registry, "linear", EnchantmentLevelBasedValue.Linear.CODEC);
	}

	static EnchantmentLevelBasedValue.Constant constant(float value) {
		return new EnchantmentLevelBasedValue.Constant(value);
	}

	static EnchantmentLevelBasedValue.Linear linear(float base, float perLevelAboveFirst) {
		return new EnchantmentLevelBasedValue.Linear(base, perLevelAboveFirst);
	}

	static EnchantmentLevelBasedValue.Linear linear(float base) {
		return linear(base, base);
	}

	float getValue(int level);

	MapCodec<? extends EnchantmentLevelBasedValue> getCodec();

	public static record Clamped(EnchantmentLevelBasedValue value, float min, float max) implements EnchantmentLevelBasedValue {
		public static final MapCodec<EnchantmentLevelBasedValue.Clamped> CODEC = RecordCodecBuilder.<EnchantmentLevelBasedValue.Clamped>mapCodec(
				instance -> instance.group(
							EnchantmentLevelBasedValue.CODEC.fieldOf("value").forGetter(EnchantmentLevelBasedValue.Clamped::value),
							Codec.FLOAT.fieldOf("min").forGetter(EnchantmentLevelBasedValue.Clamped::min),
							Codec.FLOAT.fieldOf("max").forGetter(EnchantmentLevelBasedValue.Clamped::max)
						)
						.apply(instance, EnchantmentLevelBasedValue.Clamped::new)
			)
			.validate(
				value -> value.max <= value.min
						? DataResult.error(() -> "Max must be larger than min, min: " + value.min + ", max: " + value.max)
						: DataResult.success(value)
			);

		@Override
		public float getValue(int level) {
			return MathHelper.clamp(this.value.getValue(level), this.min, this.max);
		}

		@Override
		public MapCodec<EnchantmentLevelBasedValue.Clamped> getCodec() {
			return CODEC;
		}
	}

	public static record Constant(float value) implements EnchantmentLevelBasedValue {
		public static final Codec<EnchantmentLevelBasedValue.Constant> CODEC = Codec.FLOAT
			.xmap(EnchantmentLevelBasedValue.Constant::new, EnchantmentLevelBasedValue.Constant::value);
		public static final MapCodec<EnchantmentLevelBasedValue.Constant> TYPE_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(Codec.FLOAT.fieldOf("value").forGetter(EnchantmentLevelBasedValue.Constant::value))
					.apply(instance, EnchantmentLevelBasedValue.Constant::new)
		);

		@Override
		public float getValue(int level) {
			return this.value;
		}

		@Override
		public MapCodec<EnchantmentLevelBasedValue.Constant> getCodec() {
			return TYPE_CODEC;
		}
	}

	public static record Fraction(EnchantmentLevelBasedValue numerator, EnchantmentLevelBasedValue denominator) implements EnchantmentLevelBasedValue {
		public static final MapCodec<EnchantmentLevelBasedValue.Fraction> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						EnchantmentLevelBasedValue.CODEC.fieldOf("numerator").forGetter(EnchantmentLevelBasedValue.Fraction::numerator),
						EnchantmentLevelBasedValue.CODEC.fieldOf("denominator").forGetter(EnchantmentLevelBasedValue.Fraction::denominator)
					)
					.apply(instance, EnchantmentLevelBasedValue.Fraction::new)
		);

		@Override
		public float getValue(int level) {
			float f = this.denominator.getValue(level);
			return f == 0.0F ? 0.0F : this.numerator.getValue(level) / f;
		}

		@Override
		public MapCodec<EnchantmentLevelBasedValue.Fraction> getCodec() {
			return CODEC;
		}
	}

	public static record LevelsSquared(float added) implements EnchantmentLevelBasedValue {
		public static final MapCodec<EnchantmentLevelBasedValue.LevelsSquared> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(Codec.FLOAT.fieldOf("added").forGetter(EnchantmentLevelBasedValue.LevelsSquared::added))
					.apply(instance, EnchantmentLevelBasedValue.LevelsSquared::new)
		);

		@Override
		public float getValue(int level) {
			return (float)MathHelper.square(level) + this.added;
		}

		@Override
		public MapCodec<EnchantmentLevelBasedValue.LevelsSquared> getCodec() {
			return CODEC;
		}
	}

	public static record Linear(float base, float perLevelAboveFirst) implements EnchantmentLevelBasedValue {
		public static final MapCodec<EnchantmentLevelBasedValue.Linear> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Codec.FLOAT.fieldOf("base").forGetter(EnchantmentLevelBasedValue.Linear::base),
						Codec.FLOAT.fieldOf("per_level_above_first").forGetter(EnchantmentLevelBasedValue.Linear::perLevelAboveFirst)
					)
					.apply(instance, EnchantmentLevelBasedValue.Linear::new)
		);

		@Override
		public float getValue(int level) {
			return this.base + this.perLevelAboveFirst * (float)(level - 1);
		}

		@Override
		public MapCodec<EnchantmentLevelBasedValue.Linear> getCodec() {
			return CODEC;
		}
	}
}
