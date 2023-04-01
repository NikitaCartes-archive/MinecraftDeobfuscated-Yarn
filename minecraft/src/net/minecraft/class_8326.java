package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

public class class_8326 extends class_8294<class_8326.class_8327> {
	public void method_50357(ServerPlayerEntity serverPlayerEntity) {
		Registry<StatusEffect> registry = serverPlayerEntity.world.getRegistryManager().get(RegistryKeys.STATUS_EFFECT);

		for (class_8326.class_8327 lv : this.method_50257()) {
			StatusEffect statusEffect = registry.get(lv.effect);
			if (statusEffect != null) {
				StatusEffectInstance statusEffectInstance = new StatusEffectInstance(statusEffect, -1, lv.level, false, true);
				serverPlayerEntity.addStatusEffect(statusEffectInstance);
			}
		}
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		return Stream.generate(() -> minecraftServer.getRegistryManager().get(RegistryKeys.STATUS_EFFECT).getRandom(random))
			.limit(100L)
			.flatMap(Optional::stream)
			.filter(reference -> !((StatusEffect)reference.value()).isInstant())
			.map(reference -> {
				int ix = random.nextBetween(1, 5);
				return new class_8326.class_8327(reference.registryKey(), ix);
			})
			.limit((long)i)
			.map(object -> new class_8294.class_8295(object));
	}

	protected boolean method_50231(class_8326.class_8327 arg) {
		return super.method_50231(arg);
	}

	@Override
	protected Codec<class_8326.class_8327> method_50185() {
		return class_8326.class_8327.field_43870;
	}

	protected Text method_50187(class_8326.class_8327 arg) {
		RegistryKey<StatusEffect> registryKey = arg.effect;
		StatusEffect statusEffect = Registries.STATUS_EFFECT.get(registryKey);
		MutableText mutableText;
		if (statusEffect == null) {
			mutableText = Text.literal(registryKey.getValue().toString());
		} else {
			mutableText = Text.translatable(statusEffect.getTranslationKey());
		}

		if (arg.level > 0) {
			mutableText = Text.translatable("potion.withAmplifier", mutableText, Text.translatable("potion.potency." + arg.level));
		}

		return Text.translatable("rule.give_effect", mutableText);
	}

	@Override
	protected void method_50254(class_8290 arg, MinecraftServer minecraftServer) {
		if (arg == class_8290.APPROVE) {
			for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
				this.method_50357(serverPlayerEntity);
			}
		}
	}

	public static record class_8327(RegistryKey<StatusEffect> effect, int level) {
		public static final Codec<class_8326.class_8327> field_43870 = RecordCodecBuilder.create(
			instance -> instance.group(
						RegistryKey.createCodec(RegistryKeys.STATUS_EFFECT).fieldOf("effect").forGetter(class_8326.class_8327::effect),
						Codec.INT.fieldOf("level").forGetter(class_8326.class_8327::level)
					)
					.apply(instance, class_8326.class_8327::new)
		);
	}
}
