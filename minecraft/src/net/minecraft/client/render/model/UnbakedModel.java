package net.minecraft.client.render.model;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface UnbakedModel {
	void resolve(UnbakedModel.Resolver resolver, UnbakedModel.ModelType currentlyResolvingType);

	@Nullable
	BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer);

	@Environment(EnvType.CLIENT)
	public static enum ModelType {
		TOP,
		OVERRIDE;
	}

	@Environment(EnvType.CLIENT)
	public interface Resolver {
		UnbakedModel resolve(Identifier id);

		UnbakedModel resolveOverride(Identifier id);
	}
}
