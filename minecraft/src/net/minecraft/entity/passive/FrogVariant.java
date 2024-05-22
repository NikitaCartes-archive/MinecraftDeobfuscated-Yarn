package net.minecraft.entity.passive;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public record FrogVariant(Identifier texture) {
	public static final PacketCodec<RegistryByteBuf, RegistryEntry<FrogVariant>> PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.FROG_VARIANT);
	public static final RegistryKey<FrogVariant> TEMPERATE = of("temperate");
	public static final RegistryKey<FrogVariant> WARM = of("warm");
	public static final RegistryKey<FrogVariant> COLD = of("cold");

	private static RegistryKey<FrogVariant> of(String id) {
		return RegistryKey.of(RegistryKeys.FROG_VARIANT, Identifier.ofVanilla(id));
	}

	public static FrogVariant registerAndGetDefault(Registry<FrogVariant> registry) {
		register(registry, TEMPERATE, "textures/entity/frog/temperate_frog.png");
		register(registry, WARM, "textures/entity/frog/warm_frog.png");
		return register(registry, COLD, "textures/entity/frog/cold_frog.png");
	}

	private static FrogVariant register(Registry<FrogVariant> registry, RegistryKey<FrogVariant> key, String id) {
		return Registry.register(registry, key, new FrogVariant(Identifier.ofVanilla(id)));
	}
}
