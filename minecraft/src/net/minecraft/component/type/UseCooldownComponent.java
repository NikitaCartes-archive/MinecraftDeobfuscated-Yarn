package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public record UseCooldownComponent(float seconds, Optional<Identifier> cooldownGroup) {
	public static final Codec<UseCooldownComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.POSITIVE_FLOAT.fieldOf("seconds").forGetter(UseCooldownComponent::seconds),
					Identifier.CODEC.optionalFieldOf("cooldown_group").forGetter(UseCooldownComponent::cooldownGroup)
				)
				.apply(instance, UseCooldownComponent::new)
	);
	public static final PacketCodec<RegistryByteBuf, UseCooldownComponent> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.FLOAT,
		UseCooldownComponent::seconds,
		Identifier.PACKET_CODEC.collect(PacketCodecs::optional),
		UseCooldownComponent::cooldownGroup,
		UseCooldownComponent::new
	);

	public UseCooldownComponent(float seconds) {
		this(seconds, Optional.empty());
	}

	public int getCooldownTicks() {
		return (int)(this.seconds * 20.0F);
	}

	public void set(ItemStack stack, LivingEntity user) {
		if (user instanceof PlayerEntity playerEntity) {
			playerEntity.getItemCooldownManager().set(stack, this.getCooldownTicks());
		}
	}
}
