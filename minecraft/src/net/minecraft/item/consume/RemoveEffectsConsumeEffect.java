package net.minecraft.item.consume;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.world.World;

public record RemoveEffectsConsumeEffect(RegistryEntryList<StatusEffect> effects) implements ConsumeEffect {
	public static final MapCodec<RemoveEffectsConsumeEffect> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(RegistryCodecs.entryList(RegistryKeys.STATUS_EFFECT).fieldOf("effects").forGetter(RemoveEffectsConsumeEffect::effects))
				.apply(instance, RemoveEffectsConsumeEffect::new)
	);
	public static final PacketCodec<RegistryByteBuf, RemoveEffectsConsumeEffect> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.registryEntryList(RegistryKeys.STATUS_EFFECT), RemoveEffectsConsumeEffect::effects, RemoveEffectsConsumeEffect::new
	);

	public RemoveEffectsConsumeEffect(RegistryEntry<StatusEffect> effect) {
		this(RegistryEntryList.of(effect));
	}

	@Override
	public ConsumeEffect.Type<RemoveEffectsConsumeEffect> getType() {
		return ConsumeEffect.Type.REMOVE_EFFECTS;
	}

	@Override
	public boolean onConsume(World world, ItemStack stack, LivingEntity user) {
		boolean bl = false;

		for (RegistryEntry<StatusEffect> registryEntry : this.effects) {
			if (user.removeStatusEffect(registryEntry)) {
				bl = true;
			}
		}

		return bl;
	}
}
