package net.minecraft.enchantment.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.random.Random;

public record PlaySoundEnchantmentEffect(RegistryEntry<SoundEvent> soundEvent, FloatProvider volume, FloatProvider pitch) implements EnchantmentEntityEffect {
	public static final MapCodec<PlaySoundEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					SoundEvent.ENTRY_CODEC.fieldOf("sound").forGetter(PlaySoundEnchantmentEffect::soundEvent),
					FloatProvider.createValidatedCodec(1.0E-5F, 10.0F).fieldOf("volume").forGetter(PlaySoundEnchantmentEffect::volume),
					FloatProvider.createValidatedCodec(1.0E-5F, 2.0F).fieldOf("pitch").forGetter(PlaySoundEnchantmentEffect::pitch)
				)
				.apply(instance, PlaySoundEnchantmentEffect::new)
	);

	@Override
	public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
		Random random = user.getRandom();
		if (!user.isSilent()) {
			world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), this.soundEvent, user.getSoundCategory(), this.volume.get(random), this.pitch.get(random));
		}
	}

	@Override
	public MapCodec<PlaySoundEnchantmentEffect> getCodec() {
		return CODEC;
	}
}
