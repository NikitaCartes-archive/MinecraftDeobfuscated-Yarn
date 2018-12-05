package net.minecraft.client.render.model;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface UnbakedModel {
	Collection<Identifier> getModelDependencies();

	Collection<Identifier> getTextureDependencies(Function<Identifier, UnbakedModel> function, Set<String> set);

	@Nullable
	BakedModel bake(ModelLoader modelLoader, Function<Identifier, Sprite> function, ModelRotationContainer modelRotationContainer);
}
