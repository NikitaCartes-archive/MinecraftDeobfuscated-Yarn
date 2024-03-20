package net.minecraft.entity.passive;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public record FrogVariant(Identifier texture) {
	public static final RegistryKey<FrogVariant> TEMPERATE = of("temperate");
	public static final RegistryKey<FrogVariant> WARM = of("warm");
	public static final RegistryKey<FrogVariant> COLD = of("cold");

	private static RegistryKey<FrogVariant> of(String id) {
		return RegistryKey.of(RegistryKeys.FROG_VARIANT, new Identifier(id));
	}

	public static FrogVariant registerAndGetDefault(Registry<FrogVariant> registry) {
		register(registry, TEMPERATE, "textures/entity/frog/temperate_frog.png");
		register(registry, WARM, "textures/entity/frog/warm_frog.png");
		return register(registry, COLD, "textures/entity/frog/cold_frog.png");
	}

	private static FrogVariant register(Registry<FrogVariant> registry, RegistryKey<FrogVariant> key, String id) {
		return Registry.register(registry, key, new FrogVariant(new Identifier(id)));
	}
}
