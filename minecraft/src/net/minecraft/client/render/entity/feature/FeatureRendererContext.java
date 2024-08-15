package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface FeatureRendererContext<S extends EntityRenderState, M extends EntityModel<? super S>> {
	M getModel();

	Identifier getTexture(S state);
}
