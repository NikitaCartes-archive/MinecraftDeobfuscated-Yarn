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

public interface AllOfEnchantmentEffectTypes {
	static <T, A extends T> MapCodec<A> buildCodec(Codec<T> baseCodec, Function<List<T>, A> fromList, Function<A, List<T>> toList) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(baseCodec.listOf().fieldOf("effects").forGetter(toList)).apply(instance, fromList));
	}

	static AllOfEnchantmentEffectTypes.EntityEffects allOf(EnchantmentEntityEffectType... entityEffects) {
		return new AllOfEnchantmentEffectTypes.EntityEffects(List.of(entityEffects));
	}

	static AllOfEnchantmentEffectTypes.LocationBasedEffects allOf(EnchantmentLocationBasedEffectType... locationBasedEffects) {
		return new AllOfEnchantmentEffectTypes.LocationBasedEffects(List.of(locationBasedEffects));
	}

	static AllOfEnchantmentEffectTypes.ValueEffects allOf(EnchantmentValueEffectType... valueEffects) {
		return new AllOfEnchantmentEffectTypes.ValueEffects(List.of(valueEffects));
	}

	public static record EntityEffects(List<EnchantmentEntityEffectType> effects) implements EnchantmentEntityEffectType {
		public static final MapCodec<AllOfEnchantmentEffectTypes.EntityEffects> CODEC = AllOfEnchantmentEffectTypes.buildCodec(
			EnchantmentEntityEffectType.CODEC, AllOfEnchantmentEffectTypes.EntityEffects::new, AllOfEnchantmentEffectTypes.EntityEffects::effects
		);

		@Override
		public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
			for (EnchantmentEntityEffectType enchantmentEntityEffectType : this.effects) {
				enchantmentEntityEffectType.apply(world, level, context, user, pos);
			}
		}

		@Override
		public MapCodec<AllOfEnchantmentEffectTypes.EntityEffects> getCodec() {
			return CODEC;
		}
	}

	public static record LocationBasedEffects(List<EnchantmentLocationBasedEffectType> effects) implements EnchantmentLocationBasedEffectType {
		public static final MapCodec<AllOfEnchantmentEffectTypes.LocationBasedEffects> CODEC = AllOfEnchantmentEffectTypes.buildCodec(
			EnchantmentLocationBasedEffectType.CODEC, AllOfEnchantmentEffectTypes.LocationBasedEffects::new, AllOfEnchantmentEffectTypes.LocationBasedEffects::effects
		);

		@Override
		public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos, boolean newlyApplied) {
			for (EnchantmentLocationBasedEffectType enchantmentLocationBasedEffectType : this.effects) {
				enchantmentLocationBasedEffectType.apply(world, level, context, user, pos, newlyApplied);
			}
		}

		@Override
		public void remove(EnchantmentEffectContext context, Entity user, Vec3d pos, int level) {
			for (EnchantmentLocationBasedEffectType enchantmentLocationBasedEffectType : this.effects) {
				enchantmentLocationBasedEffectType.remove(context, user, pos, level);
			}
		}

		@Override
		public MapCodec<AllOfEnchantmentEffectTypes.LocationBasedEffects> getCodec() {
			return CODEC;
		}
	}

	public static record ValueEffects(List<EnchantmentValueEffectType> effects) implements EnchantmentValueEffectType {
		public static final MapCodec<AllOfEnchantmentEffectTypes.ValueEffects> CODEC = AllOfEnchantmentEffectTypes.buildCodec(
			EnchantmentValueEffectType.CODEC, AllOfEnchantmentEffectTypes.ValueEffects::new, AllOfEnchantmentEffectTypes.ValueEffects::effects
		);

		@Override
		public float apply(int i, Random random, float f) {
			for (EnchantmentValueEffectType enchantmentValueEffectType : this.effects) {
				f = enchantmentValueEffectType.apply(i, random, f);
			}

			return f;
		}

		@Override
		public MapCodec<AllOfEnchantmentEffectTypes.ValueEffects> getCodec() {
			return CODEC;
		}
	}
}
