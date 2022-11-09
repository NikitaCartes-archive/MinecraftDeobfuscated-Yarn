package net.minecraft.entity.passive;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public record CatVariant(Identifier texture) {
	public static final RegistryKey<CatVariant> TABBY = of("tabby");
	public static final RegistryKey<CatVariant> BLACK = of("black");
	public static final RegistryKey<CatVariant> RED = of("red");
	public static final RegistryKey<CatVariant> SIAMESE = of("siamese");
	public static final RegistryKey<CatVariant> BRITISH_SHORTHAIR = of("british_shorthair");
	public static final RegistryKey<CatVariant> CALICO = of("calico");
	public static final RegistryKey<CatVariant> PERSIAN = of("persian");
	public static final RegistryKey<CatVariant> RAGDOLL = of("ragdoll");
	public static final RegistryKey<CatVariant> WHITE = of("white");
	public static final RegistryKey<CatVariant> JELLIE = of("jellie");
	public static final RegistryKey<CatVariant> ALL_BLACK = of("all_black");

	private static RegistryKey<CatVariant> of(String id) {
		return RegistryKey.of(RegistryKeys.CAT_VARIANT, new Identifier(id));
	}

	public static CatVariant registerAndGetDefault(Registry<CatVariant> registry) {
		register(registry, TABBY, "textures/entity/cat/tabby.png");
		register(registry, BLACK, "textures/entity/cat/black.png");
		register(registry, RED, "textures/entity/cat/red.png");
		register(registry, SIAMESE, "textures/entity/cat/siamese.png");
		register(registry, BRITISH_SHORTHAIR, "textures/entity/cat/british_shorthair.png");
		register(registry, CALICO, "textures/entity/cat/calico.png");
		register(registry, PERSIAN, "textures/entity/cat/persian.png");
		register(registry, RAGDOLL, "textures/entity/cat/ragdoll.png");
		register(registry, WHITE, "textures/entity/cat/white.png");
		register(registry, JELLIE, "textures/entity/cat/jellie.png");
		return register(registry, ALL_BLACK, "textures/entity/cat/all_black.png");
	}

	private static CatVariant register(Registry<CatVariant> registry, RegistryKey<CatVariant> key, String textureId) {
		return Registry.register(registry, key, new CatVariant(new Identifier(textureId)));
	}
}
