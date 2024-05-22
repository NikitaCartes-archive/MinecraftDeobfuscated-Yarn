package net.minecraft.enchantment.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Function;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public interface AllOfEnchantmentEffects {
	static <T, A extends T> MapCodec<A> buildCodec(Codec<T> baseCodec, Function<List<T>, A> fromList, Function<A, List<T>> toList) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(baseCodec.listOf().fieldOf("effects").forGetter(toList)).apply(instance, fromList));
	}

	static AllOfEnchantmentEffects.EntityEffects allOf(EnchantmentEntityEffect... entityEffects) {
		return new AllOfEnchantmentEffects.EntityEffects(List.of(entityEffects));
	}

	static AllOfEnchantmentEffects.LocationBasedEffects allOf(EnchantmentLocationBasedEffect... locationBasedEffects) {
		return new AllOfEnchantmentEffects.LocationBasedEffects(List.of(locationBasedEffects));
	}

	static AllOfEnchantmentEffects.ValueEffects allOf(EnchantmentValueEffect... valueEffects) {
		return new AllOfEnchantmentEffects.ValueEffects(List.of(valueEffects));
	}

	public static record EntityEffects(List<EnchantmentEntityEffect> effects) implements EnchantmentEntityEffect {
		public static final MapCodec<AllOfEnchantmentEffects.EntityEffects> CODEC = AllOfEnchantmentEffects.buildCodec(
			EnchantmentEntityEffect.CODEC, AllOfEnchantmentEffects.EntityEffects::new, AllOfEnchantmentEffects.EntityEffects::effects
		);

		@Override
		public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
			for (EnchantmentEntityEffect enchantmentEntityEffect : this.effects) {
				enchantmentEntityEffect.apply(world, level, context, user, pos);
			}
		}

		@Override
		public MapCodec<AllOfEnchantmentEffects.EntityEffects> getCodec() {
			return CODEC;
		}
	}

	public static record LocationBasedEffects(List<EnchantmentLocationBasedEffect> effects) implements EnchantmentLocationBasedEffect {
		public static final MapCodec<AllOfEnchantmentEffects.LocationBasedEffects> CODEC = AllOfEnchantmentEffects.buildCodec(
			EnchantmentLocationBasedEffect.CODEC, AllOfEnchantmentEffects.LocationBasedEffects::new, AllOfEnchantmentEffects.LocationBasedEffects::effects
		);

		@Override
		public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos, boolean newlyApplied) {
			for (EnchantmentLocationBasedEffect enchantmentLocationBasedEffect : this.effects) {
				enchantmentLocationBasedEffect.apply(world, level, context, user, pos, newlyApplied);
			}
		}

		@Override
		public void remove(EnchantmentEffectContext context, Entity user, Vec3d pos, int level) {
			for (EnchantmentLocationBasedEffect enchantmentLocationBasedEffect : this.effects) {
				enchantmentLocationBasedEffect.remove(context, user, pos, level);
			}
		}

		@Override
		public MapCodec<AllOfEnchantmentEffects.LocationBasedEffects> getCodec() {
			return CODEC;
		}
	}

	public static record ValueEffects(List<EnchantmentValueEffect> effects) implements EnchantmentValueEffect {
		public static final MapCodec<AllOfEnchantmentEffects.ValueEffects> CODEC = AllOfEnchantmentEffects.buildCodec(
			EnchantmentValueEffect.CODEC, AllOfEnchantmentEffects.ValueEffects::new, AllOfEnchantmentEffects.ValueEffects::effects
		);

		@Override
		public float apply(int level, Random random, float inputValue) {
			for (EnchantmentValueEffect enchantmentValueEffect : this.effects) {
				inputValue = enchantmentValueEffect.apply(level, random, inputValue);
			}

			return inputValue;
		}

		@Override
		public MapCodec<AllOfEnchantmentEffects.ValueEffects> getCodec() {
			return CODEC;
		}
	}
}
