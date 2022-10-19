package net.minecraft.client.render.model;

import java.util.Collection;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface UnbakedModel {
	Collection<Identifier> getModelDependencies();

	void setParents(Function<Identifier, UnbakedModel> modelLoader);

	@Nullable
	BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId);
}
