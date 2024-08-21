package net.minecraft.item.consume;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public interface ConsumeEffect {
	Codec<ConsumeEffect> CODEC = Registries.CONSUME_EFFECT_TYPE.getCodec().dispatch(ConsumeEffect::getType, ConsumeEffect.Type::codec);
	PacketCodec<RegistryByteBuf, ConsumeEffect> PACKET_CODEC = PacketCodecs.registryValue(RegistryKeys.CONSUME_EFFECT_TYPE)
		.dispatch(ConsumeEffect::getType, ConsumeEffect.Type::streamCodec);

	ConsumeEffect.Type<? extends ConsumeEffect> getType();

	boolean onConsume(World world, ItemStack stack, LivingEntity user);

	public static record Type<T extends ConsumeEffect>(MapCodec<T> codec, PacketCodec<RegistryByteBuf, T> streamCodec) {
		public static final ConsumeEffect.Type<ApplyEffectsConsumeEffect> APPLY_EFFECTS = register(
			"apply_effects", ApplyEffectsConsumeEffect.CODEC, ApplyEffectsConsumeEffect.PACKET_CODEC
		);
		public static final ConsumeEffect.Type<RemoveEffectsConsumeEffect> REMOVE_EFFECTS = register(
			"remove_effects", RemoveEffectsConsumeEffect.CODEC, RemoveEffectsConsumeEffect.PACKET_CODEC
		);
		public static final ConsumeEffect.Type<ClearAllEffectsConsumeEffect> CLEAR_ALL_EFFECTS = register(
			"clear_all_effects", ClearAllEffectsConsumeEffect.CODEC, ClearAllEffectsConsumeEffect.PACKET_CODEC
		);
		public static final ConsumeEffect.Type<TeleportRandomlyConsumeEffect> TELEPORT_RANDOMLY = register(
			"teleport_randomly", TeleportRandomlyConsumeEffect.CODEC, TeleportRandomlyConsumeEffect.PACKET_CODEC
		);
		public static final ConsumeEffect.Type<PlaySoundConsumeEffect> PLAY_SOUND = register(
			"play_sound", PlaySoundConsumeEffect.CODEC, PlaySoundConsumeEffect.PACKET_CODEC
		);

		private static <T extends ConsumeEffect> ConsumeEffect.Type<T> register(String id, MapCodec<T> codec, PacketCodec<RegistryByteBuf, T> packetCodec) {
			return Registry.register(Registries.CONSUME_EFFECT_TYPE, id, new ConsumeEffect.Type<>(codec, packetCodec));
		}
	}
}
