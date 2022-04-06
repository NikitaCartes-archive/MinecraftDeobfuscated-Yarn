package net.minecraft.entity.passive;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public record CatVariant(Identifier texture) {
	public static final CatVariant TABBY = register("tabby", "textures/entity/cat/tabby.png");
	public static final CatVariant BLACK = register("black", "textures/entity/cat/black.png");
	public static final CatVariant RED = register("red", "textures/entity/cat/red.png");
	public static final CatVariant SIAMESE = register("siamese", "textures/entity/cat/siamese.png");
	public static final CatVariant BRITISH = register("british", "textures/entity/cat/british_shorthair.png");
	public static final CatVariant CALICO = register("calico", "textures/entity/cat/calico.png");
	public static final CatVariant PERSIAN = register("persian", "textures/entity/cat/persian.png");
	public static final CatVariant RAGDOLL = register("ragdoll", "textures/entity/cat/ragdoll.png");
	public static final CatVariant WHITE = register("white", "textures/entity/cat/white.png");
	public static final CatVariant JELLIE = register("jellie", "textures/entity/cat/jellie.png");
	public static final CatVariant ALL_BLACK = register("all_black", "textures/entity/cat/all_black.png");

	private static CatVariant register(String id, String textureId) {
		return Registry.register(Registry.CAT_VARIANT, id, new CatVariant(new Identifier(textureId)));
	}
}
