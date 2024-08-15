package net.minecraft.client.render.model;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface Baker {
	UnbakedModel getModel(Identifier id);

	@Nullable
	BakedModel bake(Identifier id, ModelBakeSettings settings);
}
