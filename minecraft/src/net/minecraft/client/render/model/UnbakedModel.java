package net.minecraft.client.render.model;

import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4730;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface UnbakedModel {
	Collection<Identifier> getModelDependencies();

	Collection<class_4730> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences);

	@Nullable
	BakedModel bake(ModelLoader loader, Function<class_4730, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId);
}
