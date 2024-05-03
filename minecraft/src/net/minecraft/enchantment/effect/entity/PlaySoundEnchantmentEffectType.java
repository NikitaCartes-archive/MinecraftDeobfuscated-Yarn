package net.minecraft.enchantment.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.EnchantmentEntityEffectType;
import net.minecraft.entity.Entity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.random.Random;

public record PlaySoundEnchantmentEffectType(RegistryEntry<SoundEvent> soundEvent, FloatProvider volume, FloatProvider pitch)
	implements EnchantmentEntityEffectType {
	public static final MapCodec<PlaySoundEnchantmentEffectType> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					SoundEvent.ENTRY_CODEC.fieldOf("sound").forGetter(PlaySoundEnchantmentEffectType::soundEvent),
					FloatProvider.createValidatedCodec(1.0E-5F, 10.0F).fieldOf("volume").forGetter(PlaySoundEnchantmentEffectType::volume),
					FloatProvider.createValidatedCodec(1.0E-5F, 2.0F).fieldOf("pitch").forGetter(PlaySoundEnchantmentEffectType::pitch)
				)
				.apply(instance, PlaySoundEnchantmentEffectType::new)
	);

	@Override
	public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
		Random random = user.getRandom();
		user.playSound(this.soundEvent.value(), this.volume.get(random), this.pitch.get(random));
	}

	@Override
	public MapCodec<PlaySoundEnchantmentEffectType> getCodec() {
		return CODEC;
	}
}
